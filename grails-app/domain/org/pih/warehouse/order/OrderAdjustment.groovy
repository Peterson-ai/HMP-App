/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/
package org.pih.warehouse.order

import grails.databinding.BindUsing
import grails.util.Holders
import org.pih.warehouse.EmptyStringsToNullBinder
import org.pih.warehouse.core.BudgetCode
import org.pih.warehouse.core.GlAccount
import org.pih.warehouse.invoice.InvoiceItem
import org.pih.warehouse.invoice.InvoiceType
import org.pih.warehouse.invoice.InvoiceTypeCode

class OrderAdjustment implements Serializable, Comparable<OrderAdjustment> {

    def publishRefreshEvent() {
        if (order?.isPurchaseOrder && !disableRefresh) {
            Holders.grailsApplication.mainContext.publishEvent(new RefreshOrderSummaryEvent(order))
        }
    }

    def afterUpdate() {
        publishRefreshEvent()
    }

    String id

    @BindUsing({ obj, source -> EmptyStringsToNullBinder.bindEmptyStringToNull(source, "amount")})
    BigDecimal amount

    @BindUsing({ obj, source -> EmptyStringsToNullBinder.bindEmptyStringToNull(source, "percentage")})
    BigDecimal percentage

    String description      // overrides description of order adjustment type

    String comments

    OrderAdjustmentType orderAdjustmentType

    BudgetCode budgetCode

    GlAccount glAccount

    // Audit fields
    Date dateCreated
    Date lastUpdated

    Boolean canceled = Boolean.FALSE

    Boolean disableRefresh = Boolean.TRUE

    static transients = [
        'totalAdjustments',
        'postedPurchaseInvoiceItems',
        'isInvoiced',
        "invoices",
        "hasInvoices",
        "hasPrepaymentInvoice",
        "hasRegularInvoice",
        "disableRefresh",
    ]

    static belongsTo = [order: Order, orderItem: OrderItem]

    static hasMany = [invoiceItems: InvoiceItem]

    static mapping = {
        id generator: 'uuid'
        invoiceItems joinTable: [name: 'order_adjustment_invoice', key: 'order_adjustment_id']
    }

    static constraints = {
        order(nullable:false)
        orderItem(nullable:true)
        orderAdjustmentType(nullable:true)
        amount(nullable:true)
        percentage(nullable:true)
        description(nullable:false, blank: false)
        comments(nullable: true)
        budgetCode(nullable: true)
        glAccount(nullable: true)
        canceled(nullable: true)
    }


    def getTotalAdjustments() {
        return amount ?: percentage ? orderItem ? orderItem?.subtotal * (percentage/100) : order.subtotal * (percentage/100) : 0
    }

    def getPostedPurchaseInvoiceItems() {
        invoiceItems?.findAll {
            it.invoice.datePosted != null && it.invoice.isRegularInvoice
        }
    }

    Boolean getIsInvoiced() {
        return !postedPurchaseInvoiceItems.empty
    }

    /**
     * Overall invoiced quantity for this adjustment. Expected is either 0 or 1. Should not be more than 1.
     * */
    Integer getInvoicedQuantity() {
        return invoiceItems?.findAll { it.invoice.isRegularInvoice && !it.inverse }?.sum { it.quantity } ?: 0
    }

    /**
     * Overall invoiced amount for this adjustment. Can be positive, negative or 0.
     * */
    BigDecimal getInvoicedAmount() {
        return invoiceItems?.findAll { it.invoice?.isRegularInvoice && !it.inverse }?.sum { it.amount } ?: 0
    }

    /**
     * Overall inversed quantity for this adjustment. Expected is either 0 or 1. Should not be more than 1.
     * */
    Integer getInversedQuantity() {
        return invoiceItems?.findAll { it.inverse }?.sum { it.quantity } ?: 0
    }

    /**
     * Overall inversed amount for this adjustment. Can be positive, negative or 0.
     * */
    BigDecimal getInversedAmount() {
        return invoiceItems.findAll { it.inverse }?.sum { it.amount } ?: 0
    }


    def getInvoices() {
        return invoiceItems*.invoice.unique()
    }

    /**
     * Adjustment can be on a regular invoice if it is not canceled or if it is canceled, but has prepayment
     * */
    Boolean canBeOnRegularInvoice() {
        if (canceled) {
            return hasPrepaymentInvoice
        }

        return true
    }

    /**
     * Adjustment is invoiceable on regular invoice if:
     *  - if adjustment is canceled, we can invoice if it has prepayment, does not have regular already and order is placed
     *  - if adjustment is not canceled, and it was previously invoiced as canceled on regular invoice
     *  - if adjustment has no regular invoice yet and order is placed
     * */
    Boolean isInvoiceable() {
        if (canceled) {
            return hasPrepaymentInvoice && !hasRegularInvoice && order.placed
        }

        // If is not canceled and has regular invoice, check if by any chance it was invoiced as cancelled
        if (!canceled && hasRegularInvoice) {
            return invoicedQuantity == 0 && order.placed
        }

        return !hasRegularInvoice && order.placed
    }

    Boolean getHasInvoices() {
        return !invoices.empty
    }

    Boolean getHasPrepaymentInvoice() {
        return invoices.any { it.invoiceType?.code == InvoiceTypeCode.PREPAYMENT_INVOICE }
    }

    Boolean getHasRegularInvoice() {
        return invoices.any { it.invoiceType == null || it.invoiceType?.code == InvoiceTypeCode.INVOICE }
    }

    /**
     * Should it be unit price instead amount? :thinking:
     * */
    BigDecimal getAmountAvailableToInvoice() {
        if (totalAdjustments == 0 || canceled) {
            return 0
        }

        return totalAdjustments - invoicedAmount
    }

    @Override
    int compareTo(OrderAdjustment o) {
        return dateCreated <=> o.dateCreated ?:
                lastUpdated <=> o.lastUpdated ?:
                        id <=> o.id
    }
}
