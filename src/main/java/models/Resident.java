package models;

import java.util.ArrayList;
import java.util.List;

public class Resident extends User {
    private String address;
    private String postalCode;
    private String birthDay;
    private String neighborhood;
    private int id;
    private List<WorkRequest> workRequests;
    private List<Notification> notifications; // Nouvel attribut pour les notifications

    public Resident(String fullName, String email, String password, String role, String address, String postalCode, String birthDay, String neighborhood, int id) {
        super(fullName, email, password, role);
        this.address = address;
        this.postalCode = postalCode;
        this.birthDay = birthDay;
        this.neighborhood = neighborhood;
        this.workRequests = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPostalCode(){
        return postalCode;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public List<WorkRequest> getWorkRequests() {
        return workRequests;
    }

    public void addWorkRequest(WorkRequest request) {
        workRequests.add(request);
    }

    public void updateWorkRequest(int index, WorkRequest request) {
        if (index >= 0 && index < workRequests.size()) {
            workRequests.set(index, request);
        }
    }

    // Getters et setters pour notifications
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
    }

    public void markNotificationAsRead(int index) {
        if (index >= 0 && index < notifications.size()) {
            notifications.get(index).setRead(true);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}