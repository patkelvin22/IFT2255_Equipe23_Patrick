package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import models.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import services.ProjectsServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ProjectsServicesTest {

    private ProjectsServices projectsServices;
    private Resident resident;
    private Intervenant intervenant;
    private ManageUsers mockManageUsers;


    @BeforeEach
    public void setUp() {
        mockManageUsers = Mockito.mock(ManageUsers.class);
        resident = new Resident("Test resident", "test@test.com", "passwordtest", "Resident", "testResident", "514-123-4567", "123 rue de la rue, H1H 1H1, Montréal, QC", "1990-01-01", "Ville-Marie");
        intervenant = new Intervenant("Test intervenant", "testIntervenant@gmail.com", "passwordtest", "Intervenant", "12345678", "TestType");
        projectsServices = Mockito.spy(new ProjectsServices());
    }

    /**
     * Test de soumission d'une nouvelle requête résidentielle
     */
    @Test
    public void testSubmitRequest() {
        // Sauvegarde de l'entrée standard d'origine
        InputStream originalIn = System.in;

        // Simulation des entrées utilisateur pour soumettre une nouvelle requête
        String simulatedInput = "2\nTitre de la requête\nDescription de la requête\n1\n2024-11-22\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        try {
            projectsServices.scanner = new Scanner(System.in);

            // Appel de la méthode à tester
            projectsServices.submitRequest(resident);


            // Vérification qu'une nouvelle requête a été ajoutée
            assertFalse(resident.getWorkRequests().isEmpty(), "Une nouvelle requête devrait être ajoutée au résident.");
            assertEquals("Titre de la requête", resident.getWorkRequests().get(0).getTitle(), "Le titre de la requête doit correspondre à l'entrée.");
            assertEquals("Description de la requête", resident.getWorkRequests().get(0).getDescription(), "La description doit correspondre à l'entrée.");
            assertEquals("Fenêtres", resident.getWorkRequests().get(0).getWorkType(), "Le type de travaux doit correspondre à l'entrée.");
        } finally {
            // Restauration de l'entrée standard
            System.setIn(originalIn);
            projectsServices.scanner = new Scanner(System.in);
        }
    }

    /**
     * Test de soumission de nouveaux travaux
     */
    @Test
    public void testSubmitNewWork() {
        // Sauvegarde de l'entrée standard d'origine
        InputStream originalIn = System.in;

        // Simulation des entrées utilisateur pour soumettre de nouveaux travaux
        String simulatedInput = "Titre\nDescription\n0\nQuartierTest\nRueTest\n2024-01-01\n2024-02-01\n08:00-17:00\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        try {
            projectsServices.scanner = new Scanner(System.in);

            // Appel de la méthode à tester
            projectsServices.submitNewWork(intervenant);
            String[] roadWorkTypes = projectsServices.roadWorkTypes;

            // Vérification qu'une nouvelle soumission de travaux a été ajoutée
            List<WorkProject> roadWorks = projectsServices.getRoadWorksList();
            assertFalse(roadWorks.isEmpty(), "Une nouvelle soumission de travaux doit être ajoutée.");
            assertEquals(roadWorkTypes[0] + " " +intervenant.getFullName(), roadWorks.get(roadWorks.size() - 1).getTitle(), "Le titre doit correspondre à l'entrée.");
            assertEquals(roadWorkTypes[0], roadWorks.get(roadWorks.size() - 1).getReasonCategory(), "Le type de travaux doit correspondre à l'entrée.");
        } finally {
            // Restauration de l'entrée standard
            System.setIn(originalIn);
            projectsServices.scanner = new Scanner(System.in);
        }
    }

    /**
     * Test pour la modification d'un travail
     */
    @Test
    public void testUpdateWork(){
        // Sauvegarde de l'entrée standard d'origine
        InputStream originalIn = System.in;

        // Simulation des entrées utilisateur pour modifier un travail
        String simulatedInput = "3\nTest Status\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        try {
            projectsServices.scanner = new Scanner(System.in);
            projectsServices.manageUsers = mockManageUsers;

            WorkProject existingWork = new WorkProject("123", "QuartierTest", "En cours", "Route", "Entreprise publique", "Intervenant Test");
            intervenant.addWorkProject(existingWork);
            // Appel de la méthode à tester
            projectsServices.updateWork(intervenant);

            // Vérification que le travail a été modifié
            WorkProject updatedWork = intervenant.getWorkProjects().get(0);
            assertEquals("Test Status", updatedWork.getCurrentStatus(), "Le statut du travail doit être mis à jour.");

            verify(mockManageUsers, times(1)).updateWorkProject(intervenant,0, updatedWork);
        } finally {
            // Restauration de l'entrée standard
            System.setIn(originalIn);
            projectsServices.scanner = new Scanner(System.in);
        }
    }
    
    /**
     * Test pour la vérification du chargement des travaux dans le API
     */
    @Test
    public void testGetRoadWorksList() {
        ProjectsServices projectsServices = new ProjectsServices();
        List<WorkProject> roadWorks = projectsServices.getRoadWorksList();
        
        assertNotNull(roadWorks, "La liste des travaux routiers ne doit pas être null.");
        assertTrue(roadWorks.size() > 0, "La liste des travaux routiers doit contenir au moins un élément.");
    }
    

    /**
     * Test pour la vérification du chargement des observations dans le API
     */
    @Test
    public void testGetObservationsList() {
        ProjectsServices projectsServices = new ProjectsServices();
        List<Obstacles> observations = projectsServices.getObstaclesList();
        
        assertNotNull(observations, "La liste des observations ne doit pas être null.");
        assertTrue(observations.size() > 0, "La liste des observations doit contenir au moins un élément.");
    }
}