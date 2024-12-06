package models;

import java.util.List;
import java.util.ArrayList;

public class Intervenant extends User {
    private String idCity;
    private int idApi;
    private String type;
    private String organizationName;
    private List<WorkProject> workProjects;

    public Intervenant(String fullName, String email, String password, String role, String idCity, int idApi, String type, String organizationName) {
        super(fullName, email, password, role);
        this.idCity = idCity;
        this.idApi = idApi;
        this.type = type;
        this.organizationName = organizationName;
        this.workProjects = new ArrayList<>();
    }

    public String getId() {
        return idCity;
    }

    public void setId(String idCity) {
        this.idCity = idCity;
    }

    public String getType() {
        return type;
    }

    public int getIdApi() {
        return idApi;
    }

    public void setIdApi(int idApi) {
        this.idApi = idApi;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<WorkProject> getWorkProjects() {
        return workProjects;
    }
    
    public void addWorkProject(WorkProject workProject) {
        workProjects.add(workProject);
    }

    public void updateWorkProject(int index, WorkProject workProject) {
        workProjects.set(index, workProject);
    }
    public void setType(String type) {
        this.type = type;
    }
}