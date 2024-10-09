package org.pih.warehouse.invoice

import org.pih.warehouse.core.IdentifierService

class InvoiceIdentifierService extends IdentifierService {

    @Override
    String getPropertyKey() {
        return "invoice"
    }

    @Override
    protected Integer countDuplicates(String invoiceNumber) {
        return Invoice.countByInvoiceNumber(invoiceNumber)
    }
}
