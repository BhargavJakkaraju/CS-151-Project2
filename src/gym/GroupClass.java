package gym;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class GroupClass extends FitnessSession {
    private static final double STANDARD_PRICE = 40.0;
    private static final double PREMIUM_PRICE = 25.0;

    private int capacity;
    private ArrayList<Member> waitingList;
    private ArrayList<Member> currentClassList;

    public GroupClass(int capacity, String sessionId, String trainerId,
            LocalDateTime startTime, int durationMinutes) {
        super(sessionId, trainerId, startTime, durationMinutes);
        if (capacity < 1 || capacity > 100) {
        throw new IllegalArgumentException(
            "Capacity must be between 1 and 100.");
    }
    this.capacity = capacity;
        waitingList = new ArrayList<Member>();
        currentClassList = new ArrayList<Member>();

        if (capacity < 0) {
            throw new IllegalArgumentException(
                    "Capacity cannot be negative.");
        }
    }

    @Override
    public double calculatePrice(Member member) {
        if (member == null) {
            throw new IllegalArgumentException(
                    "Member cannot be null.");
        }
        for(Member each : currentClassList) {
            if(each.equals(member)){
                if (member != null
                        && member.getMembershipType().equals("PREMIUM")
                        && member.hasActiveMembership()) {
                    return PREMIUM_PRICE;
                }
                return STANDARD_PRICE;
            }
        }
        for(Member each : waitingList) {
            if(each.equals(member)){
                if (member != null
                        && member.getMembershipType().equals("PREMIUM")
                        && member.hasActiveMembership()) {
                    return PREMIUM_PRICE;
                }
                return STANDARD_PRICE;
            }
        }
        throw new IllegalStateException(
                "Member is not in class.");
    }

    @Override
    public void addToClass(Member member) throws MemberAlreadyInClassException{
        if (member == null) {
            throw new IllegalArgumentException(
                    "Member cannot be null.");
        }
        for(Member each : currentClassList) {
            if(each.equals(member)) {
                throw new MemberAlreadyInClassException();
            }
        }
        for(Member each : waitingList) {
            if(each.equals(member)) {
                throw new MemberAlreadyInClassException();
            }
        }
        if(currentClassList.size() < capacity) {
            currentClassList.add(member);
        } else {
            waitingList.add(member);
        }
    }

    @Override
    public void removeMemberFromClass(Member member) {
        if (member == null) {
            throw new IllegalArgumentException(
                    "Member cannot be null.");
        }

        if (currentClassList.remove(member)) {
            promoteWaitingMember();
            return;
        }

        if (waitingList.remove(member)) {
            return;
        }

        throw new IllegalStateException(
                "That member is not enrolled or on the waiting list.");
    }

    public void promoteWaitingMember() {
        int index = 0;

        while (currentClassList.size() < capacity
                && index < waitingList.size()) {
            Member next = waitingList.get(index);

            if (next.hasActiveMembership()) {
                waitingList.remove(index);
                currentClassList.add(next);
            } else {
                index++;
            }
        }
    }

    public void increaseCapacity(int difference) {
        if (difference <= 0 || difference > 100 - capacity) {
            throw new IllegalArgumentException(
                    "Increase must be positive and total capacity cannot exceed 100.");
        }

        capacity += difference;
        promoteWaitingMember();
    }

    public void decreaseCapacity(int difference) {
        if (difference <= 0 || difference >= capacity) {
            throw new IllegalArgumentException(
                    "Decrease must be positive and capacity must remain at least 1.");
        }

        if (capacity - difference < currentClassList.size()) {
            throw new IllegalStateException(
                    "Capacity cannot be less than current enrollment.");
        }

        capacity -= difference;
    }

    public boolean isEnrolled(Member member) {
        return currentClassList.contains(member);
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return super.toString()
                + ", People in class: " + currentClassList.size()
                + ", People on waiting list: " + waitingList.size();
    }
}
