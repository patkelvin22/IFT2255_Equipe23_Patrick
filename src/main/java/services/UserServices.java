package services;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.*;

import models.*;
import ui.*;

public class UserServices {

    private ManageUsers manageUsers;
    private Runnable onLoginSuccess;
    private int residentsCount = 0;
    private int intervenantsCount = 0;

    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPassword;
    private JTextField txtBirthDate;
    private JTextField txtAddress;
    private JTextField txtPostalCode;
    private JTextField txtType;
    private JTextField txtNeighborhood;
    private JTextField txtId;
    private JTextField txtOrganization;

    public UserServices(ManageUsers manageUsers){
        this.manageUsers = manageUsers;
        residentsCount = manageUsers.getAllResidents().size();
        intervenantsCount = manageUsers.getAllIntervenants().size();
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public User signUp() {
        String[] options = {"Intervenant", "Résident", "Retour"};
        int choice = JOptionPane.showOptionDialog(null,
                "Inscrivez-vous en tant que :",
                "Inscription",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
    
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            return null; // Retourner si l'utilisateur annule
        }
    
        String userType = (choice == 0) ? "Intervenant" : "Resident";
        User user = getInfo(userType);
    
        if (user != null) {
            JOptionPane.showMessageDialog(null, "Inscription réussie !");
        }
        return user;
    }

    public void logIn() {
        String[] options = {"Intervenant", "Résident", "Retour"};
        int choice = JOptionPane.showOptionDialog(null,
                "Connectez-vous en tant que :",
                "Connexion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            return; // Retourner si l'utilisateur annule
        }

        String userType = (choice == 0) ? "Intervenant" : "Resident";
        User user = authentificate(userType);

        if (user != null) {
            JOptionPane.showMessageDialog(null, "Connexion réussie !");
            if (onLoginSuccess != null) {
                onLoginSuccess.run(); // Exécuter le callback pour fermer MainMenu
            }
            if (user instanceof Intervenant) {
                new IntervenantMenu((Intervenant) user, this, manageUsers).setVisible(true);
            } else {
                new ResidentMenu((Resident) user, this, manageUsers).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Compte inexistant ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private User getInfo(String userType) {
        // Crée une fenêtre modale pour afficher les champs à remplir
        JDialog infoDialog = new JDialog((JFrame) null, "Inscription - " + userType, true);
        infoDialog.setSize(500, 400);
        infoDialog.setLayout(new BorderLayout());
        infoDialog.setLocationRelativeTo(null);
    
        // Panel principal pour afficher les champs
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // Ajouter des champs communs pour tous les utilisateurs
        JTextField txtFullName = createField(infoPanel, "Nom complet", "");
        JTextField txtEmail = createField(infoPanel, "Adresse e-mail", "");
        JTextField txtPassword = createField(infoPanel, "Mot de passe", "");
    
        // Champs spécifiques pour les résidents
        if (userType.equals("Resident")) {
            txtAddress = createField(infoPanel, "Adresse", "");
            txtPostalCode = createField(infoPanel, "Code postal", "");
            txtBirthDate = createField(infoPanel, "Date de naissance (jj-mm-aaaa)", "");
            txtNeighborhood = createField(infoPanel, "Quartier", "");
        }
    
        // Champs spécifiques pour les intervenants
        if (userType.equals("Intervenant")) {
            txtId = createField(infoPanel, "Identifiant de la ville (8 chiffres)", "");
            txtType = createField(infoPanel, "Type d'entreprise (public/privé/particulier)", "");
            txtOrganization = createField(infoPanel, "Nom de l'organisation", "");
        }
    
        infoDialog.add(infoPanel, BorderLayout.CENTER);
    
        // Boutons pour valider ou annuler
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Valider");
        JButton btnCancel = new JButton("Annuler");
    
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);
        infoDialog.add(buttonPanel, BorderLayout.SOUTH);
    
        final User[] createdUser = {null};
    
        // Action pour valider
        btnSubmit.addActionListener(e -> {
            try {
                if (userType.equals("Resident")) {
                    Resident resident = new Resident(
                            txtFullName.getText(),
                            txtEmail.getText(),
                            txtPassword.getText(),
                            "Resident",
                            txtAddress.getText(),
                            txtPostalCode.getText(),
                            txtBirthDate.getText(),
                            txtNeighborhood.getText(),
                            residentsCount++
                    );
                    manageUsers.addResident(resident);
                    createdUser[0] = resident;
                } else if (userType.equals("Intervenant")) {
                    Intervenant intervenant = new Intervenant(
                            txtFullName.getText(),
                            txtEmail.getText(),
                            txtPassword.getText(),
                            "Intervenant",
                            txtId.getText(),
                            intervenantsCount++,
                            txtType.getText(),
                            txtOrganization.getText()
                    );
                    manageUsers.addIntervenant(intervenant);
                    createdUser[0] = intervenant;
                }
                JOptionPane.showMessageDialog(infoDialog, "Inscription réussie !");
                infoDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(infoDialog, "Erreur lors de l'inscription : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        // Action pour annuler
        btnCancel.addActionListener(e -> {
            createdUser[0] = null;
            infoDialog.dispose();
        });
    
        // Afficher la fenêtre
        infoDialog.setVisible(true);
    
        return createdUser[0];
    }

    public User authentificate(String userType) {
        String email = JOptionPane.showInputDialog("Entrez votre adresse e-mail :");
        User user = (userType.equals("Resident")) ? manageUsers.getResident(email) : manageUsers.getIntervenant(email);
    
        if (user == null) {
            JOptionPane.showMessageDialog(null, "Compte introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    
        String password = JOptionPane.showInputDialog("Entrez votre mot de passe :");
        if (!user.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(null, "Mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    
        return user;
    }

    public void modifyProfile(User user) {
        // Crée une fenêtre modale pour afficher et modifier le profil utilisateur
        JDialog profileDialog = new JDialog((JFrame) null, "Modifier le Profil", true);
        profileDialog.setSize(500, 400);
        profileDialog.setLayout(new BorderLayout());
        profileDialog.setLocationRelativeTo(null);

        // Panel principal pour afficher les champs modifiables
        JPanel profilePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ajouter des champs dynamiques en fonction du type d'utilisateur
        txtFullName = createField(profilePanel, "Nom complet", user.getFullName());
        txtEmail = createField(profilePanel, "Adresse e-mail", user.getEmail());
        txtPassword = createField(profilePanel, "Mot de passe", user.getPassword());

        // Champs spécifiques aux résidents
        if (user instanceof Resident) {
            Resident resident = (Resident) user;
            txtBirthDate = createField(profilePanel, "Date de naissance", resident.getBirthDay());
            txtAddress = createField(profilePanel, "Adresse", resident.getAddress());
        }

        // Champs spécifiques aux intervenants
        if (user instanceof Intervenant) {
            Intervenant intervenant = (Intervenant) user;
            txtType = createField(profilePanel, "Type d'entreprise", intervenant.getType());
        }

        profileDialog.add(profilePanel, BorderLayout.CENTER);

        // Boutons pour sauvegarder ou annuler
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Sauvegarder");
        JButton btnCancel = new JButton("Annuler");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        profileDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Action pour sauvegarder les modifications
        btnSave.addActionListener(e -> {
            user.setFullName(txtFullName.getText());
            user.setEmail(txtEmail.getText());
            user.setPassword(txtPassword.getText());

            if (user instanceof Resident) {
                Resident resident = (Resident) user;
                resident.setBirthDay(txtBirthDate.getText());
                resident.setAddress(txtAddress.getText());
            }

            if (user instanceof Intervenant) {
                Intervenant intervenant = (Intervenant) user;
                intervenant.setType(txtType.getText());
            }

            // Mettre à jour le profil dans le système
            manageUsers.updateUser(user);

            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(profileDialog, "Profil mis à jour avec succès !");
            profileDialog.dispose();

            // Afficher les informations mises à jour
            displayUserInfo(user);
        });

        // Action pour annuler les modifications
        btnCancel.addActionListener(e -> profileDialog.dispose());

        // Afficher la fenêtre
        profileDialog.setVisible(true);
    }

    private JTextField createField(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label + ":");
        JTextField txtField = new JTextField(value);
        panel.add(lbl);
        panel.add(txtField);
        return txtField;
    }

    private void displayUserInfo(User user) {
        // Affiche les informations du profil utilisateur dans une fenêtre distincte
        JDialog infoDialog = new JDialog((JFrame) null, "Informations du Profil", true);
        infoDialog.setSize(400, 300);
        infoDialog.setLayout(new BorderLayout());
        infoDialog.setLocationRelativeTo(null);

        // Zone de texte pour afficher les informations
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);

        StringBuilder userInfo = new StringBuilder();
        userInfo.append("Nom complet: ").append(user.getFullName()).append("\n");
        userInfo.append("Adresse e-mail: ").append(user.getEmail()).append("\n");

        if (user instanceof Resident) {
            Resident resident = (Resident) user;
            userInfo.append("Date de naissance: ").append(resident.getBirthDay()).append("\n");
            userInfo.append("Adresse: ").append(resident.getAddress()).append("\n");
        }

        if (user instanceof Intervenant) {
            Intervenant intervenant = (Intervenant) user;
            userInfo.append("Type d'entreprise: ").append(intervenant.getType()).append("\n");
        }

        infoArea.setText(userInfo.toString());
        infoDialog.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        // Bouton pour fermer la fenêtre
        JButton btnClose = new JButton("Fermer");
        btnClose.addActionListener(e -> infoDialog.dispose());
        infoDialog.add(btnClose, BorderLayout.SOUTH);

        infoDialog.setVisible(true);
    }
}
