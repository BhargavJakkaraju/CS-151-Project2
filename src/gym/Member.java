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

    @Override
    public String toString() {
        return "Member ID: " + memberId
                + ", Name: " + name
                + ", Membership: " + membershipType
                + ", Expiration: " + expirationDate
                + ", Frozen: " + frozen;
    }
}