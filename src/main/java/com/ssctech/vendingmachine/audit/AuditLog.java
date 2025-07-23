package com.ssctech.vendingmachine.audit;

import com.ssctech.vendingmachine.event.VendingEvent;

import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private final List<VendingEvent> eventList = new ArrayList<>();

    public void addEvent(VendingEvent event) {
        eventList.add(event);
    }

    public List<VendingEvent> getEvents() {
        return new ArrayList<>(eventList);
    }

    public void print() {
        System.out.println("======= Audit Logs ======");
        eventList.forEach(System.out::println);
        System.out.println("=======    End     ======");
    }
}