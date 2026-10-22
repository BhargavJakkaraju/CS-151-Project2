package gym;

public class GymTest {
    public static void main(String[] args) {
        Gym gym = new Gym();
        Member member = new Member(
                "M001", "John", "john@example.com");

        gym.registerMember(member);

        check(gym.getMembers().size() == 1,
                "Member registration succeeds");

        check(gym.findMember(" m001 ") == member,
                "Member lookup ignores case and surrounding spaces");

        expectException(IllegalArgumentException.class,
                () -> gym.registerMember(null),
                "Null member is rejected");

        expectException(IllegalArgumentException.class,
                () -> gym.registerMember(new Member(
                        "m001", "Other", "other@example.com")),
                "Duplicate member ID is rejected");

        expectException(IllegalArgumentException.class,
                () -> gym.registerMember(new Member(
                        "M002", "Other", "JOHN@example.com")),
                "Duplicate email is rejected");

        check(gym.getMembers().size() == 1,
                "Rejected registrations leave the list unchanged");

        expectException(UnsupportedOperationException.class,
                () -> gym.getMembers().clear(),
                "Returned member list cannot be modified");

        expectException(IllegalStateException.class,
                () -> gym.checkInMember("M001"),
                "Member without a membership cannot check in");

        member.purchaseMembership("BASIC", 1);
        member.freezeMembership();

        expectException(IllegalStateException.class,
                () -> gym.checkInMember("M001"),
                "Frozen member cannot check in");

        member.resumeMembership();
        gym.checkInMember("M001");
        System.out.println("PASS: Active member can check in");

        expectException(IllegalStateException.class,
                () -> gym.checkInMember("m001"),
                "Second check-in on the same day is rejected");

        expectException(IllegalArgumentException.class,
                () -> gym.checkInMember("UNKNOWN"),
                "Unknown member cannot check in");

        gym.removeMember("m001");

        check(gym.getMembers().isEmpty()
                        && gym.findMember("M001") == null,
                "Removal deletes the member");

        expectException(IllegalArgumentException.class,
                () -> gym.removeMember("M001"),
                "Removing an unknown member is rejected");

        gym.registerMember(member);
        gym.checkInMember("M001");
        System.out.println(
                "PASS: Removal also clears the previous check-in record");

        testMemberLimit();

        System.out.println("\nAll Gym tests passed.");
    }

    private static void testMemberLimit() {
        Gym gym = new Gym();
        int limit = Gym.getMaximumInstances();

        check(limit == 100, "Shared object limit is 100");

        for (int i = 1; i <= limit; i++) {
            gym.registerMember(new Member(
                    "L" + i, "Member " + i,
                    "member" + i + "@example.com"));
        }

        check(gym.getMembers().size() == limit,
                "Gym accepts 100 members");

        expectException(IllegalStateException.class,
                () -> gym.registerMember(new Member(
                        "OVER", "Extra Member", "extra@example.com")),
                "The 101st member is rejected");

        check(gym.getMembers().size() == limit,
                "Rejected 101st registration preserves the member count");

        gym.removeMember("L1");
        gym.registerMember(new Member(
                "REPLACEMENT", "Replacement", "replacement@example.com"));

        check(gym.getMembers().size() == limit,
                "Removing a member frees registration capacity");
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