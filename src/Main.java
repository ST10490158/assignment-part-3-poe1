import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // =========================
        // PART 1 - REGISTRATION
        // =========================

        System.out.println("=== CREATE ACCOUNT ===");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print(
                "Enter your South African cellphone number: "
        );

        String cellPhoneNumber =
                scanner.nextLine();

        Login user = new Login(
                firstName,
                lastName,
                username,
                password,
                cellPhoneNumber
        );

        System.out.println();

        if (user.checkUserName()) {

            System.out.println(
                    "Username successfully captured."
            );

        } else {

            System.out.println(
                    "Username is not correctly formatted; please ensure that "
                    + "your username contains an underscore and is no more than "
                    + "five characters in length."
            );
        }

        if (user.checkPasswordComplexity()) {

            System.out.println(
                    "Password successfully captured."
            );

        } else {

            System.out.println(
                    "Password is not correctly formatted; please ensure that "
                    + "the password contains at least eight characters, "
                    + "a capital letter, a number, and a special character."
            );
        }

        if (user.checkCellPhoneNumber()) {

            System.out.println(
                    "Cell phone number successfully added."
            );

        } else {

            System.out.println(
                    "Cell phone number incorrectly formatted or does not contain "
                    + "international code."
            );
        }

        boolean validRegistration =
                user.checkUserName()
                && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber();

        if (!validRegistration) {

            System.out.println();

            System.out.println(
                    "Registration failed. Please restart and try again."
            );

            scanner.close();

            return;
        }

        // =========================
        // PART 1 - LOGIN
        // =========================

        System.out.println();
        System.out.println("=== LOGIN ===");

        System.out.print("Enter your username: ");
        String loginUsername =
                scanner.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword =
                scanner.nextLine();

        user.setLoginDetails(
                loginUsername,
                loginPassword
        );

        System.out.println();

        System.out.println(
                user.returnLoginStatus()
        );

        if (!user.loginUser()) {

            scanner.close();

            return;
        }

        // =========================
        // PART 2 AND 3 - QUICKCHAT
        // =========================

        System.out.println();
        System.out.println("Welcome to QuickChat.");

        // Load any messages stored in JSON
        Message.loadStoredMessagesFromJson();

        System.out.println();

        System.out.print(
                "How many messages would you like to enter? "
        );

        int maximumMessages =
                Integer.parseInt(
                        scanner.nextLine()
                );

        int messagesEntered = 0;

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println(
                    "=== QUICKCHAT MENU ==="
            );

            System.out.println(
                    "1. Send Messages"
            );

            System.out.println(
                    "2. Show recently sent messages"
            );

            System.out.println(
                    "3. Quit"
            );

            System.out.println(
                    "4. Stored Messages"
            );

            System.out.print(
                    "Select an option: "
            );

            String menuChoice =
                    scanner.nextLine();

            switch (menuChoice) {

                case "1":

                    if (messagesEntered
                            >= maximumMessages) {

                        System.out.println(
                                "You have reached the number of messages "
                                + "you selected."
                        );

                        break;
                    }

                    System.out.println();
                    System.out.println(
                            "=== CREATE MESSAGE ==="
                    );

                    System.out.print(
                            "Enter recipient cellphone number: "
                    );

                    String recipient =
                            scanner.nextLine();

                    System.out.print(
                            "Enter message: "
                    );

                    String messageText =
                            scanner.nextLine();

                    String sender =
                            firstName + " " + lastName;

                    Message newMessage =
                            new Message(
                                    sender,
                                    recipient,
                                    messageText
                            );

                    System.out.println();

                    System.out.println(
                            newMessage.checkRecipientCell()
                    );

                    System.out.println(
                            newMessage.checkMessageLength()
                    );

                    if (!newMessage
                            .checkRecipientCell()
                            .equals(
                                    "Cell phone number successfully captured."
                            )) {

                        System.out.println(
                                "Message cannot be processed."
                        );

                        break;
                    }

                    if (!newMessage
                            .checkMessageLength()
                            .equals(
                                    "Message ready to send."
                            )) {

                        System.out.println(
                                "Message cannot be processed."
                        );

                        break;
                    }

                    System.out.println();

                    System.out.println(
                            "What would you like to do?"
                    );

                    System.out.println(
                            "1. Send Message"
                    );

                    System.out.println(
                            "2. Disregard Message"
                    );

                    System.out.println(
                            "3. Store Message to send later"
                    );

                    System.out.print(
                            "Select an option: "
                    );

                    int action =
                            Integer.parseInt(
                                    scanner.nextLine()
                            );

                    newMessage.setMessageAction(
                            action
                    );

                    String result =
                            newMessage.SentMessage();

                    System.out.println(result);

                    if (action == 2) {

                        System.out.print(
                                "Enter 0 to delete the message: "
                        );

                        String deleteChoice =
                                scanner.nextLine();

                        if (deleteChoice.equals("0")) {

                            System.out.println(
                                    "Message deleted."
                            );
                        }
                    }

                    messagesEntered++;

                    System.out.println();
                    System.out.println(
                            "=== MESSAGE DETAILS ==="
                    );

                    System.out.println(
                            "Message ID: "
                            + newMessage.getMessageID()
                    );

                    System.out.println(
                            "Message Hash: "
                            + newMessage.getMessageHash()
                    );

                    System.out.println(
                            "Recipient: "
                            + newMessage.getRecipient()
                    );

                    System.out.println(
                            "Message: "
                            + newMessage.getMessage()
                    );

                    break;

                case "2":

                    System.out.println();

                    System.out.println(
                            Message.displaySentMessages()
                    );

                    break;

                case "3":

                    running = false;

                    break;

                case "4":

                    showStoredMessageMenu(
                            scanner
                    );

                    break;

                default:

                    System.out.println(
                            "Invalid option. Please try again."
                    );
            }
        }

        System.out.println();

        System.out.println(
                "Total messages sent: "
                + Message.getTotalMessagesSent()
        );

        System.out.println(
                "Thank you for using QuickChat."
        );

        scanner.close();
    }

    // =========================
    // PART 3 
    // =========================

    public static void showStoredMessageMenu(
            Scanner scanner) {

        boolean storedMenu = true;

        while (storedMenu) {

            System.out.println();

            System.out.println(
                    "=== STORED MESSAGES ==="
            );

            System.out.println(
                    "1. Display sender and recipient of stored messages"
            );

            System.out.println(
                    "2. Display longest stored message"
            );

            System.out.println(
                    "3. Search for message ID"
            );

            System.out.println(
                    "4. Search messages for recipient"
            );

            System.out.println(
                    "5. Delete message using message hash"
            );

            System.out.println(
                    "6. Display stored message report"
            );

            System.out.println(
                    "7. Back to main menu"
            );

            System.out.print(
                    "Select an option: "
            );

            String choice =
                    scanner.nextLine();

            switch (choice) {

                case "1":

                    System.out.println();

                    System.out.println(
                            Message
                                    .displayStoredSendersAndRecipients()
                    );

                    break;

                case "2":

                    System.out.println();

                    System.out.println(
                            Message
                                    .getLongestStoredMessage()
                    );

                    break;

                case "3":

                    System.out.print(
                            "Enter message ID: "
                    );

                    String messageID =
                            scanner.nextLine();

                    System.out.println();

                    System.out.println(
                            Message.searchMessageByID(
                                    messageID
                            )
                    );

                    break;

                case "4":

                    System.out.print(
                            "Enter recipient cellphone number: "
                    );

                    String recipient =
                            scanner.nextLine();

                    System.out.println();

                    System.out.println(
                            Message.searchMessagesByRecipient(
                                    recipient
                            )
                    );

                    break;

                case "5":

                    System.out.print(
                            "Enter message hash: "
                    );

                    String messageHash =
                            scanner.nextLine();

                    System.out.println();

                    System.out.println(
                            Message.deleteMessageByHash(
                                    messageHash
                            )
                    );

                    break;

                case "6":

                    System.out.println();

                    System.out.println(
                            Message.displayStoredReport()
                    );

                    break;

                case "7":

                    storedMenu = false;

                    break;

                default:

                    System.out.println(
                            "Invalid option. Please try again."
                    );
            }
        }
    }
}