package gym;

public class SessionFullException extends Exception {

    public SessionFullException() {
        super("Session is full");
    }
}
