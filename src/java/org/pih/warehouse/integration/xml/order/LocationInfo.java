package org.pih.warehouse.integration.xml.order;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"stopSequence", "address", "plannedDateTime", "diverInstructions"})
public class LocationInfo {
    private Integer stopSequence;
    private Address address;
    private PlannedDateTime plannedDateTime;
    private String diverInstructions;

    public LocationInfo() { }

    public LocationInfo(Integer stopSequence, Address address, PlannedDateTime plannedDateTime, String diverInstructions) {
        this.stopSequence = stopSequence;
        this.address = address;
        this.plannedDateTime = plannedDateTime;
        this.diverInstructions = diverInstructions;
    }

    @XmlElement(name = "StopSequence")
    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    @XmlElement(name = "Address")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @XmlElement(name = "PlannedDateTime")
    public PlannedDateTime getPlannedDateTime() {
        return plannedDateTime;
    }

    public void setPlannedDateTime(PlannedDateTime plannedDateTime) {
        this.plannedDateTime = plannedDateTime;
    }

    @XmlElement(name = "DriverInstructions")
    public String getDiverInstructions() {
        return diverInstructions;
    }

    public void setDiverInstructions(String diverInstructions) {
        this.diverInstructions = diverInstructions;
    }
}
