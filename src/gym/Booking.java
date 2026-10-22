package gym;

public class Booking {
    public Member member;
    public FitnessSession session;
    public enum STATUS {
        BOOKED, ATTENDED, MISSED
    }
    public STATUS status;
    
    public Booking(Member member, FitnessSession session) {
        //add exception handling later
        this.member = member;
        this.session = session;
        session.addToClass(member);
        status = STATUS.BOOKED;
    }

    public void cancelBooking() {
        //add exception handling later;
        session.removeFromClass(member);
    }

    public void rescheduleBooking(FitnessSession newSession) {
        session.removeFromClass(member);
        newSession.addToClass(member);
    }

    public void markAttendence(Boolean attended) {
        if(attended) {
            status = STATUS.ATTENDED;
        } else {
            status = STATUS.MISSED;
        }
    }

    public boolean wasAttended() {
        if(status == STATUS.ATTENDED) {
            return true;
        }
        return false;
    }
}
