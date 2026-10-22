package gym;

public interface Bookable {
    public abstract void addToClass(Member member) throws SessionFullException, MemberAlreadyInClassException;

    public abstract void removeMemberFromClass(Member member);
}
