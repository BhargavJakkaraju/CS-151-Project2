package gym;

import java.util.ArrayList;

public class Booking {
    public Member member;
    public FitnessSession session;
    public enum STATUS {
        BOOKED, ATTENDED, MISSED
    }
    public Status status;
    
    public Booking(Member member, FitnessSession session) {
        //add exception handling later
        this.member = member;
        this.session = session;
        session.addMemberToClass(member);
        status = Status.BOOKED;
    }

    public void cancelBooking() {
        //add exception handling later;
        session.removeMemberFromClass(member);
    }

    public void rescheduleBooking(FitnessSession newSession) {
        session.removeMemberFromClass(member);
        newSession.addMemberToClass(member);
    }

    public void markAttendence(Boolean attended) {
        if(attended) {
            status = Status.ATTENDED;
        } else {
            status = Status.MISSED;
        }
    }
}
