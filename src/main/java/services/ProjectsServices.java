package services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.*;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import java.awt.*;

import java.util.UUID;


public class ProjectsServices {

    private static final String roadWorksUrl = "https://donnees.montreal.ca/api/3/action/datastore_search?resource_id=cc41b532-f12d-40fb-9f55-eb58c9a2b12b";
    private static final String obstaclesUrl = "https://donnees.montreal.ca/api/3/action/datastore_search?resource_id=a2bc8014-488c-495d-941b-e7ae1999d1bd";
    public List<WorkProject> roadWorks;
    private List<Obstacles> obstacles;
    public String[] roadWorkTypes = Stream.of(Category.values())
                                    .map(Category::getDisplayName)
                                    .toArray(String[]::new);
    
    private String[] residentialWorkTypes = {"Toiture", "Fenêtres", "Portes", "Revêtement extérieur", "Piscine", "Patio", "Balcon", "Clôture", "Gazon", "Pavé uni", "Asphalte"};

    private String[] neighborhoods;
    public ManageUsers manageUsers;
    private List<Resident> residents = new ArrayList<>();
    private List<String> residentsNeighborhoods = new ArrayList<>();
    public ProjectsServices(ManageUsers manageUsers) {
        this.manageUsers = manageUsers;
        this.residents = manageUsers.getAllResidents();
        this.roadWorks = getRoadWorks();
        this.obstacles = getObstacles();
        this.neighborhoods = getNeighborhood(roadWorks).toArray(new String[0]);
        for (Resident resident : residents){
            this.residentsNeighborhoods.add(resident.getNeighborhood());
        }
    }


    public void submitRequest(Resident resident) {
        JFrame frame = new JFrame("Soumettre une requête");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
    
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JLabel titleLabel = new JLabel("Titre :");
        JTextField titleField = new JTextField();
        JLabel descriptionLabel = new JLabel("Description :");
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JLabel typeLabel = new JLabel("Type de travaux :");
        JComboBox<String> typeBox = new JComboBox<>(residentialWorkTypes);
        JLabel startDateLabel = new JLabel("Date de début :");
        JTextField startDateField = new JTextField("YYYY-MM-DD");
    
        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(descriptionLabel);
        panel.add(descriptionScroll);
        panel.add(typeLabel);
        panel.add(typeBox);
        panel.add(startDateLabel);
        panel.add(startDateField);
    
        JButton submitButton = new JButton("Soumettre");
        JButton cancelButton = new JButton("Annuler");
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
    
        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
    
            if (title.isEmpty() || description.isEmpty() || startDate == null) {
                JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            WorkRequest newRequest = new WorkRequest(title, description, type, startDate, resident.getNeighborhood(), resident);
            manageUsers.addWorkRequest(newRequest);
    
            JOptionPane.showMessageDialog(frame, "Votre requête a été soumise avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });
    
        cancelButton.addActionListener(e -> frame.dispose());
    
        frame.setVisible(true);
    }

    public void consultWorks() {
        JFrame frame = new JFrame("Consultation des Travaux");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Panneau pour les travaux affichés
        JPanel workPanel = new JPanel();
        workPanel.setLayout(new BoxLayout(workPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(workPanel);

        // Panneau de navigation pour les pages
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");
        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);

        // Panneau pour les filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Statut : ");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"Tous", "En cours", "Prévu"});
        JLabel boroughLabel = new JLabel("Quartier : ");
        String[] boroughOptions = new String[neighborhoods.length + 1];
        boroughOptions[0] = "Tous";
        System.arraycopy(neighborhoods, 0, boroughOptions, 1, neighborhoods.length);
        JComboBox<String> boroughFilter = new JComboBox<>(boroughOptions);
        JButton applyFilterButton = new JButton("Appliquer le filtre");
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(boroughLabel);
        filterPanel.add(boroughFilter);
        filterPanel.add(applyFilterButton);

        // Ajouter les composants au cadre principal
        frame.add(filterPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(navigationPanel, BorderLayout.SOUTH);

        // Variables pour la pagination
        int[] currentPage = {0};
        int itemsPerPage = 25;

        // Fonction pour mettre à jour l'affichage des travaux
        Runnable updateWorkDisplay = () -> {
            workPanel.removeAll();
            List<WorkProject> filteredWorks = getFilteredWorks(
                (String) statusFilter.getSelectedItem(),
                (String) boroughFilter.getSelectedItem()
            );

            int start = currentPage[0] * itemsPerPage;
            int end = Math.min(start + itemsPerPage, filteredWorks.size());

            for (int i = start; i < end; i++) {
                WorkProject work = filteredWorks.get(i);
                JButton workButton = new JButton(work.getTitle());
                workButton.addActionListener(e -> work.getDetails());
                workPanel.add(workButton);
            }
            workPanel.revalidate();
            workPanel.repaint();
        };

        // Boutons de navigation
        previousButton.addActionListener(e -> {
            if (currentPage[0] > 0) {
                currentPage[0]--;
                updateWorkDisplay.run();
            }
        });
        nextButton.addActionListener(e -> {
            int maxPage = (int) Math.ceil(getFilteredWorks(
                (String) statusFilter.getSelectedItem(),
                (String) boroughFilter.getSelectedItem()
            ).size() / (double) itemsPerPage);
            if (currentPage[0] < maxPage - 1) {
                currentPage[0]++;
                updateWorkDisplay.run();
            }
        });

        // Filtre appliqué
        applyFilterButton.addActionListener(e -> {
            currentPage[0] = 0;
            updateWorkDisplay.run();
        });

        // Charger les travaux initiaux
        updateWorkDisplay.run();
        frame.setVisible(true);
    }

    private List<WorkProject> getFilteredWorks(String statusFilter, String boroughFilter) {
        LocalDate currentDate = LocalDate.now();
        LocalDate threeMonthsLater = currentDate.plusMonths(3);
    
        return roadWorks.stream()
            .filter(work -> {
                // Filtrer par quartier
                if (boroughFilter != null && !boroughFilter.equals("Tous") && !boroughFilter.isEmpty() && !boroughFilter.equals(work.getBoroughId())) {
                    return false;
                }
    
                // Filtrer par statut (En cours, Prévu ou Tous)
                if ("En cours".equalsIgnoreCase(statusFilter)) {
                    // Vérifier si la date actuelle est entre la date de début et la date de fin
                    LocalDate startDate = LocalDate.parse(work.getStartDate());
                    LocalDate endDate = work.getEndDate() != null ? LocalDate.parse(work.getEndDate()) : null;
                    return (startDate.isBefore(currentDate) || startDate.isEqual(currentDate)) &&
                           (endDate == null || endDate.isAfter(currentDate) || endDate.isEqual(currentDate));
                } else if ("Prévu".equalsIgnoreCase(statusFilter)) {
                    // Vérifier si la date de début est dans les 3 prochains mois
                    LocalDate startDate = LocalDate.parse(work.getStartDate());
                    return startDate.isAfter(currentDate) && startDate.isBefore(threeMonthsLater);
                }
    
                // Inclure tous les travaux pour le filtre "Tous"
                return true;
            })
            .collect(Collectors.toList());
    }


    public void planWork() {
        // Création de la fenêtre principale
        JFrame frame = new JFrame("Planification des Travaux");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
    
        // Panneau pour la sélection des jours
        JPanel dayPanel = new JPanel(new GridLayout(7, 1));
        JLabel lblInstructions = new JLabel("Choisissez les plages horaires pour les travaux :");
        dayPanel.add(lblInstructions);
    
        // Champs pour chaque jour de la semaine
        String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        List<JPanel> dayFields = new ArrayList<>();
        for (String day : days) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lblDay = new JLabel(day + ": ");
            JTextField startField = new JTextField(5);
            startField.setToolTipText("Heure de début (HH:MM)");
            JTextField endField = new JTextField(5);
            endField.setToolTipText("Heure de fin (HH:MM)");
            panel.add(lblDay);
            panel.add(new JLabel("Début :"));
            panel.add(startField);
            panel.add(new JLabel("Fin :"));
            panel.add(endField);
            dayFields.add(panel);
            dayPanel.add(panel);
        }
    
        // Boutons de contrôle
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSubmit = new JButton("Soumettre");
        JButton btnCancel = new JButton("Annuler");
        controlPanel.add(btnSubmit);
        controlPanel.add(btnCancel);
    
        // Ajouter les panneaux à la fenêtre
        frame.add(dayPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
    
        // Action pour le bouton Annuler
        btnCancel.addActionListener(e -> frame.dispose());
    
        // Action pour le bouton Soumettre
        btnSubmit.addActionListener(e -> {
            List<TimeSlot> preferredSlots = new ArrayList<>();
            for (int i = 0; i < days.length; i++) {
                JPanel panel = dayFields.get(i);
                JTextField startField = (JTextField) panel.getComponent(2);
                JTextField endField = (JTextField) panel.getComponent(4);
    
                String start = startField.getText().trim();
                String end = endField.getText().trim();
    
                if (!start.isEmpty() && !end.isEmpty()) {
                    // Validation du format d'heure (HH:MM)
                    if (isValidTimeFormat(start) && isValidTimeFormat(end)) {
                        preferredSlots.add(new TimeSlot(days[i], start, end));
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "Format d'heure invalide pour " + days[i] + ". Veuillez utiliser le format HH:MM.",
                            "Erreur de format",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
    
            // Afficher les plages horaires sélectionnées
            if (!preferredSlots.isEmpty()) {
                StringBuilder summary = new StringBuilder("Plages horaires sélectionnées :\n");
                for (TimeSlot slot : preferredSlots) {
                    summary.append(slot).append("\n");
                }
                JOptionPane.showMessageDialog(frame, summary.toString(), "Résumé des Plages Horaires", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Fermer la fenêtre après soumission
            } else {
                JOptionPane.showMessageDialog(frame, "Aucune plage horaire sélectionnée.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        frame.setVisible(true);
    }
    
    // Fonction pour valider le format d'heure (HH:MM)
    private boolean isValidTimeFormat(String time) {
        return time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    public void updateWork(Intervenant intervenant) {
        // Fenêtre principale
        JFrame frame = new JFrame("Gérer les travaux");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
    
        // Panneau pour la liste des travaux
        JPanel workListPanel = new JPanel();
        workListPanel.setLayout(new BoxLayout(workListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(workListPanel);
    
        // Vérifier s'il y a des travaux associés
        if (intervenant.getWorkProjects().isEmpty()) {
            JLabel noWorkLabel = new JLabel("Aucun travail associé à cet intervenant.", JLabel.CENTER);
            workListPanel.add(noWorkLabel);
        } else {
            for (WorkProject work : intervenant.getWorkProjects()) {
                JPanel workPanel = new JPanel(new BorderLayout());
                JLabel workTitle = new JLabel(work.getTitle());
                JButton modifyButton = new JButton("Modifier le statut");
                
                // Action pour modifier le statut
                modifyButton.addActionListener(e -> modifyWorkStatus(frame, work));
                
                workPanel.add(workTitle, BorderLayout.CENTER);
                workPanel.add(modifyButton, BorderLayout.EAST);
                workListPanel.add(workPanel);
            }
        }
    
        // Bouton pour soumettre un nouveau projet
        JButton submitNewProjectButton = new JButton("Soumettre un nouveau projet");
        submitNewProjectButton.addActionListener(e -> {
            frame.dispose(); // Fermer la fenêtre actuelle
            submitNewWork(intervenant); // Ouvrir le formulaire pour un nouveau projet
        });
    
        // Ajouter les panneaux à la fenêtre
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(submitNewProjectButton, BorderLayout.SOUTH);
    
        // Afficher la fenêtre
        frame.setVisible(true);
    }
    
    // Méthode pour modifier le statut d'un travail
    private void modifyWorkStatus(JFrame parentFrame, WorkProject work) {
        String newStatus = JOptionPane.showInputDialog(parentFrame, "Entrez le nouveau statut pour : " + work.getTitle(), "Modifier le statut", JOptionPane.PLAIN_MESSAGE);
    
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            work.setCurrentStatus(newStatus.trim());
            manageUsers.updateWorkProjectStatus(work.getId(), newStatus); // Mettre à jour le travail dans le système
            JOptionPane.showMessageDialog(parentFrame, "Le statut du travail a été mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Le statut ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Méthode pour afficher le formulaire de nouveau projet
    private void submitNewWork(Intervenant intervenant) {
        JFrame newProjectFrame = new JFrame("Soumettre un nouveau projet");
        newProjectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newProjectFrame.setSize(800, 600);
        newProjectFrame.setLayout(new GridLayout(0, 2, 10, 10));
    
        // Champs pour le nouveau projet
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(roadWorkTypes);
        JTextField boroughField = new JTextField();
        JTextField startDateField = new JTextField("YYYY-MM-DD");
        JTextField endDateField = new JTextField("YYYY-MM-DD");
    
        // Ajouter les champs au formulaire
        newProjectFrame.add(new JLabel("Titre :"));
        newProjectFrame.add(titleField);
        newProjectFrame.add(new JLabel("Description :"));
        newProjectFrame.add(descriptionField);
        newProjectFrame.add(new JLabel("Type de travaux :"));
        newProjectFrame.add(typeBox);
        newProjectFrame.add(new JLabel("Quartier :"));
        newProjectFrame.add(boroughField);
        newProjectFrame.add(new JLabel("Date de début :"));
        newProjectFrame.add(startDateField);
        newProjectFrame.add(new JLabel("Date de fin :"));
        newProjectFrame.add(endDateField);
    
        // Boutons de contrôle
        JButton submitButton = new JButton("Soumettre");
        JButton cancelButton = new JButton("Annuler");
        newProjectFrame.add(submitButton);
        newProjectFrame.add(cancelButton);
    
        // Action pour soumettre le projet
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            String borough = boroughField.getText().trim();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
    
            if (title.isEmpty() || description.isEmpty() || borough.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(newProjectFrame, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String id = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
            WorkProject newWork = new WorkProject(
                id,
                borough,
                "Prévu",
                type,
                intervenant.getType(),
                intervenant.getFullName()
            );
            newWork.setStartDate(startDate);
            newWork.setEndDate(endDate);
    
            roadWorks.add(newWork);
            manageUsers.addWorkProject(newWork);
    
            JOptionPane.showMessageDialog(newProjectFrame, "Le projet a été soumis avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            newProjectFrame.dispose(); // Fermer la fenêtre après soumission
        });
    
        // Action pour annuler
        cancelButton.addActionListener(e -> newProjectFrame.dispose());
    
        newProjectFrame.setVisible(true);
    }

    public void consultRequests(Intervenant intervenant) {
        JFrame requestFrame = new JFrame("Consulter les requêtes de travaux");
        requestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        requestFrame.setSize(800, 600);
        requestFrame.setLayout(new BorderLayout());
    
        // Liste des requêtes de travaux
        List<WorkRequest> workRequests = new ArrayList<>();
        for (Resident resident : residents) {
            workRequests.addAll(resident.getWorkRequests());
        }
    
        // Panneau principal pour afficher les requêtes
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(requestPanel);
    
        // Boutons de navigation
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");
        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);
    
        // Pagination
        int[] currentPage = {0};
        int itemsPerPage = 10;
    
        // Méthode pour mettre à jour l'affichage des requêtes
        Runnable updateRequestsDisplay = () -> {
            requestPanel.removeAll();
            int start = currentPage[0] * itemsPerPage;
            int end = Math.min(start + itemsPerPage, workRequests.size());
    
            for (int i = start; i < end; i++) {
                WorkRequest request = workRequests.get(i);
    
                JPanel requestItemPanel = new JPanel(new BorderLayout());
                JLabel titleLabel = new JLabel("Titre : " + request.getTitle());
                JButton applyButton = new JButton("Soumettre sa candidature");
    
                // Action pour le bouton "Soumettre sa candidature"
                applyButton.addActionListener(e -> {
                    applyToRequest(intervenant, request);
                    JOptionPane.showMessageDialog(requestFrame, "Candidature soumise avec succès pour : " + request.getTitle());
                });
    
                requestItemPanel.add(titleLabel, BorderLayout.CENTER);
                requestItemPanel.add(applyButton, BorderLayout.EAST);
                requestPanel.add(requestItemPanel);
            }
    
            requestPanel.revalidate();
            requestPanel.repaint();
        };
    
        // Boutons de navigation
        previousButton.addActionListener(e -> {
            if (currentPage[0] > 0) {
                currentPage[0]--;
                updateRequestsDisplay.run();
            }
        });
    
        nextButton.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) workRequests.size() / itemsPerPage);
            if (currentPage[0] < maxPage - 1) {
                currentPage[0]++;
                updateRequestsDisplay.run();
            }
        });
    
        // Charger l'affichage initial
        updateRequestsDisplay.run();
    
        // Ajouter les composants à la fenêtre
        requestFrame.add(scrollPane, BorderLayout.CENTER);
        requestFrame.add(navigationPanel, BorderLayout.SOUTH);
    
        // Afficher la fenêtre
        requestFrame.setVisible(true);
    }

    private void applyToRequest(Intervenant intervenant, WorkRequest request) {
        if (!request.getCandidates().contains(intervenant)) {
            request.getCandidates().add(intervenant);
            Resident owner = request.getResident();
            String notificationMsg = "Votre requête a reçu une nouvelle candidature de " + intervenant.getFullName();
            owner.getNotifications().add(new Notification(owner, notificationMsg, false, LocalDateTime.now()));
            JOptionPane.showMessageDialog(null, "Candidature soumise pour : " + request.getTitle(), "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Vous avez déjà soumis une candidature pour cette requête.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showObstacles() {
        JFrame frame = new JFrame("Afficher les entraves");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
    
        JLabel filterLabel = new JLabel("Filtrer par :");
        JComboBox<String> filterBox = new JComboBox<>(new String[]{"Rue", "Toutes les entraves"});
        JTextField filterField = new JTextField();
        JButton filterButton = new JButton("Appliquer le filtre");
    
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(filterLabel, BorderLayout.WEST);
        filterPanel.add(filterBox, BorderLayout.CENTER);
        filterPanel.add(filterField, BorderLayout.EAST);
        filterPanel.add(filterButton, BorderLayout.SOUTH);
    
        JTextArea resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
    
        frame.add(filterPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
    
        filterButton.addActionListener(e -> {
            String filterType = (String) filterBox.getSelectedItem();
            String filterValue = filterField.getText().trim();
            StringBuilder results = new StringBuilder();
    
            if (filterType.equals("Rue")) {
                for (Obstacles obstacle : obstacles) {
                    if (obstacle.getStreetId().equals(filterValue)) {
                        results.append(obstacle).append("\n");
                    }
                }
            } else {
                for (Obstacles obstacle : obstacles) {
                    results.append(obstacle).append("\n");
                }
            }
    
            if (results.length() == 0) {
                results.append("Aucune donnée trouvée pour ce filtre.");
            }
    
            resultArea.setText(results.toString());
        });
    
        frame.setVisible(true);
    }

    public List<WorkProject> getRoadWorksList(){
        return roadWorks;
    }

    public List<Obstacles> getObstaclesList(){
        return obstacles;
    }

    private List<WorkProject> getRoadWorks() {
        List<WorkProject> roadWorksList = new ArrayList<>();
        try {
            URL url = new URL(roadWorksUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class);
                JsonArray records = jsonResponse.getAsJsonObject("result").getAsJsonArray("records");

                for (int i = 0; i < records.size(); i++) {
                    JsonObject record = records.get(i).getAsJsonObject();
                    WorkProject roadWork = new WorkProject();

                    roadWork.setId(record.has("id") && !record.get("id").isJsonNull() ? record.get("id").getAsString() : null);
                    
                    String reasonCategory = record.has("reason_category") && !record.get("reason_category").isJsonNull() 
                    ? record.get("reason_category").getAsString() : null;
                
                    String appCategory = Category.getAppCategory(reasonCategory);
                    roadWork.setReasonCategory(appCategory);

                    roadWork.setBoroughId(record.has("boroughid") && !record.get("boroughid").isJsonNull() ? record.get("boroughid").getAsString() : null);
                    roadWork.setCurrentStatus(record.has("currentstatus") && !record.get("currentstatus").isJsonNull() ? record.get("currentstatus").getAsString() : null);
                    roadWork.setSubmitterCategory(record.has("submittercategory") && !record.get("submittercategory").isJsonNull() ? record.get("submittercategory").getAsString() : null);
                    roadWork.setOrganisationName(record.has("organizationname") && !record.get("organizationname").isJsonNull() ? record.get("organizationname").getAsString() : null);
                    if (record.has("duration_start_date") && !record.get("duration_start_date").isJsonNull()) {
                        String startDateTime = record.get("duration_start_date").getAsString();
                        roadWork.setStartDate(LocalDate.parse(startDateTime.substring(0, 10)).toString()); // Extracts the "YYYY-MM-DD" part
                    }
                    // End Date (Extract date only)
                    if (record.has("duration_end_date") && !record.get("duration_end_date").isJsonNull()) {
                        String endDateTime = record.get("duration_end_date").getAsString();
                        roadWork.setEndDate(LocalDate.parse(endDateTime.substring(0, 10)).toString()); // Extracts the "YYYY-MM-DD" part
                    }
                    roadWorksList.add(roadWork);
                }
                reader.close();
            } else {
                System.out.println("Error: Unable to fetch data from API. Response code: " + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roadWorksList;
    }

    private List<Obstacles> getObstacles() {
        List<Obstacles> obstaclesList = new ArrayList<>();
        try {
            URL url = new URL(obstaclesUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class);
                JsonArray records = jsonResponse.getAsJsonObject("result").getAsJsonArray("records");

                for (int i = 0; i < records.size(); i++) {
                    JsonObject record = records.get(i).getAsJsonObject();
                    Obstacles obstacle = new Obstacles(
                        record.has("id_request") && !record.get("id_request").isJsonNull() ? record.get("id_request").getAsString() : null,
                        record.has("streetid") && !record.get("streetid").isJsonNull() ? record.get("streetid").getAsString() : null,
                        record.has("shortname") && !record.get("shortname").isJsonNull() ? record.get("shortname").getAsString() : null,
                        record.has("streetimpacttype") && !record.get("streetimpacttype").isJsonNull() ? record.get("streetimpacttype").getAsString() : null
                    );
                    obstaclesList.add(obstacle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obstaclesList;
    }

    private List<String> getNeighborhood(List<WorkProject> roadWorks) {
        Set<String> neighbordhoodSet = new HashSet<>();

        for (WorkProject roadWork : roadWorks){
            String category = null;

            category = roadWork.getBoroughId();

            if (category != null) {
                neighbordhoodSet.add(category);
            }
        }
        return new ArrayList<>(neighbordhoodSet);
    }
}