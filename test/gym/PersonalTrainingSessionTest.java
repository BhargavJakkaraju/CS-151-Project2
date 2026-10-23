package gym;

import java.time.LocalDateTime;
import java.util.List;

public class PersonalTrainingSessionTest {
    public static void main(String[] args) throws SessionFullException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        expectException(IllegalArgumentException.class,
                () -> new PersonalTrainingSession(
                        " ", "T001", start, 60),
                "Blank session ID is rejected");

        expectException(IllegalArgumentException.class,
                () -> new PersonalTrainingSession(
                        "S001", " ", start, 60),
                "Blank trainer ID is rejected");

        expectException(IllegalArgumentException.class,
                () -> new PersonalTrainingSession(
                        "S001", "T001", null, 60),
                "Null start time is rejected");

        expectException(IllegalArgumentException.class,
                () -> new PersonalTrainingSession(
                        "S001", "T001", start, 0),
                "Non-positive duration is rejected");

        PersonalTrainingSession session = new PersonalTrainingSession(
                "S001", "T001", start, 60);

        check(session.getClient() == null,
                "New session has no client");

        check(session.getExercises().isEmpty(),
                "New session has no exercises");

        check(session.getCompletedExercises().isEmpty(),
                "New session has no completed exercises");

        expectException(IllegalArgumentException.class,
                () -> session.addExercise(" "),
                "Blank exercise name is rejected");

        session.addExercise("Squat");
        session.addExercise("Deadlift");

        check(session.getExercises().size() == 2,
                "Exercises are added to the plan");

        expectException(IllegalArgumentException.class,
                () -> session.addExercise("squat"),
                "Duplicate exercise is rejected regardless of case");

        expectException(IllegalArgumentException.class,
                () -> session.removeExercise(" "),
                "Blank exercise removal is rejected");

        expectException(IllegalArgumentException.class,
                () -> session.removeExercise("Press"),
                "Removing an unknown exercise is rejected");

        expectException(IllegalArgumentException.class,
                () -> session.recordExerciseCompletion(" "),
                "Blank completion name is rejected");

        expectException(IllegalArgumentException.class,
                () -> session.recordExerciseCompletion("Press"),
                "Completion for an unknown exercise is rejected");

        session.recordExerciseCompletion("Squat");

        check(session.getCompletedExercises().size() == 1,
                "Completed exercise is recorded");

        expectException(IllegalStateException.class,
                () -> session.recordExerciseCompletion("squat"),
                "Duplicate completion is rejected");

        session.removeExercise("Squat");

        check(session.getExercises().size() == 1,
                "Removing an exercise reduces the plan size");

        check(session.getCompletedExercises().isEmpty(),
                "Removing an exercise clears its completion");

        session.addExercise("Squat");

        Member basicMember = new Member(
                "M001", "John", "john@example.com");
        Member premiumMember = new Member(
                "M002", "Pat", "pat@example.com");
        Member inactiveMember = new Member(
                "M003", "Noone", "none@example.com");
        Member otherMember = new Member(
                "M004", "Other", "other@example.com");

        basicMember.purchaseMembership("BASIC", 2);
        premiumMember.purchaseMembership("PREMIUM", 2);

        expectException(IllegalArgumentException.class,
                () -> session.calculatePrice(null),
                "calculatePrice rejects null member");

        expectException(IllegalStateException.class,
                () -> session.calculatePrice(basicMember),
                "calculatePrice rejects a non-client before assignment");

        try {
            session.assignClient(null);
            throw new AssertionError(
                    "FAIL: Null client is rejected"
                            + " - no exception thrown");
        } catch (IllegalArgumentException expected) {
            System.out.println("PASS: Null client is rejected");
        }

        try {
            session.assignClient(inactiveMember);
            throw new AssertionError(
                    "FAIL: Member without active membership is rejected"
                            + " - no exception thrown");
        } catch (IllegalStateException expected) {
            System.out.println(
                    "PASS: Member without active membership is rejected");
        }

        session.assignClient(basicMember);

        check(session.getClient() == basicMember,
                "Client is assigned successfully");

        check(session.calculatePrice(basicMember) == 60.0,
                "Standard client is charged $60");

        try {
            session.assignClient(premiumMember);
            throw new AssertionError(
                    "FAIL: Second client is rejected"
                            + " - no exception thrown");
        } catch (SessionFullException expected) {
            System.out.println(
                    "PASS: Second client is rejected"
                            + " with SessionFullException");
        }

        expectException(IllegalArgumentException.class,
                () -> session.removeMemberFromClass(null),
                "Null removal is rejected");

        expectException(IllegalArgumentException.class,
                () -> session.removeMemberFromClass(otherMember),
                "Removing the wrong member is rejected");

        session.recordExerciseCompletion("Deadlift");
        session.removeMemberFromClass(basicMember);

        check(session.getClient() == null,
                "Client is cleared after removal");

        check(session.getCompletedExercises().isEmpty(),
                "Completed exercises are cleared on removal");

        expectException(IllegalStateException.class,
                () -> session.removeMemberFromClass(basicMember),
                "Removing from an empty session is rejected");

        session.assignClient(premiumMember);

        check(session.calculatePrice(premiumMember) == 45.0,
                "Premium client is charged $45");

        List<String> exercises = session.getExercises();
        List<String> completed = session.getCompletedExercises();

        expectException(UnsupportedOperationException.class,
                () -> exercises.add("Press"),
                "Returned exercise list cannot be modified");

        expectException(UnsupportedOperationException.class,
                () -> completed.add("Press"),
                "Returned completed-exercise list cannot be modified");

        check(session.toString().contains("S001"),
                "toString includes the session ID");

        System.out.println(
                "\nAll PersonalTrainingSession tests passed.");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("FAIL: " + description);
        }

        System.out.println("PASS: " + description);
    }

    private static void expectException(
            Class<? extends RuntimeException> expectedType,
            Runnable action, String description) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (!expectedType.isInstance(exception)) {
                throw new AssertionError(
                        "FAIL: " + description
                                + " - unexpected exception", exception);
            }

            System.out.println("PASS: " + description);
            return;
        }

        throw new AssertionError(
                "FAIL: " + description + " - no exception thrown");
    }
}
