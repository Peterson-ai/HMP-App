/**
 * Copyright (c) 2012 Partners In Health.  All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 **/
package org.pih.warehouse.core

import grails.util.Holders
import org.pih.warehouse.order.Order
import org.pih.warehouse.order.OrderType
import org.pih.warehouse.order.OrderTypeCode

class Organization extends Party {

    String id
    String code
    String name
    String description
    Location defaultLocation
    Boolean active = true

    Date dateCreated
    Date lastUpdated

    static hasMany = [locations : Location]

    Map<IdentifierTypeCode, String> sequences

    static mapping = {
        id generator: 'uuid'
        sequences joinTable: [key: 'sequences']
        roles cascade: "all-delete-orphan"
    }

    static constraints = {
        code(nullable: false, blank: false, unique: true,
                minSize: Holders.grailsApplication.config.openboxes.identifier.organization.minSize,
                maxSize: Holders.grailsApplication.config.openboxes.identifier.organization.maxSize)
        name(nullable: false, blank: false, maxSize: 255)
        description(nullable: true, maxSize: 255)
        defaultLocation(nullable: true)
        active(nullable: false)
    }

    String toString() {
        return name
    }

    int compareTo(Organization obj) {
        return name <=> obj.name ?:
                dateCreated <=> obj.dateCreated ?:
                        id <=> obj.id
    }

    boolean hasPurchaseOrders() {
        return Order.createCriteria().get {
            projections {
                count("id")
            }
            eq("orderType", OrderType.findByCode(OrderTypeCode.PURCHASE_ORDER.name()))
            eq("destinationParty", this)
        }
    }


    String maxPurchaseOrderNumber() {
        return Order.createCriteria().get {
            projections {
                max("orderNumber")
            }
            eq("orderType", OrderType.findByCode(OrderTypeCode.PURCHASE_ORDER.name()))
            eq("destinationParty", this)
        }
    }

    Boolean hasRoleType(RoleType roleType) {
        return roles.any { PartyRole role -> role.roleType == roleType }
    }

    Map toJson() {
        return [
                id             : id,
                name           : name,
                description    : description,
                code           : code,
                dateCreated    : dateCreated,
                lastUpdated    : lastUpdated,
                defaultLocation: Location.toJson(defaultLocation),
                partyType      : partyType,
                roles          : roles,
                sequences      : sequences
        ]
    }

}
