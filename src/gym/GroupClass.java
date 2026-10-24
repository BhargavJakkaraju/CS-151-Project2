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
        while (currentClassList.size() < capacity
                && !waitingList.isEmpty()) {
            Member next = waitingList.remove(0);

            if (next.hasActiveMembership()
                    && !currentClassList.contains(next)) {
                currentClassList.add(next);
            }
        }
    }

    public void increaseCapacity(int difference) {
        capacity += difference;
    }

    public void decreaseCapacity(int difference) {
        if(currentClassList.size() > (capacity-difference)) {
            capacity = currentClassList.size();
        } else {
            capacity -= difference;
        }
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
