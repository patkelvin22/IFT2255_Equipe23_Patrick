package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import services.*;
import models.*;

public class ResidentMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private Resident resident;

    private UserServices userServices;
    private ManageUsers manageUsers;
    private ProjectsServices projectsServices;

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel[] pages;

    private JButton btnParticiper;

    public ResidentMenu(Resident resident, UserServices userServices, ManageUsers manageUsers) {
        this.resident = resident;
        this.userServices = userServices;
        this.manageUsers = manageUsers;
        this.projectsServices = new ProjectsServices(manageUsers);

        setTitle("Menu Résident - MaVille");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Bienvenue, " + resident.getFullName(), JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Contenu principal avec CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialiser les pages
        initializePages();

        add(mainPanel, BorderLayout.CENTER);

        // Boutons d'action
        addButtonPanels();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializePages() {
        pages = new JPanel[3];

        // Page 1
        pages[0] = createPage(
            "LES TRAVAUX DANS VOTRE QUARTIER :                           Détails\n" +
            "Boulevard Saint-Joseph Est entre                            Date de début prévue\n" +
            "De Bullion et Hôtel-de-Ville                                30 septembre 2024\n\n" +
            "Travaux de toiture                                          Date de fin prévue \n" +
            "Aménagement prévu pour la circulation piétonne              11 octobre 2024\n" +
            "en tout temps\n\n" +
            "Arrondissement                                              Responsable des travaux\n" +
            "Plateau Mont-Royal                                          Toitures V. Perreault\n\n" +
            "Statut : Terminé"
        );

        // Page 2
        pages[1] = createPage(
            "LES TRAVAUX DANS VOTRE QUARTIER :                           Détails\n" +
            "Rue Sherbrooke Ouest entre                                  Date de début prévue\n" +
            "Atwater et Guy                                              5 octobre 2024\n\n" +
            "Réfection du trottoir                                       Date de fin prévue\n" +
            "Aménagement temporaire pour les piétons                     15 novembre 2024\n" +
            "et pistes cyclables                                         Horaire des entraves\n" +
            "                                                            Semaine de 8h00 à 18h00\n" +
            "                                                            Fins de semaine de 9h00 à 17h00\n\n" +
            "Arrondissement                                              Responsable des travaux\n" +
            "Ville-Marie                                                 Construction MTL\n\n" +
            "Statut : En cours"
        );

        // Page 3 (Statut Prévu)
        pages[2] = createPage(
            "LES TRAVAUX DANS VOTRE QUARTIER :                           Détails\n" +
            "Avenue du Parc entre                                        Date de début prévue\n" +
            "Mont-Royal et Laurier                                       1er décembre 2024\n\n" +
            "Installation d'un nouvel éclairage                          Date de fin prévue\n" +
            "public et réfection des trottoirs                           20 décembre 2024\n\n" +
            "Horaire des entraves\n" +
            "Semaine de 7h30 à 17h00\n" +
            "Fins de semaine de 9h00 à 15h00\n\n" +
            "Arrondissement                                              Responsable des travaux\n" +
            "Plateau Mont-Royal                                          Éclairage Urbain Inc.\n\n" +
            "Statut : Prévu"
        );

        for (JPanel page : pages) {
            mainPanel.add(page);
        }
    }

    private JPanel createPage(String content) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        contentArea.setEditable(false);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        return panel;
    }

    private void addButtonPanels() {
        JPanel topLeftPanel = new JPanel(new GridLayout(2, 1));
        JButton btnNotifications = createActionButton("1. Notifications", e -> showNotifications());
        JButton btnModifierProfil = createActionButton("2. Modifier Profil", e -> modifyProfile());
        topLeftPanel.add(btnNotifications);
        topLeftPanel.add(btnModifierProfil);

        add(topLeftPanel, BorderLayout.WEST);

        JPanel topRightPanel = new JPanel(new GridLayout(3, 1));
        JButton btnConsulterTravaux = createActionButton("4. Consulter Travaux", e -> consultWorks());
        JButton btnSoumettreRequete = createActionButton("5. Soumettre Requête", e -> submitRequest());
        topRightPanel.add(btnConsulterTravaux);
        topRightPanel.add(btnSoumettreRequete);

        add(topRightPanel, BorderLayout.EAST);

        JPanel bottomRightPanel = new JPanel(new GridLayout(2, 1));
        JButton btnConsulterEntraves = createActionButton("6. Consulter Entraves", e -> showObstacles());
        JButton btnPlanifierTravail = createActionButton("7. Planifier Travail", e -> planWork());
        JButton btnDeconnexion = createActionButton("8. Déconnexion", e -> logout());
        JButton btnNextPage = createActionButton("Page Suivante", e -> nextPage());
        bottomRightPanel.add(btnConsulterEntraves);
        bottomRightPanel.add(btnPlanifierTravail);
        bottomRightPanel.add(btnDeconnexion);
        bottomRightPanel.add(btnNextPage);

        add(bottomRightPanel, BorderLayout.SOUTH);

        btnParticiper = new JButton("Participer à la planification");
        btnParticiper.addActionListener(e -> participate());
    }

    private JButton createActionButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private void showNotifications() {
        JOptionPane.showMessageDialog(this, "Notifications...");
    }

    private void modifyProfile() {
        userServices.modifyProfile(resident);
    }

    private void consultWorks() {
        projectsServices.consultWorks();
    }

    private void submitRequest() {
        projectsServices.submitRequest(resident);
        JOptionPane.showMessageDialog(this, "Requête soumise.");
    }

    private void showObstacles() {
        projectsServices.showObstacles();
        JOptionPane.showMessageDialog(this, "Entraves consultées.");
    }

    private void planWork() {
        projectsServices.planWork();
        JOptionPane.showMessageDialog(this, "Travail planifié.");
    }

    private void logout() {
        dispose();
        new MainMenu(userServices, manageUsers).setVisible(true);
    }

    private void participate() {
        JOptionPane.showMessageDialog(this, "Participation enregistrée.");
    }

    private void nextPage() {
        cardLayout.next(mainPanel);
        // Déterminer si la page actuelle contient "Statut : Prévu"
        for (JPanel panel : pages) {
            String currentPageContent = ((JTextArea) ((JScrollPane) panel.getComponent(0)).getViewport().getView()).getText();
            if (currentPageContent.contains("Statut : Prévu")) {
                btnParticiper.setVisible(true);
            } else {
                btnParticiper.setVisible(false);
            }
        }
    
        revalidate();
        repaint();
    }
}