/**
 * Copyright (c) 2012 Partners In Health.  All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 **/
package org.pih.warehouse.invoice

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.apache.commons.csv.CSVPrinter
import org.grails.plugins.web.taglib.ApplicationTagLib
import org.hibernate.FetchMode
import org.hibernate.criterion.CriteriaSpecification
import org.pih.warehouse.core.Constants
import org.pih.warehouse.core.UnitOfMeasure
import org.pih.warehouse.importer.CSVUtils
import org.pih.warehouse.order.Order
import org.pih.warehouse.order.OrderAdjustment
import org.pih.warehouse.order.OrderItem
import org.pih.warehouse.order.OrderItemStatusCode
import org.pih.warehouse.product.Product
import org.pih.warehouse.shipping.ReferenceNumber
import org.pih.warehouse.shipping.ReferenceNumberType
import org.pih.warehouse.shipping.ShipmentItem
import org.joda.time.LocalDate

@Transactional
class InvoiceService {

    def authService
    def identifierService
    GrailsApplication grailsApplication

    ApplicationTagLib getApplicationTagLib() {
        return grailsApplication.mainContext.getBean(ApplicationTagLib)
    }

    List<InvoiceList> getInvoices(Map params, Boolean fetchItemsEagerly = false) {
        // Parse pagination parameters
        Integer max = params.format == "csv" ? null : params.int("max", 10)
        Integer offset = params.format == "csv" ? null : params.int("offset", 0)

        // Parse date parameters
        params.dateInvoiced = params.dateInvoiced ? Date.parse("MM/dd/yyyy", params.dateInvoiced) : null

        return InvoiceList.createCriteria().list(max: max, offset: offset) {
            resultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)

            if (fetchItemsEagerly) {
                fetchMode("invoice", FetchMode.JOIN)
                fetchMode("invoice.invoiceItems", FetchMode.JOIN)
            }

            eq("partyFromId", params.partyFromId)

            if (params.invoiceNumber) {
                ilike("invoiceNumber", "%" + params.invoiceNumber + "%")
            }

            if (params.status) {
                eq("status", params.status as InvoiceStatus)
            }

            if (params.vendor && params.vendor != "null") {
                eq("partyId", params.vendor)
            }

            if (params.invoiceTypeCode) {
                eq("invoiceTypeCode", params.invoiceTypeCode as InvoiceTypeCode)
            }

            if (params.dateInvoiced) {
                eq("dateInvoiced", params.dateInvoiced)
            }

            if (params.createdBy) {
                eq("createdById", params.createdBy)
            }

            if (params.sort) {
                order(params.sort, params.order ?: 'asc')
            } else {
                order("dateCreated", "desc")
            }
        }
    }

    List<InvoiceItem> getInvoiceItems(String id, String max, String offset) {
        Invoice invoice = Invoice.get(id)

        if (!invoice) {
            return []
        }

        List <InvoiceItem> invoiceItems
        if (max != null && offset != null) {
            invoiceItems = InvoiceItem.createCriteria().list(max: max.toInteger(), offset: offset.toInteger()) {
                eq("invoice", invoice)
            }
        } else {
            invoiceItems = InvoiceItem.createCriteria().list() {
                eq("invoice", invoice)
            }
        }

        return invoiceItems
    }

    def getInvoiceItemCandidates(String id, List orderNumbers, List shipmentNumbers) {
        Invoice invoice = Invoice.get(id)

        if (!invoice) {
            return []
        }

        def currentLocation = authService.currentLocation
        List<InvoiceItemCandidate> invoiceItemCandidates = InvoiceItemCandidate.createCriteria()
            .list() {
                if (invoice.party) {
                    eq("vendor", invoice.party)
                }

                if (invoice.currencyUom?.code) {
                    eq("currencyCode", invoice.currencyUom.code)
                }

                if (orderNumbers.size() > 0) {
                    'in'("orderNumber", orderNumbers)
                }

                if (shipmentNumbers.size() > 0) {
                    'in'("shipmentNumber", shipmentNumbers)
                }

                order {
                    eq("destinationParty", currentLocation.organization)
                }
            }

        return invoiceItemCandidates
    }

    def getDistinctFieldFromInvoiceItemCandidates(String id, String distinctField) {
        Invoice invoice = Invoice.get(id)

        if (!invoice) {
            return []
        }

        def currentLocation = authService.currentLocation
        List<InvoiceItemCandidate> invoiceItemCandidates = InvoiceItemCandidate.createCriteria()
            .list() {
                projections {
                    groupProperty(distinctField)
                }
                if (invoice.party) {
                    eq("vendor", invoice.party)
                }

                if (invoice.currencyUom?.code) {
                    eq("currencyCode", invoice.currencyUom.code)
                }

                order {
                    eq("destinationParty", currentLocation.organization)
                }

                ne(distinctField, "")
            }

        return invoiceItemCandidates
    }

    ReferenceNumber createOrUpdateVendorInvoiceNumber(Invoice invoice, String vendorInvoiceNumber) {
        ReferenceNumberType vendorInvoiceNumberType = ReferenceNumberType.findById(Constants.VENDOR_INVOICE_NUMBER_TYPE_ID)
        if (!vendorInvoiceNumberType) {
            throw new IllegalStateException("Must configure reference number type for Vendor Invoice Number with ID '${Constants.VENDOR_INVOICE_NUMBER_TYPE_ID}'")
        }

        ReferenceNumber referenceNumber = invoice.vendorInvoiceNumber

        if (vendorInvoiceNumber) {
            if (!referenceNumber) {
                referenceNumber = new ReferenceNumber()
                referenceNumber.identifier = vendorInvoiceNumber
                referenceNumber.referenceNumberType = vendorInvoiceNumberType
                invoice.addToReferenceNumbers(referenceNumber)
            }
            else {
                referenceNumber.identifier = vendorInvoiceNumber
            }
        }
        else if (referenceNumber) {
            invoice.removeFromReferenceNumbers(referenceNumber)
        }
        return referenceNumber
    }

    def removeInvoiceItem(String id) {
        InvoiceItem invoiceItem = InvoiceItem.get(id)
        if (!invoiceItem) {
            throw new IllegalArgumentException("No invoice item found with id: ${id}")
        }
        Invoice invoice = invoiceItem.invoice
        invoice.removeFromInvoiceItems(invoiceItem)
        deleteInvoiceItem(invoiceItem)
    }

    def updateItems(Invoice invoice, List items) {
        List<InvoiceItem> currentInvoiceItems = InvoiceItem.findAllByInvoice(invoice)

        items.each { item ->
            InvoiceItem invoiceItem = currentInvoiceItems.find{ it.id == item?.id }
            // update existing invoice item
            if (invoiceItem) {
                if (item.quantity > 0) {
                    invoiceItem.quantity = item.quantity
                } else {
                    removeInvoiceItem(invoiceItem.id)
                }
            } else {
                // create new invoice item from candidate
                if (item.quantityToInvoice > 0) {
                    InvoiceItemCandidate candidateItem = InvoiceItemCandidate.get(item.id)
                    if (!candidateItem) {
                        throw new IllegalArgumentException("No Invoice Item Candidate found with ID ${item.id}")
                    }
                    invoiceItem = createFromInvoiceItemCandidate(candidateItem)
                    invoiceItem.quantity = item.quantityToInvoice
                    invoice.addToInvoiceItems(invoiceItem)
                }
            }
        }

        invoice.save()
    }

    def submitInvoice(Invoice invoice) {
        invoice.dateSubmitted = new Date()
        invoice.disableRefresh = invoice.isPrepaymentInvoice
        invoice.save()
    }

    def postInvoice(Invoice invoice) {
        invoice.datePosted = new Date()
        invoice.disableRefresh = invoice.isPrepaymentInvoice
        invoice.save()
    }

    Invoice generatePrepaymentInvoice(Order order) {
        if (order.orderItems.any { it.hasInvoices } || order.orderAdjustments.any { it.hasInvoices }) {
            throw new Exception("Some order items or order adjustments for this order already have been invoiced")
        }

        Invoice invoice = createFromOrder(order)
        invoice.invoiceType = InvoiceType.findByCode(InvoiceTypeCode.PREPAYMENT_INVOICE)
        createOrUpdateVendorInvoiceNumber(invoice, order.orderNumber + Constants.PREPAYMENT_INVOICE_SUFFIX)

        if (order?.orderItems?.empty && order?.orderAdjustments?.empty) {
            throw new Exception("No order items or order adjustments found for given order")
        }

        order.activeOrderItems.each { OrderItem orderItem ->
            InvoiceItem invoiceItem = createFromOrderItem(orderItem)
            invoice.addToInvoiceItems(invoiceItem)
        }

        order.activeOrderAdjustments.each { OrderAdjustment orderAdjustment ->
            InvoiceItem invoiceItem = createFromOrderAdjustment(orderAdjustment)
            invoice.addToInvoiceItems(invoiceItem)
        }

        return invoice.save()
    }

    Invoice generateInvoice(Order order) {
        if (!order.hasPrepaymentInvoice) {
            throw new Exception("This order has no prepayment invoice")
        }

        Invoice invoice = createFromOrder(order)
        createOrUpdateVendorInvoiceNumber(invoice, order.orderNumber)

        order.orderItems.each { OrderItem orderItem ->
            if (orderItem.orderItemStatusCode == OrderItemStatusCode.CANCELED) {
                InvoiceItem invoiceItem = createFromOrderItem(orderItem)
                invoice.addToInvoiceItems(invoiceItem)
            } else {
                orderItem?.shipmentItems?.each { ShipmentItem shipmentItem ->
                    InvoiceItem invoiceItem = createFromShipmentItem(shipmentItem)
                    invoice.addToInvoiceItems(invoiceItem)
                }
            }
        }

        order.orderAdjustments.each { OrderAdjustment orderAdjustment ->
            InvoiceItem invoiceItem = createFromOrderAdjustment(orderAdjustment)
            invoice.addToInvoiceItems(invoiceItem)
        }

        return invoice.save()
    }

    Invoice createFromOrder(Order order) {
        Invoice invoice = new Invoice()
        invoice.invoiceNumber = identifierService.generateInvoiceIdentifier()
        invoice.name = order.name
        invoice.description = order.description
        invoice.partyFrom = order.destinationParty
        invoice.party = order.origin.organization
        invoice.dateInvoiced = LocalDate.now().toDate()
        invoice.currencyUom = UnitOfMeasure.findByCode(order.currencyCode)
        invoice.invoiceType = InvoiceType.findByCode(InvoiceTypeCode.INVOICE)
        return invoice
    }

    def refreshInvoiceItems(Invoice invoice) {
        invoice.invoiceItems?.each { item ->
            if (item.orderAdjustment) {
                OrderAdjustment orderAdjustment = item.orderAdjustment

                item.budgetCode = orderAdjustment.budgetCode
                item.quantityUom = orderAdjustment.orderItem?.quantityUom
                item.quantityPerUom = orderAdjustment.orderItem?.quantityPerUom ?: 1
                item.unitPrice = orderAdjustment.totalAdjustments
            } else if (item.shipmentItem) {
                ShipmentItem shipmentItem = item.shipmentItem
                OrderItem orderItem = shipmentItem.orderItems?.find { it }

                item.quantity = shipmentItem.quantity ? shipmentItem.quantity/orderItem.quantityPerUom : 0
                item.budgetCode = orderItem?.budgetCode
                item.quantityUom = orderItem?.quantityUom
                item.quantityPerUom = orderItem?.quantityPerUom ?: 1
                item.unitPrice = orderItem?.unitPrice
            } else {
                OrderItem orderItem = item.orderItem

                item.quantity = orderItem.quantity
                item.budgetCode = orderItem.budgetCode
                item.quantityUom = orderItem.quantityUom
                item.quantityPerUom = orderItem.quantityPerUom ?: 1
                item.unitPrice = orderItem.unitPrice
            }
        }
    }

    InvoiceItem createFromInvoiceItemCandidate(InvoiceItemCandidate candidate) {
        InvoiceItem invoiceItem = new InvoiceItem(
            budgetCode: candidate.budgetCode,
            product: candidate.productCode ? Product.findByProductCode(candidate.productCode) : null,
            glAccount: candidate.glAccount,
            quantity: candidate.quantity,
            quantityUom: candidate.quantityUom,
            quantityPerUom: candidate.quantityPerUom ?: 1,
            unitPrice: candidate.candidateUnitPrice
        )

        ShipmentItem shipmentItem = ShipmentItem.get(candidate.id)
        if (shipmentItem) {
            invoiceItem.addToShipmentItems(shipmentItem)
        } else {
            OrderAdjustment orderAdjustment = OrderAdjustment.get(candidate.id)
            if (orderAdjustment) {
                invoiceItem.addToOrderAdjustments(orderAdjustment)
            }
        }

        return invoiceItem
    }

    InvoiceItem createFromOrderItem(OrderItem orderItem) {
        if (orderItem.orderItemStatusCode == OrderItemStatusCode.CANCELED) {
            InvoiceItem invoiceItem = new InvoiceItem(
                    quantity: 0,
                    product: orderItem.product,
                    glAccount: orderItem.glAccount ?: orderItem.product.glAccount,
                    budgetCode: orderItem?.budgetCode,
                    quantityUom: orderItem?.quantityUom,
                    quantityPerUom: orderItem.quantityPerUom ?: 1,
                    unitPrice: orderItem.unitPrice
            )
            invoiceItem.addToOrderItems(orderItem)
            return invoiceItem
        }

        InvoiceItem invoiceItem = new InvoiceItem(
            quantity: orderItem.quantity,
            product: orderItem.product,
            glAccount: orderItem.glAccount ?: orderItem.product.glAccount,
            budgetCode: orderItem?.budgetCode,
            quantityUom: orderItem?.quantityUom,
            quantityPerUom: orderItem.quantityPerUom ?: 1,
            unitPrice: orderItem.unitPrice
        )
        invoiceItem.addToOrderItems(orderItem)
        return invoiceItem
    }

    InvoiceItem createFromShipmentItem(ShipmentItem shipmentItem) {
        OrderItem orderItem = shipmentItem.orderItems?.find { it }
        InvoiceItem invoiceItem = new InvoiceItem(
            quantity: shipmentItem.quantity ? shipmentItem.quantity/orderItem.quantityPerUom : 0,
            product: shipmentItem.product,
            glAccount: shipmentItem.product.glAccount,
            budgetCode: orderItem?.budgetCode,
            quantityUom: orderItem?.quantityUom,
            quantityPerUom: orderItem?.quantityPerUom ?: 1,
            unitPrice: orderItem?.unitPrice
        )
        invoiceItem.addToShipmentItems(shipmentItem)
        return invoiceItem
    }

    InvoiceItem createFromOrderAdjustment(OrderAdjustment orderAdjustment) {
        InvoiceItem invoiceItem = new InvoiceItem(
            budgetCode: orderAdjustment.budgetCode,
            product: orderAdjustment.orderItem?.product,
            glAccount: orderAdjustment.glAccount ?: orderAdjustment.orderItem?.glAccount ?: orderAdjustment.orderAdjustmentType?.glAccount,
            quantity: orderAdjustment?.canceled ? 0 : 1,
            quantityUom: orderAdjustment.orderItem?.quantityUom,
            quantityPerUom: orderAdjustment.orderItem?.quantityPerUom ?: 1,
            unitPrice: orderAdjustment.totalAdjustments
        )
        invoiceItem.addToOrderAdjustments(orderAdjustment)
        return invoiceItem
    }

    List<InvoiceItem> getPendingInvoiceItems(Product product) {
        return InvoiceItem.createCriteria().list() {
            invoice {
                isNull("datePaid")
                isNull("datePosted")
            }
            eq("product", product)
        }
    }

    def deleteInvoice(Invoice invoice) {
        if (!invoice) {
            throw new IllegalArgumentException("Missing invoice to delete")
        }
        invoice.invoiceItems?.each { InvoiceItem invoiceItem ->
            deleteInvoiceItem(invoiceItem)
        }
        invoice.delete(flush: true)
    }

    def deleteInvoiceItem(InvoiceItem invoiceItem) {
        if (!invoiceItem) {
            throw new IllegalArgumentException("Missing invoice item to delete")
        }
        invoiceItem.orderAdjustments?.each { OrderAdjustment oa -> oa.removeFromInvoiceItems(invoiceItem) }
        invoiceItem.orderItems?.each { OrderItem oi -> oi.removeFromInvoiceItems(invoiceItem) }
        invoiceItem.shipmentItems?.each { ShipmentItem si -> si.removeFromInvoiceItems(invoiceItem) }
        invoiceItem.delete()
    }

    CSVPrinter getInvoicesCsv(List<InvoiceList> invoices) {
        CSVPrinter csv = CSVUtils.getCSVPrinter()
        csv.printRecord(
    "# items",
            "Status",
            "Invoice Type",
            "Invoice Number",
            "Vendor",
            "Vendor invoice number",
            "Total Value",
            "Currency"
        )

        invoices?.each { InvoiceList invoiceListItem ->
            csv.printRecord(
                invoiceListItem?.itemCount,
                invoiceListItem?.status?.name(),
                invoiceListItem?.invoiceTypeCode?.name(),
                invoiceListItem?.invoiceNumber,
                "${invoiceListItem?.partyCode} ${invoiceListItem?.partyName}",
                invoiceListItem?.vendorInvoiceNumber,
                invoiceListItem?.invoice?.totalValue,
                invoiceListItem?.currency,
            )
        }
        return csv
    }

    CSVPrinter getInvoiceItemsCsv(List<InvoiceItem> invoiceItems) {
        CSVPrinter csv = CSVUtils.getCSVPrinter()
        csv.printRecord(
                "Invoice Number",
                "Vendor Invoice Number",
                "Vendor",
                "Currency",
                "Status",
                "Buyer Organization",
                "Invoice Type",
                "Code",
                "Name",
                "Order Number",
                "GL Account",
                "Budget Code",
                "Quantity",
                "Quantity per UoM",
                "Amount",
                "Total Amount",
        )

        invoiceItems?.each { InvoiceItem invoiceItem ->
            csv.printRecord(
                    invoiceItem.invoice?.invoiceNumber,
                    invoiceItem.invoice?.vendorInvoiceNumber,
                    "${invoiceItem.invoice?.party?.code} ${invoiceItem.invoice?.party?.name}",
                    invoiceItem.invoice?.currencyUom.name,
                    invoiceItem.invoice?.status?.name(),
                    "${invoiceItem.invoice?.partyFrom?.code} ${invoiceItem.invoice?.partyFrom?.name}",
                    invoiceItem.invoice?.invoiceType?.code?.name() ?: InvoiceTypeCode.INVOICE.name(),
                    invoiceItem.product?.productCode ?: applicationTagLib.message(code:'default.all.label', default: 'all'),
                    invoiceItem?.orderAdjustment ? invoiceItem?.description : invoiceItem.product?.name,
                    invoiceItem.order?.orderNumber,
                    invoiceItem.glAccount?.code,
                    invoiceItem.budgetCode?.code,
                    invoiceItem?.quantity,
                    invoiceItem?.quantityPerUom,
                    invoiceItem?.unitPrice ?: 0,
                    invoiceItem?.totalAmount ?: 0,
            )
        }
        return csv
    }
}
