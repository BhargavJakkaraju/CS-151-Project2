package gym;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private final String trainerId;
    private final String name;
    private final String specialty;
    private final List<LocalDateTime> blockedTimes;
    private final List<FitnessSession> assignedSessions;

    public Trainer(String trainerId, String name, String specialty) {
        this.trainerId = requireText(trainerId, "Trainer ID");
        this.name = requireText(name, "Name");
        this.specialty = requireText(specialty, "Specialty");
        this.blockedTimes = new ArrayList<>();
        this.assignedSessions = new ArrayList<>();
    }

    public void assignSession(FitnessSession session) {
        if (session == null) {
            throw new IllegalArgumentException(
                    "Session cannot be null.");
        }

        if (!trainerId.equalsIgnoreCase(session.getTrainerId())) {
            throw new IllegalArgumentException(
                    "This session is assigned to a different trainer.");
        }

        for (FitnessSession existing : assignedSessions) {
            if (existing.getSessionId()
                    .equalsIgnoreCase(session.getSessionId())) {
                throw new IllegalArgumentException(
                        "This session is already assigned to the trainer.");
            }

            if (existing.getStartTime().equals(session.getStartTime())) {
                throw new IllegalStateException(
                        "The trainer already has a session at that time.");
            }
        }

        for (LocalDateTime blocked : blockedTimes) {
            if (blocked.equals(session.getStartTime())) {
                throw new IllegalStateException(
                        "The trainer is not available at that time.");
            }
        }

        assignedSessions.add(session);
    }

    public void cancelAssignment(String sessionId) {
        String trimmedId = requireText(sessionId, "Session ID");

        for (int i = 0; i < assignedSessions.size(); i++) {
            FitnessSession existing = assignedSessions.get(i);
            if (existing.getSessionId().equalsIgnoreCase(trimmedId)) {
                assignedSessions.remove(i);
                return;
            }
        }

        throw new IllegalArgumentException(
                "This trainer is not assigned to that session.");
    }

    public void blockUnavailableTime(LocalDateTime startTime) {
        LocalDateTime validStart = requireStartTime(startTime);

        for (FitnessSession existing : assignedSessions) {
            if (existing.getStartTime().equals(validStart)) {
                throw new IllegalStateException(
                        "That time overlaps an assigned session.");
            }
        }

        for (LocalDateTime blocked : blockedTimes) {
            if (blocked.equals(validStart)) {
                throw new IllegalArgumentException(
                        "That time is already blocked.");
            }
        }

        blockedTimes.add(validStart);
    }

    public boolean isAvailable(LocalDateTime startTime) {
        LocalDateTime validStart = requireStartTime(startTime);

        for (LocalDateTime blocked : blockedTimes) {
            if (blocked.equals(validStart)) {
                return false;
            }
        }

        for (FitnessSession existing : assignedSessions) {
            if (existing.getStartTime().equals(validStart)) {
                return false;
            }
        }

        return true;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public List<FitnessSession> getAssignedSessions() {
        return List.copyOf(assignedSessions);
    }

    public List<LocalDateTime> getBlockedTimes() {
        return List.copyOf(blockedTimes);
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

    @Override
    public String toString() {
        return "Trainer ID: " + trainerId
                + ", Name: " + name
                + ", Specialty: " + specialty
                + ", Assigned sessions: " + assignedSessions.size()
                + ", Blocked times: " + blockedTimes.size();
    }
}