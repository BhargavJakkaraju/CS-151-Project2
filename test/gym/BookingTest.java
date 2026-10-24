package gym;

import java.time.LocalDateTime;

public class BookingTest {
    public static void main(String[] args)
            throws SessionFullException, MemberAlreadyInClassException {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        LocalDateTime past = LocalDateTime.now().minusDays(1);

        Member basicMember = new Member(
                "M001", "John", "john@example.com");
        Member premiumMember = new Member(
                "M002", "Pat", "pat@example.com");
        Member otherMember = new Member(
                "M003", "Other", "other@example.com");
        Member inactiveMember = new Member(
                "M004", "Noone", "none@example.com");
        Member waitingMember = new Member(
                "M005", "Wendy", "wendy@example.com");

        basicMember.purchaseMembership("BASIC", 2);
        premiumMember.purchaseMembership("PREMIUM", 2);
        otherMember.purchaseMembership("BASIC", 2);
        waitingMember.purchaseMembership("BASIC", 2);

        // ---------- construction ----------

        PersonalTrainingSession personal = new PersonalTrainingSession(
                "S001", "T001", future, 60);
        PersonalTrainingSession emptyPersonal = new PersonalTrainingSession(
                "S002", "T001", future, 60);

        Booking personalBooking = new Booking(basicMember, personal);

        check(personalBooking.getMember() == basicMember,
                "Booking stores the member");

        check(personalBooking.getSession() == personal,
                "Booking stores the session");

        check(personalBooking.getStatus() == Booking.STATUS.BOOKED,
                "New booking has BOOKED status");

        check(personal.getClient() == basicMember,
                "Booking adds the member to the session");

        check(!personalBooking.wasAttended(),
                "New booking is not marked attended");

        expectCheckedException(SessionFullException.class,
                () -> new Booking(premiumMember, personal),
                "Booking a full session is rejected");

        expectCheckedException(IllegalStateException.class,
                () -> new Booking(inactiveMember, emptyPersonal),
                "Booking without an active membership is rejected");

        expectCheckedException(IllegalArgumentException.class,
                () -> new Booking(null, emptyPersonal),
                "Booking with a null member is rejected");

        GroupClass groupA = new GroupClass(1, "G001", "T001", future, 60);

        Booking groupBooking = new Booking(basicMember, groupA);

        check(groupA.isEnrolled(basicMember),
                "Booking enrolls the member in a group class");

        expectCheckedException(MemberAlreadyInClassException.class,
                () -> new Booking(basicMember, groupA),
                "Duplicate booking is rejected");

        Booking waitingBooking = new Booking(waitingMember, groupA);

        check(waitingBooking.getStatus() == Booking.STATUS.BOOKED,
                "Waiting list booking has BOOKED status");

        check(!groupA.isEnrolled(waitingMember),
                "Waiting list booking is not enrolled");

        // ---------- cancelBooking ----------

        waitingBooking.cancelBooking();

        check(waitingBooking.getStatus() == Booking.STATUS.CANCELLED,
                "Cancelling a waiting list booking sets CANCELLED");

        check(groupA.isEnrolled(basicMember),
                "Cancelling a waiting booking leaves the class unchanged");

        Booking promotedBooking = new Booking(waitingMember, groupA);
        groupBooking.cancelBooking();

        check(groupBooking.getStatus() == Booking.STATUS.CANCELLED,
                "Cancelling sets CANCELLED");

        check(!groupA.isEnrolled(basicMember),
                "Cancelling removes the member from the session");

        check(groupA.isEnrolled(waitingMember),
                "Cancelling promotes the first waiting member");

        expectException(IllegalStateException.class,
                () -> groupBooking.cancelBooking(),
                "Cancelling twice is rejected");

        Booking personalCancel = new Booking(otherMember, emptyPersonal);
        personalCancel.cancelBooking();

        check(emptyPersonal.getClient() == null,
                "Cancelling clears a personal session client");

        // ---------- rescheduleBooking ----------

        GroupClass groupB = new GroupClass(2, "G002", "T001", future, 60);
        GroupClass groupC = new GroupClass(2, "G003", "T001", future, 60);
        GroupClass groupD = new GroupClass(2, "G004", "T001", future, 60);

        Booking moveBooking = new Booking(otherMember, groupB);

        expectCheckedException(IllegalArgumentException.class,
                () -> moveBooking.rescheduleBooking(null),
                "Rescheduling to a null session is rejected");

        expectCheckedException(IllegalArgumentException.class,
                () -> moveBooking.rescheduleBooking(groupB),
                "Rescheduling to the same session is rejected");

        moveBooking.rescheduleBooking(groupC);

        check(moveBooking.getSession() == groupC,
                "Rescheduling changes the booking's session");

        check(groupC.isEnrolled(otherMember),
                "Rescheduling enrolls the member in the new session");

        check(!groupB.isEnrolled(otherMember),
                "Rescheduling removes the member from the old session");

        check(moveBooking.getStatus() == Booking.STATUS.BOOKED,
                "Rescheduled booking stays BOOKED");

        expectCheckedException(SessionFullException.class,
                () -> moveBooking.rescheduleBooking(personal),
                "Rescheduling to a full session is rejected");

        check(moveBooking.getSession() == groupC
                && groupC.isEnrolled(otherMember),
                "Failed reschedule keeps the original session");

        Booking duplicateBooking = new Booking(otherMember, groupD);

        expectCheckedException(MemberAlreadyInClassException.class,
                () -> moveBooking.rescheduleBooking(groupD),
                "Rescheduling to a session the member is in is rejected");

        check(moveBooking.getSession() == groupC
                && groupC.isEnrolled(otherMember),
                "Duplicate reschedule keeps the original session");

        Booking cancelledBooking = new Booking(premiumMember, groupB);
        cancelledBooking.cancelBooking();

        expectCheckedException(IllegalStateException.class,
                () -> cancelledBooking.rescheduleBooking(groupC),
                "Rescheduling a cancelled booking is rejected");

        // ---------- markAttendence ----------

        GroupClass pastGroup = new GroupClass(1, "G005", "T001", past, 60);
        Booking attendedBooking = new Booking(basicMember, pastGroup);
        Booking waitingPastBooking = new Booking(waitingMember, pastGroup);

        expectException(IllegalArgumentException.class,
                () -> attendedBooking.markAttendence(null),
                "Null attendance is rejected");

        expectException(IllegalStateException.class,
                () -> waitingPastBooking.markAttendence(true),
                "Waiting list booking cannot have attendance recorded");

        check(waitingPastBooking.getStatus() == Booking.STATUS.BOOKED,
                "Rejected attendance leaves the status unchanged");

        attendedBooking.markAttendence(true);

        check(attendedBooking.getStatus() == Booking.STATUS.ATTENDED,
                "Attendance sets ATTENDED");

        check(attendedBooking.wasAttended(),
                "wasAttended is true after attending");

        expectException(IllegalStateException.class,
                () -> attendedBooking.markAttendence(false),
                "Attendance cannot be recorded twice");

        expectException(IllegalStateException.class,
                () -> attendedBooking.cancelBooking(),
                "Cancelling an attended booking is rejected");

        expectCheckedException(IllegalStateException.class,
                () -> attendedBooking.rescheduleBooking(groupC),
                "Rescheduling an attended booking is rejected");

        PersonalTrainingSession pastPersonal = new PersonalTrainingSession(
                "S003", "T001", past, 60);
        Booking missedBooking = new Booking(premiumMember, pastPersonal);
        missedBooking.markAttendence(false);

        check(missedBooking.getStatus() == Booking.STATUS.MISSED,
                "Missed attendance sets MISSED");

        check(!missedBooking.wasAttended(),
                "wasAttended is false after missing");

        expectException(IllegalStateException.class,
                () -> personalBooking.markAttendence(true),
                "Attendance before the session ends is rejected");

        check(personalBooking.getStatus() == Booking.STATUS.BOOKED,
                "Early attendance attempt leaves the status unchanged");

        GroupClass inProgress = new GroupClass(
                2, "G006", "T001", LocalDateTime.now().minusMinutes(30), 60);
        Booking inProgressBooking = new Booking(basicMember, inProgress);

        expectException(IllegalStateException.class,
                () -> inProgressBooking.markAttendence(true),
                "Attendance during the session is rejected");

        GroupClass pastGroupTwo = new GroupClass(
                2, "G007", "T001", past, 60);
        Booking pastCancelled = new Booking(basicMember, pastGroupTwo);
        pastCancelled.cancelBooking();

        expectException(IllegalStateException.class,
                () -> pastCancelled.markAttendence(true),
                "Attendance on a cancelled booking is rejected");

        // ---------- toString ----------

        check(personalBooking.toString().contains("S001"),
                "toString includes the session ID");

        check(personalBooking.toString().contains("Member:"),
                "toString includes the member");

        System.out.println("\nAll Booking tests passed.");
    }

    private interface ThrowingAction {
        void run() throws Exception;
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

    private static void expectCheckedException(
            Class<? extends Exception> expectedType,
            ThrowingAction action, String description) {
        try {
            action.run();
        } catch (Exception exception) {
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