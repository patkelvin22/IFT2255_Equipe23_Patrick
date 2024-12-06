package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe WorkRequest représente une requête de travail soumise par un résident. 
 * Elle contient les informations relatives à la requête, telles que le titre, 
 * la description, le type de travail, les dates, le statut, le quartier, et 
 * les candidats potentiels (intervenants) associés à la requête.
 */
public class WorkRequest {
    private String title;
    private String description;
    private String workType;
    private LocalDate startDate;
    private String status;
    private String neighborhood;
    private List<Intervenant> candidates = new ArrayList<>();
    private Resident resident;

    // Constantes de statut
    /**
     * Statut indiquant que la requête est en attente de candidatures.
     */
    public static final String STATUS_PENDING = "En attente de candidature";

    /**
     * Statut indiquant que la requête est en cours de traitement.
     */
    public static final String STATUS_IN_PROGRESS = "En cours";

    /**
     * Statut indiquant que la requête est terminée.
     */
    public static final String STATUS_COMPLETED = "Terminé";

    /**
     * Constructeur de la classe WorkRequest.
     *
     * @param title        Le titre de la requête.
     * @param description  La description de la requête.
     * @param workType     Le type de travail demandé.
     * @param startDate    La date de début souhaitée pour le travail.
     * @param neighborhood Le quartier où les travaux doivent avoir lieu.
     * @param resident     Le résident qui a soumis la requête.
     */
    public WorkRequest(String title, String description, String workType, LocalDate startDate, String neighborhood, Resident resident) {
        this.title = title;
        this.description = description;
        this.workType = workType;
        this.startDate = startDate;
        this.neighborhood = neighborhood;
        this.resident = resident;
        this.status = STATUS_PENDING; // Par défaut, la requête est en attente
    }

    /**
     * Obtient le titre de la requête.
     *
     * @return Le titre de la requête.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Obtient la description de la requête.
     *
     * @return La description de la requête.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtient le type de travaux demandé.
     *
     * @return Le type de travaux.
     */
    public String getWorkType() {
        return workType;
    }

    /**
     * Obtient la date de début espérée pour les travaux.
     *
     * @return La date de début.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Obtient le statut actuel de la requête.
     *
     * @return Le statut actuel.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Obtient le quartier associé à la requête.
     *
     * @return Le quartier.
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Met à jour le statut de la requête.
     *
     * @param newStatus Le nouveau statut de la requête.
     */
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    /**
     * Ajoute un candidat (intervenant) à la liste des candidats.
     *
     * @param candidate L'intervenant candidat pour la requête.
     */
    public void addCandidate(Intervenant candidate) {
        candidates.add(candidate);
    }

    /**
     * Obtient la liste des candidats associés à la requête.
     *
     * @return Une liste d'intervenants candidats.
     */
    public List<Intervenant> getCandidates() {
        return candidates;
    }

    /**
     * Obtient le résident qui a soumis la requête.
     *
     * @return Le résident propriétaire de la requête.
     */
    public Resident getResident() {
        return resident;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de la requête.
     *
     * @return Une chaîne représentant les détails de la requête.
     */
    @Override
    public String toString() {
        return "Titre : " + title + "\n" +
               "Description : " + description + "\n" +
               "Type de travaux : " + workType + "\n" +
               "Date de début espéré : " + startDate + "\n" +
               "Statut : " + status + "\n";
    }
}