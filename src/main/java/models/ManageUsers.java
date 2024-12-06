package models;

import services.APIDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageUsers {

    private static ManageUsers instance;
    private APIDatabase apiDatabase; // Corrigé pour inclure les paramètres de connexion
    private Map<String, Resident> residents; // Utilisation d'Integer comme clé pour correspondre aux IDs
    private Map<String, Intervenant> intervenants; // Utilisation d'Integer comme clé pour correspondre aux IDs

    public ManageUsers(String dbUrl, String dbUser, String dbPassword) {
        apiDatabase = new APIDatabase(dbUrl, dbUser, dbPassword); // Initialisation avec les paramètres de la base de données
        loadUsers();
    }

    public static ManageUsers getInstance(String dbUrl, String dbUser, String dbPassword) {
        if (instance == null) {
            instance = new ManageUsers(dbUrl, dbUser, dbPassword);
        }
        return instance;
    }

    // Charger tous les utilisateurs depuis la base de données
    public void loadUsers() {
        residents = apiDatabase.getResidentsMap();
        intervenants = apiDatabase.getIntervenantsMap();
    }

    // Ajouter un résident
    public void addResident(Resident resident) {
        apiDatabase.addResident(resident); // Obtenir l'ID après l'ajout
        residents.put(resident.getEmail(), resident); // Ajouter dans la map
    }

    // Ajouter un intervenant
    public void addIntervenant(Intervenant intervenant) {
        apiDatabase.addIntervenant(intervenant); // Obtenir l'ID après l'ajout
        intervenants.put(intervenant.getEmail(), intervenant); // Ajouter dans la map
    }

    // Obtenir un résident par ID
    public Resident getResident(String email) {
        return residents.get(email);
    }

    // Obtenir un intervenant par ID
    public Intervenant getIntervenant(String email) {
        return intervenants.get(email);
    }

    // Obtenir la liste de tous les résidents
    public List<Resident> getAllResidents() {
        return new ArrayList<>(residents.values());
    }

    // Obtenir la liste de tous les intervenants
    public List<Intervenant> getAllIntervenants() {
        return new ArrayList<>(intervenants.values());
    }

    // Mettre à jour un utilisateur
    public void updateUser(User user) {
        if (user instanceof Resident) {
            Resident resident = (Resident) user;
            residents.put(resident.getEmail(), resident);
            apiDatabase.updateResident(resident); // Appeler la méthode de mise à jour dans la base
        } else if (user instanceof Intervenant) {
            Intervenant intervenant = (Intervenant) user;
            intervenants.put(intervenant.getId(), intervenant);
            apiDatabase.updateIntervenant(intervenant); // Appeler la méthode de mise à jour dans la base
        }
    }

    // Ajouter une requête de travail pour un résident
    public void addWorkRequest(Resident resident, WorkRequest request) {
        apiDatabase.addWorkRequest(request, resident.getId()); // Associer avec l'ID du résident
    }

    // Ajouter un projet de travaux pour un intervenant
    public void addWorkProject(Intervenant intervenant, WorkProject project) {
        apiDatabase.addWorkProject(project, intervenant.getIdApi()); // Associer avec l'ID de l'intervenant
    }

    // Récupérer les notifications pour un résident
    public List<Notification> getNotificationsForResident(int residentId) {
        return apiDatabase.getNotificationsForResident(residentId); // Appeler la méthode correspondante
    }

    // Marquer une notification comme lue
    public void markNotificationAsRead(int notificationId) {
        apiDatabase.markNotificationAsRead(notificationId); // Mettre à jour dans la base
    }

    // Modifier un projet de travaux
    public void updateWorkProjectStatus(String projectId, String newStatus) {
        apiDatabase.updateWorkProjectStatus(projectId, newStatus); // Mettre à jour dans la base
    }

}