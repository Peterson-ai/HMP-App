package org.pih.warehouse.core

class HistoryItem<T> implements Comparable<HistoryItem> {
    Date date

    Location location

    ReferenceDocument referenceDocument

    EventTypeDto eventTypeDto

    Comment comment

    User createdBy

    @Override
    int compareTo(HistoryItem historyItem) {
        // Events with event code created should be placed
        // at the top of the lists as a first created event
        if (historyItem?.eventTypeDto?.eventCode == EventCode.CREATED) {
            return 1
        }

        if (eventTypeDto?.eventCode == EventCode.CREATED) {
            return -1
        }

        // falling back to the reference document id, to avoid disappearing items
        // in case of the same dates and event codes
        return date <=> historyItem?.date ?:
               eventTypeDto?.name <=> historyItem?.eventTypeDto?.name ?:
               referenceDocument?.id <=> historyItem?.referenceDocument?.id
    }
}
