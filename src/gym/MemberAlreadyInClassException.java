package gym;

public class MemberAlreadyInClassException extends Exception {
    public MemberAlreadyInClassException() {
        super("Member is already in the class");
    }
}
