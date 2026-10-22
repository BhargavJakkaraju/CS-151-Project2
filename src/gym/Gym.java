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
    private final List<FitnessSession> sessions;
    private final Map<String, LocalDate> lastCheckIns;
    private final List<Booking> bookings;

    public Gym() {
        members = new ArrayList<>();
        trainers = new ArrayList<>();
        sessions = new ArrayList<>();
        lastCheckIns = new HashMap<>();
        bookings = new ArrayList<>();
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
            throw new InvalidOperationException(
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
            throw new InvalidOperationException(
                    "An active, unfrozen membership is required.");
        }

        LocalDate today = LocalDate.now();
        LocalDate previousCheckIn =
                lastCheckIns.get(member.getMemberId());

        if (today.equals(previousCheckIn)) {
            throw new InvalidOperationException(
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
            throw new InvalidOperationException(
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

        public void registerSession(FitnessSession session) {
        if (session == null) {
            throw new IllegalArgumentException(
                    "Session cannot be null.");
        }

        if (findSession(session.getSessionId()) != null) {
            throw new IllegalArgumentException(
                    "A session with this ID already exists.");
        }

        int sameTypeCount = 0;

        for (FitnessSession existing : sessions) {
            if (existing.getClass() == session.getClass()) {
                sameTypeCount++;
            }

            if (existing.getTrainerId().equalsIgnoreCase(
                    session.getTrainerId())) {
                boolean overlaps =
                        session.getStartTime().isBefore(
                                existing.getStartTime().plusMinutes(
                                        existing.getDurationMinutes()))
                        && existing.getStartTime().isBefore(
                                session.getStartTime().plusMinutes(
                                        session.getDurationMinutes()));

                if (overlaps) {
                    throw new InvalidOperationException(
                            "The trainer has an overlapping session.");
                }
            }
        }

        if (sameTypeCount >= MAXIMUM_INSTANCES) {
            throw new InvalidOperationException(
                    "The limit for this session type has been reached.");
        }

        Trainer trainer = findTrainer(session.getTrainerId());

        if (trainer == null) {
            throw new IllegalArgumentException(
                    "Register the trainer before creating a session.");
        }

                for (java.time.LocalDateTime blocked : trainer.getBlockedTimes()) {
            boolean fallsInsideSession =
                    !blocked.isBefore(session.getStartTime())
                    && blocked.isBefore(
                            session.getStartTime().plusMinutes(
                                    session.getDurationMinutes()));

            if (fallsInsideSession) {
                throw new InvalidOperationException(
                        "The session overlaps a trainer's blocked time.");
            }
        }

        trainer.assignSession(session);
        sessions.add(session);
    }

    public FitnessSession findSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Session ID cannot be blank.");
        }

        for (FitnessSession session : sessions) {
            if (session.getSessionId().equalsIgnoreCase(
                    sessionId.trim())) {
                return session;
            }
        }

        return null;
    }

    public void removeSession(String sessionId) {
        FitnessSession session = findSession(sessionId);

        if (session == null) {
            throw new IllegalArgumentException(
                    "Session not found.");
        }

        for (Booking booking : bookings) {
            if (booking.getSession() == session
                    && booking.getStatus() == Booking.STATUS.BOOKED) {
                throw new InvalidOperationException(
                        "Cancel active bookings before removing this session.");
            }
        }

        Trainer trainer = findTrainer(session.getTrainerId());

        if (trainer == null) {
            throw new InvalidOperationException(
                    "The session's trainer could not be found.");
        }

        trainer.cancelAssignment(session.getSessionId());
        sessions.remove(session);
    }

    public void reassignSession(String sessionId, String newTrainerId) {
        FitnessSession session = findSession(sessionId);
        Trainer newTrainer = findTrainer(newTrainerId);

        if (session == null || newTrainer == null) {
            throw new IllegalArgumentException(
                    "Session or new trainer not found.");
        }

        Trainer oldTrainer = findTrainer(session.getTrainerId());

        if (oldTrainer == null) {
            throw new InvalidOperationException(
                    "Current trainer not found.");
        }

        if (oldTrainer == newTrainer) {
            throw new IllegalArgumentException(
                    "Choose a different trainer.");
        }

        if (!session.getStartTime().isAfter(
                java.time.LocalDateTime.now())) {
            throw new InvalidOperationException(
                    "Cannot reassign a session that has started.");
        }

        java.time.LocalDateTime start = session.getStartTime();
        java.time.LocalDateTime end =
                start.plusMinutes(session.getDurationMinutes());

        for (FitnessSession other : newTrainer.getAssignedSessions()) {
            boolean overlaps =
                    start.isBefore(other.getStartTime().plusMinutes(
                            other.getDurationMinutes()))
                    && other.getStartTime().isBefore(end);

            if (overlaps) {
                throw new InvalidOperationException(
                        "The new trainer has an overlapping session.");
            }
        }

        for (java.time.LocalDateTime blocked : newTrainer.getBlockedTimes()) {
            if (!blocked.isBefore(start) && blocked.isBefore(end)) {
                throw new InvalidOperationException(
                        "The new trainer is unavailable during this session.");
            }
        }

        String previousTrainerId = session.getTrainerId();
        oldTrainer.cancelAssignment(session.getSessionId());
        session.setTrainerId(newTrainer.getTrainerId());

        try {
            newTrainer.assignSession(session);
        } catch (RuntimeException exception) {
            session.setTrainerId(previousTrainerId);

            try {
                oldTrainer.assignSession(session);
            } catch (RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
            }

            throw exception;
        }
    }
        
    public List<FitnessSession> getSessions() {
        return List.copyOf(sessions);
    }

        public Booking createBooking(String memberId, String sessionId)
            throws SessionFullException, MemberAlreadyInClassException {
        Member member = findMember(memberId);
        FitnessSession session = findSession(sessionId);

        if (member == null || session == null) {
            throw new IllegalArgumentException(
                    "Member or session not found.");
        }

        if (bookings.size() >= MAXIMUM_INSTANCES) {
            throw new InvalidOperationException(
                    "The booking limit has been reached.");
        }

        validateBooking(member, session, null);

        Booking booking = new Booking(member, session);
        bookings.add(booking);
        return booking;
    }

    private void validateBooking(Member member, FitnessSession session,
            Booking ignoredBooking) {
        if (!member.hasActiveMembership()) {
            throw new InvalidOperationException(
                    "An active, unfrozen membership is required.");
        }

        if (!session.getStartTime().isAfter(
                java.time.LocalDateTime.now())) {
            throw new InvalidOperationException(
                    "Cannot book a session that has already started.");
        }

        if (session.getStartTime().toLocalDate()
                .isAfter(member.getExpirationDate())) {
            throw new InvalidOperationException(
                    "Membership expires before this session.");
        }

        for (Booking existing : bookings) {
            if (existing == ignoredBooking
                    || existing.getStatus() != Booking.STATUS.BOOKED
                    || !existing.getMember().equals(member)) {
                continue;
            }

            if (existing.getSession() == session) {
                throw new IllegalArgumentException(
                        "This member already booked this session.");
            }

            FitnessSession other =
                    (FitnessSession) existing.getSession();

            boolean overlaps =
                    session.getStartTime().isBefore(
                            other.getStartTime().plusMinutes(
                                    other.getDurationMinutes()))
                    && other.getStartTime().isBefore(
                            session.getStartTime().plusMinutes(
                                    session.getDurationMinutes()));

            if (overlaps) {
                throw new InvalidOperationException(
                        "This member has an overlapping booking.");
            }
        }
    }

    public void rescheduleBooking(Booking booking, String newSessionId)
            throws SessionFullException, MemberAlreadyInClassException {
        if (booking == null || !bookings.contains(booking)) {
            throw new IllegalArgumentException(
                    "Booking not found.");
        }

        if (booking.getStatus() != Booking.STATUS.BOOKED) {
            throw new InvalidOperationException(
                    "Only an active booking can be rescheduled.");
        }

        FitnessSession newSession = findSession(newSessionId);

        if (newSession == null) {
            throw new IllegalArgumentException(
                    "New session not found.");
        }

        validateBooking(booking.getMember(), newSession, booking);
        booking.rescheduleBooking(newSession);
    }

    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    @Override
    public String toString() {
        return "Gym: " + members.size()
                + " registered members";
    }
}