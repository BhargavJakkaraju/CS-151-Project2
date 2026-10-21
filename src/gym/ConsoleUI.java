package gym;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

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
                System.out.println("3. Purchase membership");
                System.out.println("4. Renew membership");
                System.out.println("5. Freeze membership");
                System.out.println("6. Resume membership");
                System.out.println("7. Check in");
                System.out.println("8. Remove member");
                System.out.println("9. Register trainer");
                System.out.println("10. View trainers");
                System.out.println("11. Create personal training session");
                System.out.println("12. View sessions");
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
                        case "3":
                            purchaseMembership();
                            break;
                        case "4":
                            renewMembership();
                            break;
                        case "5":
                            freezeMembership();
                            break;
                        case "6":
                            resumeMembership();
                            break;
                        case "7":
                            checkInMember();
                            break;
                        case "8":
                            removeMember();
                            break; 
                        case "9":
                            registerTrainer();
                            break;
                        case "10":
                            viewTrainers();
                            break;
                         case "11":
                            createPersonalTrainingSession();
                            break;
                        case "12":
                            viewSessions();
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

        private void purchaseMembership() {
        Member member = selectMember();
        String type;

        while (true) {
            type = readText("Membership type (BASIC or PREMIUM): ");

            if (type.equalsIgnoreCase("BASIC")
                    || type.equalsIgnoreCase("PREMIUM")) {
                break;
            }

            System.out.println("Please enter BASIC or PREMIUM.");
        }

        int months = readMonths();
        member.purchaseMembership(type, months);

        System.out.println("Membership purchased successfully.");
        System.out.println(member);
    }

    private void renewMembership() {
        Member member = selectMember();
        int months = readMonths();

        member.renewMembership(months);

        System.out.println("Membership renewed successfully.");
        System.out.println(member);
    }

    private void freezeMembership() {
        Member member = selectMember();
        member.freezeMembership();

        System.out.println("Membership frozen. Gym access is paused.");
        System.out.println("The expiration date stays the same.");
    }

    private void resumeMembership() {
        Member member = selectMember();
        member.resumeMembership();

        System.out.println("Membership resumed successfully.");
    }

    private void checkInMember() {
        Member member = selectMember();
        gym.checkInMember(member.getMemberId());

        System.out.println("Check-in successful. Welcome, "
                + member.getName() + "!");
    }

    private void removeMember() {
        Member member = selectMember();
        String confirmation = readText(
                "Remove " + member.getName() + "? Enter YES to confirm: ");

        if (!confirmation.equalsIgnoreCase("YES")) {
            System.out.println("Removal canceled.");
            return;
        }

        gym.removeMember(member.getMemberId());
        System.out.println("Member removed successfully.");
    }

        private void registerTrainer() {
        String id = readText("Trainer ID: ");
        String name = readText("Trainer name: ");
        String specialty = readText("Specialty: ");

        Trainer trainer = new Trainer(id, name, specialty);
        gym.registerTrainer(trainer);

        System.out.println("Trainer registered successfully.");
    }

    private void viewTrainers() {
        if (gym.getTrainers().isEmpty()) {
            System.out.println("No trainers registered.");
            return;
        }

        for (Trainer trainer : gym.getTrainers()) {
            System.out.println(trainer);
        }
    }

        private LocalDateTime readSessionTime() {
        while (true) {
            String input = readText(
                    "Start time (example: 2026-10-24T15:30): ");

            try {
                LocalDateTime startTime = LocalDateTime.parse(input);

                if (startTime.isAfter(LocalDateTime.now())) {
                    return startTime;
                }

                System.out.println("Please enter a future date and time.");
            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Use YYYY-MM-DDTHH:MM, such as 2026-10-24T15:30.");
            }
        }
    }

    private int readIntInRange(String prompt, int minimum, int maximum) {
        while (true) {
            String input = readText(prompt);

            try {
                int value = Integer.parseInt(input);

                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // Ask again when the input is not a whole number.
            }

            System.out.println("Enter a whole number from "
                    + minimum + " to " + maximum + ".");
        }
    }

        private void createPersonalTrainingSession() {
        String trainerId = readText("Trainer ID: ");
        Trainer trainer = gym.findTrainer(trainerId);

        if (trainer == null) {
            throw new IllegalArgumentException(
                    "Trainer not found. Register the trainer first.");
        }

        String sessionId = readText("Session ID: ");
        LocalDateTime startTime = readSessionTime();
        int duration = readIntInRange(
                "Duration in minutes (1-180): ", 1, 180);

        PersonalTrainingSession session = new PersonalTrainingSession(
                sessionId, trainer.getTrainerId(), startTime, duration);

        gym.registerSession(session);
        System.out.println("Personal training session created.");
    }

    private void viewSessions() {
        if (gym.getSessions().isEmpty()) {
            System.out.println("No sessions registered.");
            return;
        }

        for (FitnessSession session : gym.getSessions()) {
            System.out.println(session);
        }
    }

        private Member selectMember() {
        String memberId = readText("Member ID: ");
        Member member = gym.findMember(memberId);

        if (member == null) {
            throw new IllegalArgumentException("Member not found.");
        }

        return member;
    }

    private int readMonths() {
        while (true) {
            String input = readText("Number of months (1-12): ");

            try {
                int months = Integer.parseInt(input);

                if (months >= 1 && months <= 12) {
                    return months;
                }
            } catch (NumberFormatException exception) {
                // Ask again if the input is not a whole number.
            }

            System.out.println(
                    "Please enter a whole number from 1 to 12.");
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