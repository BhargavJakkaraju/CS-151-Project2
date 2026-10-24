package gym;

import java.time.LocalDateTime;

public class GroupClassTest {
    public static void main(String[] args)
            throws MemberAlreadyInClassException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        expectException(IllegalArgumentException.class,
                () -> new GroupClass(-5, "G001", "T001", start, 60),
                "Negative capacity is rejected");

        GroupClass groupClass = new GroupClass(
                2, "G001", "T001", start, 60);

        check(groupClass.getCapacity() == 2,
                "Capacity is stored");

        check(groupClass.toString().contains("People in class: 0"),
                "New class has no members");

        check(groupClass.toString().contains("People on waiting list: 0"),
                "New class has an empty waiting list");

        Member basicMember = new Member(
                "M001", "John", "john@example.com");
        Member premiumMember = new Member(
                "M002", "Pat", "pat@example.com");
        Member waitingMember = new Member(
                "M003", "Wendy", "wendy@example.com");
        Member inactiveMember = new Member(
                "M004", "Noone", "none@example.com");
        Member extraMember = new Member(
                "M005", "Extra", "extra@example.com");
        Member outsider = new Member(
                "M006", "Outsider", "outsider@example.com");

        basicMember.purchaseMembership("BASIC", 2);
        premiumMember.purchaseMembership("PREMIUM", 2);
        waitingMember.purchaseMembership("BASIC", 2);
        extraMember.purchaseMembership("BASIC", 2);
        outsider.purchaseMembership("BASIC", 2);

        try {
            groupClass.addToClass(null);
            throw new AssertionError(
                    "FAIL: Null member is rejected"
                            + " - no exception thrown");
        } catch (IllegalArgumentException expected) {
            System.out.println("PASS: Null member is rejected");
        }

        groupClass.addToClass(basicMember);
        groupClass.addToClass(premiumMember);

        check(groupClass.isEnrolled(basicMember),
                "First member is enrolled");

        check(groupClass.isEnrolled(premiumMember),
                "Second member is enrolled");

        groupClass.addToClass(waitingMember);

        check(!groupClass.isEnrolled(waitingMember),
                "Member beyond capacity is not enrolled");

        check(groupClass.toString().contains("People in class: 2"),
                "Class size stays at capacity");

        check(groupClass.toString().contains("People on waiting list: 1"),
                "Overflow member is placed on the waiting list");

        try {
            groupClass.addToClass(basicMember);
            throw new AssertionError(
                    "FAIL: Duplicate enrolled member is rejected"
                            + " - no exception thrown");
        } catch (MemberAlreadyInClassException expected) {
            System.out.println(
                    "PASS: Duplicate enrolled member is rejected");
        }

        try {
            groupClass.addToClass(waitingMember);
            throw new AssertionError(
                    "FAIL: Duplicate waiting member is rejected"
                            + " - no exception thrown");
        } catch (MemberAlreadyInClassException expected) {
            System.out.println(
                    "PASS: Duplicate waiting member is rejected");
        }

        expectException(IllegalArgumentException.class,
                () -> groupClass.calculatePrice(null),
                "calculatePrice rejects null member");

        expectException(IllegalStateException.class,
                () -> groupClass.calculatePrice(outsider),
                "calculatePrice rejects a member not in the class");

        check(groupClass.calculatePrice(basicMember) == 40.0,
                "Standard member is charged $40");

        check(groupClass.calculatePrice(premiumMember) == 25.0,
                "Premium member is charged $25");

        check(groupClass.calculatePrice(waitingMember) == 40.0,
                "Waiting list member is priced");

        expectException(IllegalArgumentException.class,
                () -> groupClass.removeMemberFromClass(null),
                "Null removal is rejected");

        expectException(IllegalStateException.class,
                () -> groupClass.removeMemberFromClass(outsider),
                "Removing an unknown member is rejected");

        groupClass.removeMemberFromClass(basicMember);

        check(!groupClass.isEnrolled(basicMember),
                "Removed member is no longer enrolled");

        check(groupClass.isEnrolled(waitingMember),
                "Removal promotes the first waiting member");

        check(groupClass.toString().contains("People on waiting list: 0"),
                "Promoted member leaves the waiting list");

        groupClass.addToClass(inactiveMember);
        groupClass.addToClass(extraMember);
        groupClass.removeMemberFromClass(premiumMember);

        check(!groupClass.isEnrolled(inactiveMember),
                "Member without active membership is not promoted");

        check(groupClass.isEnrolled(extraMember),
                "Next valid waiting member is promoted instead");

        groupClass.addToClass(basicMember);

        check(!groupClass.isEnrolled(basicMember),
                "Re-added member goes on the waiting list when full");

        groupClass.removeMemberFromClass(basicMember);

        check(groupClass.isEnrolled(waitingMember)
                && groupClass.isEnrolled(extraMember),
                "Removing a waiting member leaves the class unchanged");

        groupClass.addToClass(basicMember);
        groupClass.increaseCapacity(1);

        check(groupClass.getCapacity() == 3,
                "Capacity is increased");

        groupClass.increaseCapacity(7);
        groupClass.decreaseCapacity(4);

        check(groupClass.getCapacity() == 6,
                "Capacity is decreased when there is room");

        GroupClass emptyClass = new GroupClass(
                5, "G002", "T001", start, 60);

        check(groupClass.toString().contains("G001"),
                "toString includes the session ID");

        System.out.println("\nAll GroupClass tests passed.");
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