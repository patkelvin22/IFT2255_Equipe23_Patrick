package models;

import javax.swing.JOptionPane;

public class WorkProject {

    private String id;
    private String boroughId;
    private String currentStatus;
    private String reasonCategory;
    private String submitterCategory;
    private String organisationName;
    private String title;
    private String startDate;
    private String endDate;

    // Constructors
    public WorkProject() {}

    public WorkProject(String id, String boroughId, String currentStatus, String reasonCategory, String submitterCategory, String organisationName) {
        this.id = id;
        this.boroughId = boroughId;
        this.currentStatus = currentStatus;
        this.reasonCategory = reasonCategory;
        this.submitterCategory = submitterCategory;
        this.organisationName = organisationName;
        this.title = organisationName != null ? reasonCategory + " " + organisationName : reasonCategory + " " + submitterCategory;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoroughId() {
        return boroughId;
    }

    public void setBoroughId(String boroughId) {
        this.boroughId = boroughId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getReasonCategory() {
        return reasonCategory;
    }

    public void setReasonCategory(String reasonCategory) {
        this.reasonCategory = reasonCategory;
        updateTitle();
    }

    public String getSubmitterCategory() {
        return submitterCategory;
    }

    public void setSubmitterCategory(String submitterCategory) {
        this.submitterCategory = submitterCategory;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        updateTitle();
    }

    public String getTitle() {
        return title;
    }

    public void updateTitle() {
        this.title = reasonCategory + " " + organisationName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public void getDetails(){
        JOptionPane.showMessageDialog(null, "ID: " + id + "\nBorough ID: " + boroughId + "\nCurrent Status: " + currentStatus + "\nReason Category: " + reasonCategory + "\nSubmitter Category: " + submitterCategory + "\nOrganisation Name: " + organisationName + "\nTitle: " + title);
    }
}