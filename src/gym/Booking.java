package gym;

public class Booking {
    private final Member member;
    private Bookable session;
    public enum STATUS {
        BOOKED, ATTENDED, MISSED, CANCELLED
    }
    private STATUS status;
    
    public Booking(Member member, Bookable session) throws SessionFullException, MemberAlreadyInClassException {
        this.member = member;
        this.session = session;
        this.status = STATUS.BOOKED;
    }

    public void cancelBooking() {
        if (status != STATUS.BOOKED) {
            throw new IllegalStateException(
                    "Only an active booking can be canceled.");
        }

        session.removeMemberFromClass(member);
        status = STATUS.CANCELLED;
    }

    public void rescheduleBooking(Bookable newSession)
            throws SessionFullException, MemberAlreadyInClassException {
        if (newSession == null) {
            throw new IllegalArgumentException(
                    "New session cannot be null.");
        }

        if (newSession == session) {
            throw new IllegalArgumentException(
                    "Choose a different session.");
        }

        if (status != STATUS.BOOKED) {
            throw new IllegalStateException(
                    "Only an active booking can be rescheduled.");
        }

        newSession.addToClass(member);

        try {
            session.removeMemberFromClass(member);
        } catch (RuntimeException exception) {
            try {
                newSession.removeMemberFromClass(member);
            } catch (RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
            }

            throw exception;
        }

        session = newSession;
    }

    public void markAttendence(Boolean attended) {
        if (attended == null) {
            throw new IllegalArgumentException(
                    "Attendance cannot be null.");
        }

        if (status != STATUS.BOOKED) {
            throw new IllegalStateException(
                    "Attendance can only be recorded once "
                            + "for an active booking.");
        }

        FitnessSession fitnessSession = (FitnessSession) session;

        if (java.time.LocalDateTime.now().isBefore(
                fitnessSession.getStartTime().plusMinutes(
                        fitnessSession.getDurationMinutes()))) {
            throw new IllegalStateException(
                    "Record attendance after the session ends.");
        }

        if (session instanceof GroupClass
                && !((GroupClass) session).isEnrolled(member)) {
            throw new IllegalStateException(
                    "Waiting-list members cannot have attendance recorded.");
        }

        status = attended ? STATUS.ATTENDED : STATUS.MISSED;
    }

    public boolean wasAttended() {
        if(status == STATUS.ATTENDED) {
            return true;
        }
        return false;
    }

        public Member getMember() {
        return member;
    }

    public Bookable getSession() {
        return session;
    }

    public STATUS getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Session: " + session.toString()
                + " Member: " + member.toString();
    }
}
