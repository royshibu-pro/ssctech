package com.ssctech.vendingmachine.audit;

import com.ssctech.vendingmachine.event.VendingEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AuditLogTest {

    @Test
    void testAddAndGetEvent() {
        AuditLog auditLog = new AuditLog();
        VendingEvent testEvent = new VendingEvent.MachineReset(LocalDateTime.now());

        auditLog.addEvent(testEvent);
        List<VendingEvent> events = auditLog.getEvents();
        auditLog.print();
        assertNotNull(events, "The event list should not be null.");

        assertEquals(1, events.size(), "The event list should have one event.");
        assertEquals(testEvent, events.getFirst(), "The event in the list should be the one we added.");
    }
}