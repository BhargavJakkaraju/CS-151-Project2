package gym;

import java.time.LocalDate;

public class Member {
    private final String memberId;
    private final String name;
    private final String email;
    private String membershipType;
    private LocalDate expirationDate;
    private boolean frozen;

    public Member(String memberId, String name, String email) {
        this.memberId = requireText(memberId, "Member ID");
        this.name = requireText(name, "Name");
        this.email = requireText(email, "Email");
        this.membershipType = "NONE";
        this.expirationDate = null;
        this.frozen = false;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank.");
        }
        return value.trim();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean hasActiveMembership() {
        return expirationDate != null
                && !LocalDate.now().isAfter(expirationDate)
                && !frozen;
    }

        public void purchaseMembership(String type, int months) {
        String validType = requireText(type, "Membership type")
                .toUpperCase(java.util.Locale.ROOT);

        if (!validType.equals("BASIC") && !validType.equals("PREMIUM")) {
            throw new IllegalArgumentException(
                    "Membership type must be BASIC or PREMIUM.");
        }

        validateMonths(months);

        if (expirationDate != null
                && !LocalDate.now().isAfter(expirationDate)) {
            throw new IllegalStateException(
                    "You already have a membership. Renew it instead.");
        }

        membershipType = validType;
        expirationDate = LocalDate.now().plusMonths(months);
        frozen = false;
    }

    public void renewMembership(int months) {
        validateMonths(months);

        if (expirationDate == null) {
            throw new IllegalStateException(
                    "Purchase a membership before renewing.");
        }

        if (frozen) {
            throw new IllegalStateException(
                    "Resume your membership before renewing.");
        }

        LocalDate today = LocalDate.now();
        LocalDate renewalStart = expirationDate.isBefore(today)
                ? today : expirationDate;

        expirationDate = renewalStart.plusMonths(months);
    }

    public void freezeMembership() {
        if (!hasActiveMembership()) {
            throw new IllegalStateException(
                    "Only an active membership can be frozen.");
        }

        frozen = true;
    }

    public void resumeMembership() {
        if (!frozen) {
            throw new IllegalStateException(
                    "Your membership is not frozen.");
        }

        if (LocalDate.now().isAfter(expirationDate)) {
            throw new IllegalStateException(
                    "Your membership expired. Purchase a new one.");
        }

        frozen = false;
    }

    private static void validateMonths(int months) {
        if (months < 1 || months > 12) {
            throw new IllegalArgumentException(
                    "Choose a membership length from 1 to 12 months.");
        }
    }

        @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Member other = (Member) object;
        return memberId.equalsIgnoreCase(other.memberId);
    }

    @Override
    public int hashCode() {
        return memberId.toUpperCase(java.util.Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return "Member ID: " + memberId
                + ", Name: " + name
                + ", Membership: " + membershipType
                + ", Expiration: " + expirationDate
                + ", Frozen: " + frozen;
    }
}