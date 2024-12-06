package models;

 // Classe pour représenter une plage horaire
public class TimeSlot {
    private final String day;
    private final String startTime;
    private final String endTime;

    public TimeSlot(String day, String startTime, String endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return day + " : " + startTime + " - " + endTime;
    }
}
