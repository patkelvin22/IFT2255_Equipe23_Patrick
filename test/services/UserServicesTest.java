package services;

import services.UserServices;
import ui.MainMenu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import models.*;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class UserServicesTest{
    
    private UserServices userServices;
    private ManageUsers mockManageUsers;
    private Resident resident;
    private Intervenant intervenant;

    @BeforeEach
    public void setUp(){
        mockManageUsers = Mockito.mock(ManageUsers.class);
        userServices = new UserServices(mockManageUsers);
        resident = new Resident("Test resident", "test@test.com", "passwordtest", "Resident", "testResident", "514-123-4567", "123 rue de la rue, H1H 1H1, Montréal, QC", "1990-01-01", "Ville-Marie");
        when(mockManageUsers.getResident(anyString())).thenReturn(resident);
        intervenant = new Intervenant("Test intervenant", "testIntervenant@gmail.com", "passwordtest", "Intervenant", "12345678", "TestType");
        when(mockManageUsers.getIntervenant(anyString())).thenReturn(intervenant);
    }

    @Test
    public void testModifyProfile() {
        // Sauvegarde de l'entrée standard d'origine
        InputStream originalIn = System.in;

        // Simulation de l'entrée utilisateur
        String simulatedInput = "1\nOui\n3\nNon\nTest resident updated\n456 rue de la rue, H2H 2H2, Montréal, QC";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        try {
            userServices.scanner = new Scanner(System.in);
            // Appel de la méthode à tester
            userServices.modifyProfile(resident);

            // Assertions pour vérifier les mises à jour
            assertEquals("Test resident updated", resident.getFullName(), "Le nom complet n'a pas été mis à jour.");
            assertEquals("456 rue de la rue, H2H 2H2, Montréal, QC", resident.getAddress(), "L'adresse n'a pas été mise à jour.");

            // Vérification que la méthode updateUser a été appelée
            verify(mockManageUsers, times(1)).updateUser(resident);
        } finally {
            // Restauration de l'entrée standard
            System.setIn(originalIn);
            userServices.scanner = MainMenu.scanner;
        }
    }

    @Test
    public void testModifyProfileInvalidInput() {
        // Sauvegarde de l'entrée standard d'origine
        InputStream originalIn = System.in;
    
        // Simulation de l'entrée utilisateur avec des entrées invalides
        String simulatedInput = "12\nOui\n3\nNon\n456 rue de la rue, H2H 2H2, Montréal, QC";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
    
        try {
            userServices.scanner = new Scanner(System.in);

            // Appel de la méthode à tester
            userServices.modifyProfile(resident);
    
            // Assertions pour vérifier que les modifications valides sont appliquées
            assertEquals("Test resident", resident.getFullName(), "Le nom complet n'a pas été mis à jour.");
            assertEquals("456 rue de la rue, H2H 2H2, Montréal, QC", resident.getAddress(), "L'adresse n'a pas été mise à jour.");
    
            // Vérification que la méthode updateUser a été appelée
            verify(mockManageUsers, times(1)).updateUser(resident);
        } finally {
            // Restauration de l'entrée standard
            System.setIn(originalIn);
            userServices.scanner = MainMenu.scanner;
        }
    }

    @Test
    public void testModifyProfileIntervenant(){
        // Sauvegarde de l'entrée standard d'origine
        InputStream originalIn = System.in;

        // Simulation de l'entrée utilisateur
        String simulatedInput = "2\nOui\n3\nNon\nnewpasswordTest\nTestType2";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        try {
            userServices.scanner = new Scanner(System.in);
            // Appel de la méthode à tester
            userServices.modifyProfile(intervenant);

            // Assertions pour vérifier les mises à jour
            assertEquals("newpasswordTest", intervenant.getPassword(), "Le mot de passe n'a pas été mis à jour.");
            assertEquals("TestType2", intervenant.getType(), "Le type n'a pas été mis à jour.");

            // Vérification que la méthode updateUser a été appelée
            verify(mockManageUsers, times(1)).updateUser(intervenant);
        } finally {
            // Restauration de l'entrée standard
            System.setIn(originalIn);
            userServices.scanner = MainMenu.scanner;
        }
    }
    
    @Test
    public void testSignIn() {
        // Save the original System.in
        InputStream originalIn = System.in;

        // Simulate user input for signing up as an Intervenant
        String simulatedInput = "1\n09876543\nTest Intervenant\npassword123\nintervenant@gmail.com\n2";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        try {
            userServices.scanner = new Scanner(System.in);

            // Call the method to test
            userServices.signUp();

            // Verify the new Intervenant has been added
            verify(mockManageUsers, times(1)).addIntervenant(any(Intervenant.class));

            // Optionally, capture the added Intervenant and assert its properties
            ArgumentCaptor<Intervenant> intervenantCaptor = ArgumentCaptor.forClass(Intervenant.class);
            verify(mockManageUsers).addIntervenant(intervenantCaptor.capture());
            Intervenant addedIntervenant = intervenantCaptor.getValue();

            assertEquals("Test Intervenant", addedIntervenant.getFullName(), "Le nom complet de l'intervenant n'a pas été défini correctement.");
            assertEquals("intervenant@gmail.com", addedIntervenant.getEmail(), "L'adresse e-mail de l'intervenant n'a pas été définie correctement.");
            assertEquals("password123", addedIntervenant.getPassword(), "Le mot de passe de l'intervenant n'a pas été défini correctement.");
            assertEquals("Entreprise privée", addedIntervenant.getType(), "Le type de l'entreprise de l'intervenant n'a pas été défini correctement.");
            assertEquals("09876543", addedIntervenant.getId(), "L'identifiant de l'intervenant n'a pas été défini correctement.");

        } finally {
            // Restore the original System.in
            System.setIn(originalIn);
            userServices.scanner = MainMenu.scanner;
        }
    }
}