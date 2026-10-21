package gym;

import java.time.LocalDate;

public class MemberTest {
    public static void main(String[] args) {
        Member member = new Member(
                "M001", "John", "john@example.com");

        check(!member.hasActiveMembership(),
                "New member has no active membership");

        expectException(IllegalArgumentException.class,
                () -> new Member(" ", "John", "john@example.com"),
                "Blank member ID is rejected");

        expectException(IllegalStateException.class,
                () -> member.renewMembership(1),
                "Cannot renew before purchasing");

        expectException(IllegalArgumentException.class,
                () -> member.purchaseMembership("GOLD", 1),
                "Invalid membership type is rejected");

        expectException(IllegalArgumentException.class,
                () -> member.purchaseMembership("BASIC", 0),
                "Zero months is rejected");

        expectException(IllegalArgumentException.class,
                () -> member.purchaseMembership("BASIC", 13),
                "More than 12 months is rejected");

        check(member.getMembershipType().equals("NONE")
                        && member.getExpirationDate() == null,
                "Failed purchases leave membership unchanged");

        LocalDate beforePurchase = LocalDate.now();
        member.purchaseMembership("basic", 1);
        LocalDate afterPurchase = LocalDate.now();

        check(member.getMembershipType().equals("BASIC"),
                "Membership type is normalized");

        check(member.hasActiveMembership(),
                "Purchased membership is active");

        LocalDate expiration = member.getExpirationDate();
        check(!expiration.isBefore(beforePurchase.plusMonths(1))
                        && !expiration.isAfter(afterPurchase.plusMonths(1)),
                "Purchase sets the correct expiration date");

        expectException(IllegalStateException.class,
                () -> member.purchaseMembership("PREMIUM", 1),
                "Cannot purchase over an existing membership");

        member.renewMembership(2);
        check(member.getExpirationDate().equals(expiration.plusMonths(2)),
                "Renewal extends the existing expiration date");

        LocalDate renewedExpiration = member.getExpirationDate();
        member.freezeMembership();

        check(member.isFrozen() && !member.hasActiveMembership(),
                "Frozen membership blocks access");

        expectException(IllegalStateException.class,
                () -> member.renewMembership(1),
                "Cannot renew a frozen membership");

        check(member.getExpirationDate().equals(renewedExpiration),
                "Freezing and rejected renewal preserve expiration");

        member.resumeMembership();
        check(!member.isFrozen() && member.hasActiveMembership(),
                "Resuming restores access");

        expectException(IllegalStateException.class,
                () -> member.resumeMembership(),
                "Cannot resume a membership that is not frozen");

        System.out.println("\nAll Member tests passed.");
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