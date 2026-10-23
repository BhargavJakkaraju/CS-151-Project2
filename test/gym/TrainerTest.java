package gym;

import java.time.LocalDateTime;
import java.util.List;

public class TrainerTest {
    public static void main(String[] args) {
        expectException(IllegalArgumentException.class,
                () -> new Trainer(" ", "Alex", "Strength"),
                "Blank trainer ID is rejected");

        expectException(IllegalArgumentException.class,
                () -> new Trainer("T001", "  ", "Strength"),
                "Blank trainer name is rejected");

        expectException(IllegalArgumentException.class,
                () -> new Trainer("T001", "Alex", ""),
                "Blank specialty is rejected");

        Trainer trainer = new Trainer(
                " T001 ", " Alex ", " Strength ");

        check(trainer.getTrainerId().equals("T001"),
                "Trainer ID is trimmed");

        check(trainer.getName().equals("Alex"),
                "Trainer name is trimmed");

        check(trainer.getSpecialty().equals("Strength"),
                "Specialty is trimmed");

        check(trainer.getAssignedSessions().isEmpty(),
                "New trainer has no assigned sessions");

        check(trainer.getBlockedTimes().isEmpty(),
                "New trainer has no blocked times");

        LocalDateTime firstStart = LocalDateTime.now().plusDays(1);
        PersonalTrainingSession firstSession = new PersonalTrainingSession(
                "S001", "T001", firstStart, 60);

        expectException(IllegalArgumentException.class,
                () -> trainer.assignSession(null),
                "Null session is rejected");

        expectException(IllegalArgumentException.class,
                () -> trainer.assignSession(new PersonalTrainingSession(
                        "S002", "T999", firstStart.plusHours(2), 60)),
                "Session assigned to a different trainer is rejected");

        trainer.assignSession(firstSession);

        check(trainer.getAssignedSessions().size() == 1,
                "Session is added to the trainer");

        expectException(IllegalArgumentException.class,
                () -> trainer.assignSession(new PersonalTrainingSession(
                        "s001", "T001", firstStart.plusHours(5), 60)),
                "Duplicate session ID is rejected regardless of case");

        expectException(IllegalStateException.class,
                () -> trainer.assignSession(new PersonalTrainingSession(
                        "S003", "T001", firstStart, 60)),
                "Session with the same start time is rejected");

        check(trainer.getAssignedSessions().size() == 1,
                "Rejected assignments leave the list unchanged");

        expectException(IllegalArgumentException.class,
                () -> trainer.blockUnavailableTime(null),
                "Null blocked time is rejected");

        LocalDateTime blockedStart = firstStart.plusDays(1);
        trainer.blockUnavailableTime(blockedStart);

        check(trainer.getBlockedTimes().size() == 1,
                "Blocked time is recorded");

        expectException(IllegalArgumentException.class,
                () -> trainer.blockUnavailableTime(blockedStart),
                "Duplicate blocked time is rejected");

        expectException(IllegalStateException.class,
                () -> trainer.blockUnavailableTime(firstStart),
                "Blocked time overlapping an assigned session is rejected");

        expectException(IllegalStateException.class,
                () -> trainer.assignSession(new PersonalTrainingSession(
                        "S004", "T001", blockedStart, 60)),
                "Session that falls on a blocked time is rejected");

        expectException(IllegalArgumentException.class,
                () -> trainer.isAvailable(null),
                "Null time cannot be checked for availability");

        check(!trainer.isAvailable(firstStart),
                "Trainer is not available during an assigned session");

        check(!trainer.isAvailable(blockedStart),
                "Trainer is not available at a blocked time");

        check(trainer.isAvailable(firstStart.plusDays(7)),
                "Trainer is available at an unused time");

        expectException(IllegalArgumentException.class,
                () -> trainer.cancelAssignment(" "),
                "Blank session ID is rejected");

        expectException(IllegalArgumentException.class,
                () -> trainer.cancelAssignment("UNKNOWN"),
                "Unknown session ID is rejected");

        trainer.cancelAssignment("s001");

        check(trainer.getAssignedSessions().isEmpty(),
                "Cancellation removes the assigned session");

        check(trainer.isAvailable(firstStart),
                "Trainer is available again after cancellation");

        List<FitnessSession> sessions = trainer.getAssignedSessions();
        List<LocalDateTime> blockedTimes = trainer.getBlockedTimes();

        expectException(UnsupportedOperationException.class,
                () -> sessions.add(null),
                "Returned session list cannot be modified");

        expectException(UnsupportedOperationException.class,
                () -> blockedTimes.add(LocalDateTime.now()),
                "Returned blocked-times list cannot be modified");

        check(trainer.toString().contains("T001"),
                "toString includes the trainer ID");

        System.out.println("\nAll Trainer tests passed.");
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
