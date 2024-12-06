package services;

import java.sql.*;
import java.util.*;
import java.sql.Date;

import models.*;

public class APIDatabase {

    private Connection connection;
    private Map<String, Resident> residentsMap;
    private Map<String, Intervenant> intervenantsMap;
    private Map<Integer, String> boroughMap;
    private Map<String, Integer> fsaToBoroughMap;

    public APIDatabase(String dbUrl, String dbUser, String dbPassword) {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        boroughMap = getAllBoroughs();
        fsaToBoroughMap = getFSAtoBoroughMapping();
        residentsMap = loadResidents();
        intervenantsMap = loadIntervenants();
        loadWorkRequests();
        loadNotifications();
    }

    // Charger tous les résidents dans une map (id -> Resident)
    private Map<String, Resident> loadResidents() {
        Map<String, Resident> map = new HashMap<>();
        String sql = "SELECT * FROM Residents";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String fsa = rs.getString("postal_code").substring(0, 3);
                int boroughId = fsaToBoroughMap.getOrDefault(fsa, 0);
                String borough = boroughMap.getOrDefault(boroughId, "Inconnu");
                Resident resident = new Resident(
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    "Resident",
                    rs.getString("address"),
                    rs.getString("postal_code"),
                    rs.getDate("birth_date").toString(),
                    borough,
                    rs.getInt("id")
                );
                map.put(rs.getString("email"), resident);
            }
            System.out.println("Residents loaded successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to load residents: " + e.getMessage());
        }
        return map;
    }

    // Charger tous les intervenants dans une map (id -> Intervenant)
    private Map<String, Intervenant> loadIntervenants() {
        Map<String, Intervenant> map = new HashMap<>();
        String sql = "SELECT * FROM Intervenants";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Intervenant intervenant = new Intervenant(
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    "Intervenant",
                    rs.getString("id"),
                    rs.getInt("_id"),
                    rs.getString("submittercategory"),
                    rs.getString("organisation_name")
                );
                map.put(rs.getString("email"), intervenant);

                // Charger les projets de travaux pour chaque intervenant
                List<WorkProject> intervenantProjects = getWorkProjectsForIntervenant(intervenant.getIdApi());
                for (WorkProject project : intervenantProjects) {
                    intervenant.addWorkProject(project);
                }
            }
            System.out.println("Intervenants loaded successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to load intervenants: " + e.getMessage());
        }
        return map;
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
    // Obtenir un résident par ID
    public Resident getResidentById(int id) {
        String sql = "SELECT email FROM Residents WHERE id = ?";
        Resident resident = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                resident = residentsMap.get(email);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch resident by ID: " + e.getMessage());
        }
        return resident;
    }

    // Obtenir un intervenant par ID
    public Intervenant getIntervenantById(int id) {
        String sql = "SELECT email FROM Intervenants WHERE id = ?";
        Intervenant intervenant = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            String email = rs.getString("email");
            intervenant = intervenantsMap.get(email);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch intervenant by ID: " + e.getMessage());
        }
        return intervenant;
    }


    // Ajouter un résident
    public void addResident(Resident resident) {
        String sql = "INSERT INTO Residents (full_name, phone_number, address, birth_date, postal_code, borough_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, resident.getFullName());
            stmt.setString(2, resident.getAddress());
            stmt.setDate(3, Date.valueOf(resident.getBirthDay()));
            stmt.setString(4, resident.getPostalCode());
            stmt.executeUpdate();
            System.out.println("Resident added: " + resident.getFullName());
        } catch (SQLException e) {
            System.err.println("Failed to add resident: " + e.getMessage());
        }
    }

    // Mettre à jour un résident
    public void updateResident(Resident resident) {
        String sql = "UPDATE Residents SET full_name = ?, phone_number = ?, address = ?, birth_date = ?, postal_code = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, resident.getFullName());
            stmt.setString(2, resident.getAddress());
            stmt.setDate(3, Date.valueOf(resident.getBirthDay()));
            stmt.setString(4, resident.getPostalCode());
            stmt.setInt(5, resident.getId());
            stmt.executeUpdate();
            System.out.println("Resident updated: " + resident.getFullName());
        } catch (SQLException e) {
            System.err.println("Failed to update resident: " + e.getMessage());
        }
    }

    // Ajouter un intervenant
    public void addIntervenant(Intervenant intervenant) {
        String sql = "INSERT INTO Intervenants (full_name, email, id, organisation_name, password_hash, submitter_category) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, intervenant.getFullName());
            stmt.setString(2, intervenant.getEmail());
            stmt.setString(3, intervenant.getId());
            stmt.setString(4, intervenant.getOrganizationName());
            stmt.setString(5, intervenant.getPassword());
            stmt.setString(6, intervenant.getType());
            stmt.executeUpdate();
            System.out.println("Intervenant added: " + intervenant.getFullName());
        } catch (SQLException e) {
            System.err.println("Failed to add intervenant: " + e.getMessage());
        }
    }

    // Mettre à jour un intervenant
    public void updateIntervenant(Intervenant intervenant) {
        String sql = "UPDATE Intervenants SET full_name = ?, email = ?, organisation_name = ?, password_hash = ?, submitter_category = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, intervenant.getFullName());
            stmt.setString(2, intervenant.getEmail());
            stmt.setString(3, intervenant.getOrganizationName());
            stmt.setString(4, intervenant.getPassword());
            stmt.setString(5, intervenant.getType());
            stmt.setString(6, intervenant.getId());
            stmt.executeUpdate();
            System.out.println("Intervenant updated: " + intervenant.getFullName());
        } catch (SQLException e) {
            System.err.println("Failed to update intervenant: " + e.getMessage());
        }
    }

    // Ajouter un projet de travaux
    public void addWorkProject(WorkProject project, int intervenantId) {
        String sql = "INSERT INTO WorkProjects (id, start_date, end_date, status, created_by, borough_id, reasonCategory) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int boroughId = getBoroughIdFromName(project.getBoroughId());
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, project.getId());
            stmt.setDate(2, Date.valueOf(project.getStartDate()));
            stmt.setDate(3, Date.valueOf(project.getEndDate()));
            stmt.setString(4, project.getCurrentStatus());
            stmt.setInt(5, intervenantId);
            stmt.setInt(6, boroughId);
            stmt.setString(7, project.getReasonCategory());
            stmt.executeUpdate();
            System.out.println("Work project added: " + project.getId());
        } catch (SQLException e) {
            System.err.println("Failed to add work project: " + e.getMessage());
        }
    }

    // Mettre à jour un projet de travaux
    public void updateWorkProjectStatus(String projectId, String newStatus) {
        String sql = "UPDATE WorkProjects SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, projectId);
            stmt.executeUpdate();
            System.out.println("Work project status updated: " + projectId);
        } catch (SQLException e) {
            System.err.println("Failed to update work project status: " + e.getMessage());
        }
    }

    // Récupérer les requêtes de travaux pour un résident
    public List<WorkRequest> getWorkRequestsForResident(int residentId) {
        List<WorkRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM WorkRequests WHERE resident_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Resident resident = getResidentById(residentId);
                String borough = resident.getNeighborhood();
                WorkRequest request = new WorkRequest(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("work_type"),
                    rs.getDate("request_date").toLocalDate(),
                    borough,
                    resident
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch work requests: " + e.getMessage());
        }
        return requests;
    }

    // Ajouter une requête de travail
    public void addWorkRequest(WorkRequest request, int residentId) {
        String sql = "INSERT INTO WorkRequests (title, description, work_type, resident_id, project_id, request_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, request.getTitle());
            stmt.setString(2, request.getDescription());
            stmt.setString(3, request.getWorkType());
            stmt.setInt(4, residentId);
            stmt.setInt(5, new Random().nextInt(100));
            stmt.setDate(6, Date.valueOf(request.getStartDate()));
            stmt.setString(7, request.getStatus());
            stmt.executeUpdate();
            System.out.println("Work request added: " + request.getTitle());
        } catch (SQLException e) {
            System.err.println("Failed to add work request: " + e.getMessage());
        }
    }

    // Récupérer les notifications pour un résident
    public List<Notification> getNotificationsForResident(int residentId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE resident_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Resident resident = getResidentById(rs.getInt("resident_id"));
                Notification notification = new Notification(
                    resident,
                    rs.getString("message"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("sent_at").toLocalDateTime()
                );
                notifications.add(notification);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch notifications: " + e.getMessage());
        }
        return notifications;
    }

    // Mettre à jour le statut d'une notification
    public void markNotificationAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET is_read = true WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
            System.out.println("Notification marked as read: " + notificationId);
        } catch (SQLException e) {
            System.err.println("Failed to update notification: " + e.getMessage());
        }
    }

    // Lire les projets assignés à un intervenant
    public List<WorkProject> getWorkProjectsForIntervenant(int intervenantId) {
        List<WorkProject> projects = new ArrayList<>();
        
        // Récupérer tous les boroughs et FSA pour effectuer les correspondances
        Intervenant intervenant = getIntervenantById(intervenantId);
        String sql = "SELECT wp.* " +
                    "FROM WorkProjects wp " +
                    "JOIN WorkRequestCandidates wrc ON wp.id = wrc.work_request_id " +
                    "WHERE wrc.intervenant_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, intervenantId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                // Identifier le borough et la FSA pour chaque projet
                int boroughId = rs.getInt("borough_id");
                String boroughName = boroughMap.getOrDefault(boroughId, "Inconnu");
                
                WorkProject project = new WorkProject(
                    rs.getString("id"),
                    boroughName,
                    rs.getString("status"),
                    rs.getString("reasonCategory"),
                    rs.getString("created_by"),
                    intervenant.getFullName()
                );
                projects.add(project);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch work projects: " + e.getMessage());
        }
        return projects;
    }

    // Méthode pour récupérer tous les boroughs
    private Map<Integer, String> getAllBoroughs() {
        Map<Integer, String> boroughMap = new HashMap<>();
        String sql = "SELECT id, name FROM Boroughs";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                boroughMap.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch boroughs: " + e.getMessage());
        }
        return boroughMap;
    }

    // Méthode pour récupérer la correspondance FSA -> Borough
    private Map<String, Integer> getFSAtoBoroughMapping() {
        Map<String, Integer> fsaToBoroughMap = new HashMap<>();
        String sql = "SELECT fsa, borough_id FROM FSAs";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fsaToBoroughMap.put(rs.getString("fsa"), rs.getInt("borough_id"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch FSA mappings: " + e.getMessage());
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

    // Fermer la connexion
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }
}