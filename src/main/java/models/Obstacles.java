package models;

public class Obstacles {
    
    private String idRequest;
    private String streetId;
    private String shortName;
    private String streetImpactType;

    // Constructor
    public Obstacles(String idRequest, String streetId, String shortName, String streetImpactType) {
        this.idRequest = idRequest;
        this.streetId = streetId;
        this.shortName = shortName;
        this.streetImpactType = streetImpactType;
    }

    // Getters and Setters
    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStreetImpactType() {
        return streetImpactType;
    }

    public void setStreetImpactType(String streetImpactType) {
        this.streetImpactType = streetImpactType;
    }

    @Override
public String toString() {
    return "ID du travail : " + idRequest + 
           " | ID de la rue : " + streetId + 
           " | Rue : " + shortName + 
           " | Type d'impact : " + streetImpactType;
}
}