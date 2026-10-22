package gym;

public class Main {
    public static void main(String[] args) {
        Gym gym = new Gym();
        ConsoleUI ui = new ConsoleUI(gym);
        ui.run();
    }
}