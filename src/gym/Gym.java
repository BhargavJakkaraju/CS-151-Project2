package gym;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gym {
    private static final int MAXIMUM_INSTANCES = 100;

    private final List<Member> members;
    private final List<Trainer> trainers;
    private final Map<String, LocalDate> lastCheckIns;

    public Gym() {
        members = new ArrayList<>();
        trainers = new ArrayList<>();
        lastCheckIns = new HashMap<>();
    }

    public static int getMaximumInstances() {
        return MAXIMUM_INSTANCES;
    }

    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException(
                    "Member cannot be null.");
        }

        for (Member existing : members) {
            boolean sameId = existing.getMemberId()
                    .equalsIgnoreCase(member.getMemberId());
            boolean sameEmail = existing.getEmail()
                    .equalsIgnoreCase(member.getEmail());

            if (sameId || sameEmail) {
                throw new IllegalArgumentException(
                        "A member with this ID or email already exists.");
            }
        }

        if (members.size() >= MAXIMUM_INSTANCES) {
            throw new IllegalStateException(
                    "The gym cannot register more than 100 members.");
        }

        members.add(member);
    }

    public void removeMember(String memberId) {
        Member member = findMember(memberId);

        if (member == null) {
            throw new IllegalArgumentException(
                    "Member not found.");
        }

        members.remove(member);
        lastCheckIns.remove(member.getMemberId());
    }

    public void checkInMember(String memberId) {
        Member member = findMember(memberId);

        if (member == null) {
            throw new IllegalArgumentException(
                    "Member not found.");
        }

        if (!member.hasActiveMembership()) {
            throw new IllegalStateException(
                    "An active, unfrozen membership is required.");
        }

        LocalDate today = LocalDate.now();
        LocalDate previousCheckIn =
                lastCheckIns.get(member.getMemberId());

        if (today.equals(previousCheckIn)) {
            throw new IllegalStateException(
                    "This member has already checked in today.");
        }

        lastCheckIns.put(member.getMemberId(), today);
    }

    public Member findMember(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Member ID cannot be blank.");
        }

        String trimmedId = memberId.trim();

        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(trimmedId)) {
                return member;
            }
        }

        return null;
    }

    public List<Member> getMembers() {
        return List.copyOf(members);
    }

        public void registerTrainer(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException(
                    "Trainer cannot be null.");
        }

        if (findTrainer(trainer.getTrainerId()) != null) {
            throw new IllegalArgumentException(
                    "A trainer with this ID already exists.");
        }

        if (trainers.size() >= MAXIMUM_INSTANCES) {
            throw new IllegalStateException(
                    "The gym has reached its trainer limit.");
        }

        trainers.add(trainer);
    }

    public Trainer findTrainer(String trainerId) {
        if (trainerId == null || trainerId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Trainer ID cannot be blank.");
        }

        String trimmedId = trainerId.trim();

        for (Trainer trainer : trainers) {
            if (trainer.getTrainerId().equalsIgnoreCase(trimmedId)) {
                return trainer;
            }
        }

        return null;
    }

    public List<Trainer> getTrainers() {
        return List.copyOf(trainers);
    }

    @Override
    public String toString() {
        return "Gym: " + members.size()
                + " registered members";
    }
}