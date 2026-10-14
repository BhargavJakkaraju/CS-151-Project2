package gym;

import java.time.LocalDateTime;

public abstract class FitnessSession {
    private final String sessionId;
    private String trainerId;
    private LocalDateTime startTime;
    private int durationMinutes;

    protected FitnessSession(String sessionId, String trainerId,
            LocalDateTime startTime, int durationMinutes) {
        this.sessionId = requireText(sessionId, "Session ID");
        this.trainerId = requireText(trainerId, "Trainer ID");
        this.startTime = requireStartTime(startTime);
        this.durationMinutes = requireDuration(durationMinutes);
    }

    public abstract double calculatePrice();

    public String getSessionId() {
        return sessionId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = requireText(trainerId, "Trainer ID");
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = requireStartTime(startTime);
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = requireDuration(durationMinutes);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank.");
        }
        return value.trim();
    }

    private static LocalDateTime requireStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException(
                    "Start time cannot be null.");
        }
        return startTime;
    }

    private static int requireDuration(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than 0.");
        }
        return durationMinutes;
    }

    @Override
    public String toString() {
        return "Session ID: " + sessionId
                + ", Trainer ID: " + trainerId
                + ", Start: " + startTime
                + ", Duration: " + durationMinutes + " minutes";
    }
}