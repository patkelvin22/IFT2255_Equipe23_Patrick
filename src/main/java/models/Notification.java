package models;

import java.time.LocalDateTime;

/**
 * Classe représentant une notification envoyée à un résident ou un intervenant.
 */
public class Notification {
    private int id;
    private Resident resident; // Référence directe au résident destinataire
    private String message;
    private boolean isRead; // Indique si la notification a été lue
    private LocalDateTime timestamp;

    /**
     * Constructeur de la classe Notification.
     *
     * @param id        L'identifiant unique de la notification.
     * @param resident  L'objet résident qui reçoit la notification.
     * @param message   Le contenu du message de la notification.
     * @param isRead    Statut de lecture de la notification.
     * @param timestamp L'horodatage de la notification.
     */
    public Notification(Resident resident, String message, boolean isRead, LocalDateTime timestamp) {
        this.resident = resident;
        this.message = message;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", resident=" + (resident != null ? resident.getFullName() : "Non spécifié") +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}