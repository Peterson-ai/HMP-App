package org.pih.warehouse.core

import grails.validation.Validateable

class StockMovementParamsCommand implements Validateable {

    String id

    Integer stepNumber

    Boolean refreshPicklistItems = true

    static constraints = {
        stepNumber nullable: true
        refreshPicklistItems nullable: true
    }

}
