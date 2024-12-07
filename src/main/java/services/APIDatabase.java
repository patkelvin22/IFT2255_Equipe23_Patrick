package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class APIDatabase {

    private final String API_BASE_URL;
    private final ObjectMapper objectMapper;

    private Map<String, Resident> residentsMap;
    private Map<String, Intervenant> intervenantsMap;
    private Map<Integer, String> boroughMap;
    private Map<String, Integer> fsaToBoroughMap;

    public APIDatabase(String apiBaseUrl) {
        this.API_BASE_URL = apiBaseUrl; // URL de base de votre API REST
        this.objectMapper = new ObjectMapper();
        this.residentsMap = new HashMap<>();
        this.intervenantsMap = new HashMap<>();
        this.boroughMap = getAllBoroughs();
        this.fsaToBoroughMap = getFSAtoBoroughMapping();
        loadResidents();
        loadIntervenants();
        loadWorkRequests();
        loadNotifications();
    }

    // Charger les résidents via l'API REST
    private void loadResidents() {
        try {
            URL url = new URL(API_BASE_URL + "/residents");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des résidents : HTTP code " + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            List<Resident> residents = objectMapper.readValue(is, new TypeReference<List<Resident>>() {});
            for (Resident resident : residents) {
                List<WorkRequest> workRequests = getWorkRequestsForResident(resident.getId()); // Récupérer les requêtes
                for (WorkRequest request : workRequests) {
                    resident.addWorkRequest(request); // Assigner les requêtes
                }
                List<Notification> notifications = getNotificationsForResident(resident.getId()); // Assigner les notifications
                for (Notification notification : notifications){
                    resident.addNotification(notification);
                }
                residentsMap.put(resident.getEmail(), resident);
            }

            conn.disconnect();
            System.out.println("Residents loaded successfully from REST API.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des résidents via l'API REST : " + e.getMessage());
        }
    }

    // Charger les intervenants via l'API REST
    private void loadIntervenants() {
        try {
            URL url = new URL(API_BASE_URL + "/intervenants");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des intervenants : HTTP code " + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            List<Intervenant> intervenants = objectMapper.readValue(is, new TypeReference<List<Intervenant>>() {});
            for (Intervenant intervenant : intervenants) {
                List<WorkProject> projects = getWorkProjectsForIntervenant(intervenant.getIdApi());
                for (WorkProject project : projects) {
                    intervenant.addWorkProject(project);
                }
                intervenantsMap.put(intervenant.getEmail(), intervenant);
            }

            conn.disconnect();
            System.out.println("Intervenants loaded successfully from REST API.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des intervenants via l'API REST : " + e.getMessage());
        }
    }

    // Récupérer un résident spécifique via l'API REST
    public Resident getResidentById(int id) {
        try {
            URL url = new URL(API_BASE_URL + "/residents/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 404) {
                System.out.println("Résident non trouvé pour l'ID " + id);
                return null;
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération du résident : HTTP code " + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            Resident resident = objectMapper.readValue(is, Resident.class);
            conn.disconnect();
            return resident;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du résident via l'API REST : " + e.getMessage());
            return null;
        }
    }

    // Récupérer un intervenant spécifique via l'API REST
    public Intervenant getIntervenantById(int id) {
        try {
            URL url = new URL(API_BASE_URL + "/intervenants/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 404) {
                System.out.println("Intervenant non trouvé pour l'ID " + id);
                return null;
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération de l'intervenant : HTTP code " + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            Intervenant intervenant = objectMapper.readValue(is, Intervenant.class);
            conn.disconnect();
            return intervenant;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'intervenant via l'API REST : " + e.getMessage());
            return null;
        }
    }


    // CHarger les requêtes de travaux pour un résident
    private void loadWorkRequests() {
        List<Resident> residents = new ArrayList<>(residentsMap.values());
        for (Resident r : residents) {
            List<WorkRequest> requests = getWorkRequestsForResident(r.getId());
            for (WorkRequest request : requests) {
                r.addWorkRequest(request);
            }
        }
    }

    //Charge les notifications pour un résident
    private void loadNotifications(){
        List<Resident> residents = new ArrayList<>(residentsMap.values());
        for (Resident r : residents){
            List<Notification> notifications = getNotificationsForResident(r.getId());
            for (Notification notification : notifications){
                r.addNotification(notification);
            }
        }
    }


    // Ajouter un résident
    public void addResident(Resident resident) {
        try {
            URL url = new URL(API_BASE_URL + "/residents");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Convertir l'objet Resident en JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonResident = mapper.writeValueAsString(resident);

            // Envoyer le JSON dans le corps de la requête
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonResident.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 201) { // 201 Created
                throw new RuntimeException("Échec de l'ajout du résident : HTTP code " + conn.getResponseCode());
            }

            System.out.println("Resident ajouté avec succès : " + resident.getFullName());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du résident via l'API REST : " + e.getMessage());
        }
    }

    public void updateResident(Resident resident) {
        try {
            URL url = new URL(API_BASE_URL + "/residents/" + resident.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            // Convertir l'objet Resident en JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonResident = mapper.writeValueAsString(resident);
    
            // Envoyer le JSON dans le corps de la requête
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonResident.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            if (conn.getResponseCode() != 200) { // 200 OK
                throw new RuntimeException("Échec de la mise à jour du résident : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Resident mis à jour avec succès : " + resident.getFullName());
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du résident via l'API REST : " + e.getMessage());
        }
    }

    // Ajouter un intervenant
    public void addIntervenant(Intervenant intervenant) {
        try {
            URL url = new URL(API_BASE_URL + "/intervenants");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            ObjectMapper mapper = new ObjectMapper();
            String jsonIntervenant = mapper.writeValueAsString(intervenant);
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonIntervenant.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            if (conn.getResponseCode() != 201) {
                throw new RuntimeException("Échec de l'ajout de l'intervenant : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Intervenant ajouté avec succès : " + intervenant.getFullName());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'intervenant via l'API REST : " + e.getMessage());
        }
    }

    // Mettre à jour un intervenant
    public void updateIntervenant(Intervenant intervenant) {
        try {
            URL url = new URL(API_BASE_URL + "/intervenants/" + intervenant.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            ObjectMapper mapper = new ObjectMapper();
            String jsonIntervenant = mapper.writeValueAsString(intervenant);
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonIntervenant.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la mise à jour de l'intervenant : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Intervenant mis à jour avec succès : " + intervenant.getFullName());
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'intervenant via l'API REST : " + e.getMessage());
        }
    }

    // Ajouter un projet de travaux
    public void addWorkProject(WorkProject project) {
        try {
            URL url = new URL(API_BASE_URL + "/work-projects");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            ObjectMapper mapper = new ObjectMapper();
            String jsonProject = mapper.writeValueAsString(project);
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonProject.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            if (conn.getResponseCode() != 201) {
                throw new RuntimeException("Échec de l'ajout du projet de travaux : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Projet de travaux ajouté avec succès : " + project.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du projet de travaux via l'API REST : " + e.getMessage());
        }
    }

    // Mettre à jour un projet de travaux
    public void updateWorkProjectStatus(String projectId, String newStatus) {
        try {
            URL url = new URL(API_BASE_URL + "/work-projects/" + projectId + "/status");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            String jsonStatus = "{\"status\": \"" + newStatus + "\"}";
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonStatus.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la mise à jour du statut du projet : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Statut du projet mis à jour : " + projectId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du statut du projet via l'API REST : " + e.getMessage());
        }
    }

    // Récupérer les requêtes de travaux pour un résident
    public List<WorkRequest> getWorkRequestsForResident(int residentId) {
        List<WorkRequest> requests = new ArrayList<>();
        try {
            URL url = new URL(API_BASE_URL + "/residents/" + residentId + "/work-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
    
            if (conn.getResponseCode() != 200) { // 200 OK
                throw new RuntimeException("Échec de la récupération des requêtes de travaux : HTTP code " + conn.getResponseCode());
            }
    
            InputStream is = conn.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            requests = mapper.readValue(is, new TypeReference<List<WorkRequest>>() {});

            for (WorkRequest request : requests){
                List<Intervenant> candidates = getCandidatesForWorkRequest(request.getId());
                for (Intervenant candidate : candidates){
                    request.addCandidate(candidate);
                }
            }
    
            System.out.println("Requêtes de travaux récupérées avec succès pour le résident ID : " + residentId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des requêtes de travaux via l'API REST : " + e.getMessage());
        }
        return requests;
    }

    private List<Intervenant> getCandidatesForWorkRequest(int requestId) {
        List<Intervenant> candidates = new ArrayList<>();
        try {
            URL url = new URL(API_BASE_URL + "/work-requests/" + requestId + "/candidates");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des candidats : HTTP code " + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            candidates = objectMapper.readValue(is, new TypeReference<List<Intervenant>>() {});

            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des candidats : " + e.getMessage());
        }
        return candidates;
    }


    // Ajouter une requête de travail
    public void addWorkRequest(WorkRequest request) {
        try {
            URL url = new URL(API_BASE_URL + "/work-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
    
            ObjectMapper mapper = new ObjectMapper();
            String jsonRequest = mapper.writeValueAsString(request);
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            if (conn.getResponseCode() != 201) {
                throw new RuntimeException("Échec de l'ajout de la requête de travail : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Requête de travail ajoutée : " + request.getTitle());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la requête de travail via l'API REST : " + e.getMessage());
        }
    }

    // Récupérer les notifications pour un résident
    public List<Notification> getNotificationsForResident(int residentId) {
        List<Notification> notifications = new ArrayList<>();
        try {
            URL url = new URL(API_BASE_URL + "/residents/" + residentId + "/notifications");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des notifications : HTTP code " + conn.getResponseCode());
            }
    
            InputStream is = conn.getInputStream();
            notifications = objectMapper.readValue(is, new TypeReference<List<Notification>>() {});
            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des notifications via l'API REST : " + e.getMessage());
        }
        return notifications;
    }

    // Mettre à jour le statut d'une notification
    public void markNotificationAsRead(int notificationId) {
        try {
            URL url = new URL(API_BASE_URL + "/notifications/" + notificationId + "/read");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la mise à jour de la notification : HTTP code " + conn.getResponseCode());
            }
    
            System.out.println("Notification marquée comme lue : " + notificationId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la notification via l'API REST : " + e.getMessage());
        }
    }

    // Lire les projets assignés à un intervenant
    public List<WorkProject> getWorkProjectsForIntervenant(int intervenantId) {
        List<WorkProject> projects = new ArrayList<>();
        try {
            URL url = new URL(API_BASE_URL + "/intervenants/" + intervenantId + "/work-projects");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des projets : HTTP code " + conn.getResponseCode());
            }
    
            InputStream is = conn.getInputStream();
            projects = objectMapper.readValue(is, new TypeReference<List<WorkProject>>() {});
            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des projets via l'API REST : " + e.getMessage());
        }
        return projects;
    }

    // Méthode pour récupérer tous les boroughs
    private Map<Integer, String> getAllBoroughs() {
        Map<Integer, String> boroughMap = new HashMap<>();
        try {
            URL url = new URL(API_BASE_URL + "/boroughs");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des boroughs : HTTP code " + conn.getResponseCode());
            }
    
            InputStream is = conn.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> boroughs = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
    
            for (Map<String, Object> borough : boroughs) {
                Integer id = (Integer) borough.get("id");
                String name = (String) borough.get("name");
                boroughMap.put(id, name);
            }
    
            System.out.println("Boroughs loaded successfully.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des boroughs via l'API REST : " + e.getMessage());
        }
        return boroughMap;
    }

    // Méthode pour récupérer la correspondance FSA -> Borough
    private Map<String, Integer> getFSAtoBoroughMapping() {
        Map<String, Integer> fsaToBoroughMap = new HashMap<>();
        try {
            URL url = new URL(API_BASE_URL + "/fsa-boroughs");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Échec de la récupération des mappings FSA -> Borough : HTTP code " + conn.getResponseCode());
            }
    
            InputStream is = conn.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> mappings = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
    
            for (Map<String, Object> mapping : mappings) {
                String fsa = (String) mapping.get("fsa");
                Integer boroughId = (Integer) mapping.get("borough_id");
                fsaToBoroughMap.put(fsa, boroughId);
            }
    
            System.out.println("FSA to Borough mappings loaded successfully.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des mappings FSA -> Borough via l'API REST : " + e.getMessage());
        }
        return fsaToBoroughMap;
    }

    public Integer getBoroughIdFromName(String boroughName) {
        for (Map.Entry<Integer, String> entry : boroughMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(boroughName)) {
                return entry.getKey(); // Retourne l'ID du borough
            }
        }
        return null; // Si aucun borough correspondant n'est trouvé
    }

    public Map<String, Resident> getResidentsMap() {
        return residentsMap;
    }

    public Map<String, Intervenant> getIntervenantsMap() {
        return intervenantsMap;
    }

}