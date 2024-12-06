package ui;

import javax.swing.*;
import java.awt.*;
import models.*;
import services.*;

public class IntervenantMenu extends JFrame {

    private static final long serialVersionUID = 1L;

    private Intervenant intervenant;
    private UserServices userServices;
    private ProjectsServices projectsServices;
    private ManageUsers manageUsers;

    // Composants Swing
    private JLabel lblWelcome;
    private JTextArea txtContentArea;
    private JButton btnModifyProfile, btnConsultRequests, btnUpdateWork, btnLogout, btnExit;

    public IntervenantMenu(Intervenant intervenant, UserServices userServices, ManageUsers manageUsers) {
        this.intervenant = intervenant;
        this.userServices = userServices;
        this.projectsServices = new ProjectsServices(manageUsers);

        setTitle("Menu Intervenant - MaVille");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        lblWelcome = new JLabel("Bienvenue, " + intervenant.getFullName(), JLabel.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblWelcome, BorderLayout.NORTH);

        // Zone de contenu
        txtContentArea = new JTextArea();
        txtContentArea.setEditable(false);
        txtContentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtContentArea.setText("Veuillez sélectionner une option ci-dessous.");
        add(new JScrollPane(txtContentArea), BorderLayout.CENTER);

        // Panneau de boutons
        JPanel panelButtons = new JPanel(new GridLayout(3, 2));
        btnModifyProfile = new JButton("Modifier Profil");
        btnConsultRequests = new JButton("Consulter les Requêtes");
        btnUpdateWork = new JButton("Modifier ou Ajouter un projet");
        btnLogout = new JButton("Se Déconnecter");
        btnExit = new JButton("Quitter");

        panelButtons.add(btnModifyProfile);
        panelButtons.add(btnConsultRequests);
        panelButtons.add(btnUpdateWork);
        panelButtons.add(btnLogout);
        panelButtons.add(btnExit);

        add(panelButtons, BorderLayout.SOUTH);

        // Actions des boutons
        initializeActions();

        setVisible(true);
    }

    private void initializeActions() {
        btnModifyProfile.addActionListener(e -> modifyProfile());
        btnConsultRequests.addActionListener(e -> consultRequests());
        btnUpdateWork.addActionListener(e -> updateWork());
        btnLogout.addActionListener(e -> logout());
        btnExit.addActionListener(e -> exitApplication());
    }

    private void modifyProfile() {
        userServices.modifyProfile(intervenant);
    }

    private void consultRequests() {
        projectsServices.consultRequests(intervenant);
        JOptionPane.showMessageDialog(this, "Consultation des requêtes terminée !");
    }

    private void updateWork() {
        projectsServices.updateWork(intervenant);
        JOptionPane.showMessageDialog(this, "Mise à jour du chantier terminée !");
    }

    private void logout() {
        dispose(); // Ferme cette fenêtre
        new MainMenu(userServices, manageUsers).setVisible(true); // Retour au menu principal
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir quitter ?", "Quitter", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}