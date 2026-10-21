package gym;

import java.util.Scanner;

public class ConsoleUI {
    private final Gym gym;
    private final Scanner scanner;

    public ConsoleUI(Gym gym) {
        if (gym == null) {
            throw new IllegalArgumentException("Gym cannot be null.");
        }

        this.gym = gym;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Type EXIT at any prompt to quit.");

        try {
            while (true) {
                System.out.println("\n--- Gym Membership System ---");
                System.out.println("1. Register member");
                System.out.println("2. View members");
                System.out.println("0. Exit");

                String choice = readText("Choose an option: ");

                try {
                    switch (choice) {
                        case "1":
                            registerMember();
                            break;
                        case "2":
                            viewMembers();
                            break;
                        case "0":
                            return;
                        default:
                            System.out.println(
                                    "Invalid option. Please try again.");
                    }
                } catch (IllegalArgumentException
                        | IllegalStateException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }
            }
        } catch (ExitRequestedException exception) {
            // Return to the caller when EXIT is entered.
        } finally {
            System.out.println("Goodbye!");
            scanner.close();
        }
    }

    private void registerMember() {
        String id = readText("Member ID: ");
        String name = readText("Full name: ");
        String email = readText("Email: ");

        Member member = new Member(id, name, email);
        gym.registerMember(member);

        System.out.println("Member registered successfully.");
    }

    private void viewMembers() {
        if (gym.getMembers().isEmpty()) {
            System.out.println("No members registered.");
            return;
        }

        for (Member member : gym.getMembers()) {
            System.out.println(member);
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (!scanner.hasNextLine()) {
                throw new ExitRequestedException();
            }

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("EXIT")) {
                throw new ExitRequestedException();
            }

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be blank. Please try again.");
        }
    }

    private static class ExitRequestedException extends RuntimeException {
    }
}