package gym;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PersonalTrainingSession extends FitnessSession {
    private static final double STANDARD_PRICE = 60.0;
    private static final double PREMIUM_PRICE = 45.0;

    private Member client;
    private final List<String> exercises;
    private final List<String> completedExercises;

    public PersonalTrainingSession(String sessionId, String trainerId,
            LocalDateTime startTime, int durationMinutes) {
        super(sessionId, trainerId, startTime, durationMinutes);
        this.client = null;
        this.exercises = new ArrayList<>();
        this.completedExercises = new ArrayList<>();
    }

    @Override
    public double calculatePrice(Member member) {
        if(!member.equals(client)) {
            throw new IllegalStateException(
                    "Member is not a client in this session.");
        }
        if (client != null
                && client.getMembershipType().equals("PREMIUM")
                && client.hasActiveMembership()) {
            return PREMIUM_PRICE;
        }
        return STANDARD_PRICE;
    }

    @Override
    public void addToClass(Member member) throws SessionFullException {
        assignClient(member);
    }

    public void assignClient(Member client) throws SessionFullException {
        if (client == null) {
            throw new IllegalArgumentException(
                    "Client cannot be null.");
        }

        if (this.client != null) {
            throw new SessionFullException();
            // throw new IllegalStateException(
            //         "This personal training session already has a client.");
        }

        if (!client.hasActiveMembership()) {
            throw new IllegalStateException(
                    "An active, unfrozen membership is required.");
        }

        this.client = client;
    }

    @Override
    public void removeMemberFromClass(Member member) {
        if (member == null) {
            throw new IllegalArgumentException(
                    "Member cannot be null.");
        }

        if (client == null) {
            throw new IllegalStateException(
                    "This session has no client.");
        }

        if (!client.getMemberId().equalsIgnoreCase(
                member.getMemberId())) {
            throw new IllegalArgumentException(
                    "This member is not booked for this session.");
        }

        client = null;
        completedExercises.clear();
    }

    public void addExercise(String exerciseName) {
        String name = requireText(exerciseName, "Exercise name");

        for (String exercise : exercises) {
            if (exercise.equalsIgnoreCase(name)) {
                throw new IllegalArgumentException(
                        "That exercise is already on the plan.");
            }
        }

        exercises.add(name);
    }

    public void removeExercise(String exerciseName) {
        String name = requireText(exerciseName, "Exercise name");

        for (int i = 0; i < exercises.size(); i++) {
            if (exercises.get(i).equalsIgnoreCase(name)) {
                exercises.remove(i);
                completedExercises.remove(name);
                return;
            }
        }

        throw new IllegalArgumentException(
                "That exercise is not on the plan.");
    }

    public void recordExerciseCompletion(String exerciseName) {
        String name = requireText(exerciseName, "Exercise name");
        boolean found = false;

        for (String exercise : exercises) {
            if (exercise.equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                    "That exercise is not on the plan.");
        }

        for (String completed : completedExercises) {
            if (completed.equalsIgnoreCase(name)) {
                throw new IllegalStateException(
                        "That exercise is already completed.");
            }
        }

        completedExercises.add(name);
    }

    public Member getClient() {
        return client;
    }

    public List<String> getExercises() {
        return List.copyOf(exercises);
    }

    public List<String> getCompletedExercises() {
        return List.copyOf(completedExercises);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        String clientId = client == null ? "none" : client.getMemberId();

        return super.toString()
                + ", Client: " + clientId
                + ", Price: $" + calculatePrice(client)
                + ", Exercises: " + exercises.size()
                + ", Completed: " + completedExercises.size();
    }
}