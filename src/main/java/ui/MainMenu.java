package ui;

import javax.swing.*;
import java.awt.*;

import models.*;
import services.*;

public class MainMenu extends JFrame {
	
    private static final long serialVersionUID = 1L;

	private JButton btnSeConnecter;
    private JButton btnSinscrire;
    private JButton btnQuitter;

    private UserServices userServices;
    private ManageUsers manageUsers;

    public MainMenu(UserServices userServices, ManageUsers manageUsers) {
    	this.userServices = userServices;

        setTitle("MaVille");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        JLabel lblBienvenue = new JLabel("Bienvenue à MaVille!", JLabel.CENTER);
        lblBienvenue.setFont(new Font("Arial", Font.BOLD, 16));
        getContentPane().add(lblBienvenue, BorderLayout.NORTH);

        JLabel lblLogo = new JLabel(new ImageIcon("src/main/config/logoMaVille.png"));
        lblLogo.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(lblLogo, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new GridLayout(1, 3));
        btnSeConnecter = new JButton("Se connecter");
        btnSinscrire = new JButton("S'inscrire");
        btnQuitter = new JButton("Quitter");

        panelButtons.add(btnSeConnecter);
        panelButtons.add(btnSinscrire);
        panelButtons.add(btnQuitter);

        getContentPane().add(panelButtons, BorderLayout.SOUTH);

        userServices.setOnLoginSuccess(() -> this.dispose());

        // Associer les actions aux boutons
        initializeActions();
    }
    
    private void initializeActions() {
        // Action pour "Se connecter"
        btnSeConnecter.addActionListener(e -> seConnecter());

        // Action pour "S'inscrire"
        btnSinscrire.addActionListener(e -> sinscrire());

        // Action pour "Quitter"
        btnQuitter.addActionListener(e -> quitter());
    }

    // Méthodes liées aux actions
    private void seConnecter() {
        userServices.logIn();
    }

    private void sinscrire() {
        User user = userServices.signUp();
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Inscription réussie !");
            dispose();
            if (user instanceof Resident){
                new ResidentMenu((Resident) user, userServices, manageUsers).setVisible(true);
            }
            else if (user instanceof Intervenant){
                new IntervenantMenu((Intervenant) user, userServices, manageUsers).setVisible(true);
            }    
        }
    }

    private void quitter() {
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir quitter ?", "Quitter", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManageUsers manageUsers = ManageUsers.getInstance("jdbc:mysql://junction.proxy.rlwy.net:18467/railway", "root", "cdjszbwJUeECRcjpMCpFVLAORCuWIHeY");
            UserServices userServices = new UserServices(manageUsers);
            MainMenu mainMenu = new MainMenu(userServices, manageUsers);
            mainMenu.setVisible(true);
        });
    }
}