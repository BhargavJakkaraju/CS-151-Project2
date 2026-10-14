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
    }

    public double calculatePriceForMember(Member member) {
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
        //change to throw an exception later
        return 0;
    }

    @Override
    public void addToClass(Member member) {
        for(Member each : currentClassList) {
            if(each.equals(member)) {
                return;
            }
        }
        if(currentClassList.size() < capacity) {
            currentClassList.add(member);
        } else {
            waitingList.add(member);
        }
    }

    public void removeMemberFromClass(Member member) {
        for(int i = 0; i < currentClassList.size(); i++) {
            if(currentClassList.get(i).equals(member)) {
                currentClassList.remove(i);
            }
        }
        promoteWaitingMember();
    }

    public void promoteWaitingMember() {
        if(currentClassList.size() < capacity && waitingList.size() != 0) {
            currentClassList.add(waitingList.get(0));
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
}
