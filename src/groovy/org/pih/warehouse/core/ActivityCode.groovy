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

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

enum ActivityCode {

    MANAGE_INVENTORY('MANAGE_INVENTORY'),   // FIXME should change to MANAGE_STOCK
    ADJUST_INVENTORY('ADJUST_INVENTORY'),   // FIXME should change to ADJUST_STOCK
    SEND_STOCK('SEND_STOCK'),               // FIXME should change to TRANSFER_STOCK
    RECEIVE_STOCK('RECEIVE_STOCK'),
    CONSUME_STOCK('CONSUME_STOCK'),
    ISSUE_STOCK('ISSUE_STOCK'),

    EXTERNAL('EXTERNAL'),

    APPROVE_REQUEST('APPROVE_REQUEST'),
    PLACE_REQUEST('PLACE_REQUEST'),
    FULFILL_REQUEST('FULFILL_REQUEST'),

    APPROVE_ORDER('APPROVE_ORDER'),
    PLACE_ORDER('PLACE_ORDER'),
    FULFILL_ORDER('FULFILL_ORDER'),

    // Activities for INTERNAL_LOCATION
    CROSS_DOCKING('CROSS_DOCKING'),
    PUTAWAY_STOCK('PUTAWAY_STOCK'),
    PICK_STOCK('PICK_STOCK'),
    HOLD_STOCK('HOLD_STOCK'),

    // Requisition reason codes
    SUBSTITUTE_REQUISITION_ITEM('SUBSTITUTE_REQUISITION_ITEM'),
    MODIFY_REQUISITION_ITEM('MODIFY_REQUISITION_ITEM'),
    MODIFY_PICKLIST_ITEM('MODIFY_PICKLIST_ITEM'),

    // Notifications
    ENABLE_NOTIFICATIONS('ENABLE_NOTIFICATIONS'),
    ENABLE_WEBHOOKS('ENABLE_WEBHOOKS'),
    // Approval notifications (for all, for requestors, for approvers)
    DISABLE_APPROVAL_NOTIFICATIONS('DISABLE_APPROVAL_NOTIFICATIONS'),
    DISABLE_CONSUMER_APPROVAL_NOTIFICATIONS('DISABLE_CONSUMER_APPROVAL_NOTIFICATIONS'),
    DISABLE_DEPOT_APPROVAL_NOTIFICATIONS('DISABLE_DEPOT_APPROVAL_NOTIFICATIONS'),

    // Packing
    PACK_SHIPMENT('PACK_SHIPMENT'),

    // Receiving
    PARTIAL_RECEIVING('PARTIAL_RECEIVING'),

    // Accounting (Budget Code, GL Account)
    REQUIRE_ACCOUNTING('REQUIRE_ACCOUNTING'),

    // Central purchasing
    ENABLE_CENTRAL_PURCHASING('ENABLE_CENTRAL_PURCHASING'),

    // Submitting requests
    SUBMIT_REQUEST('SUBMIT_REQUEST'),

    // Dynamic creation
    DYNAMIC_CREATION('DYNAMIC_CREATION'),

    AUTOSAVE('AUTOSAVE'),

    NONE('NONE')

    final String id

    ActivityCode(String id) { this.id = id }

    static list() {
         [
                MANAGE_INVENTORY,
                ADJUST_INVENTORY,
                APPROVE_ORDER,
                APPROVE_REQUEST,
                PLACE_ORDER,
                PLACE_REQUEST,
                FULFILL_ORDER,
                FULFILL_REQUEST,
                SEND_STOCK,
                RECEIVE_STOCK,
                CONSUME_STOCK,
                CROSS_DOCKING,
                PUTAWAY_STOCK,
                PICK_STOCK,
                EXTERNAL,
                ENABLE_NOTIFICATIONS,
                ENABLE_WEBHOOKS,
                PACK_SHIPMENT,
                PARTIAL_RECEIVING,
                REQUIRE_ACCOUNTING,
                ENABLE_CENTRAL_PURCHASING,
                HOLD_STOCK,
                SUBMIT_REQUEST,
                DYNAMIC_CREATION,
                AUTOSAVE,
                NONE,
        ]
    }

    static binTrackingList() {
        [
                PICK_STOCK,
                PUTAWAY_STOCK
        ]
    }
}
