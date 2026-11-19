import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String message;
    private String messageHash;
    private int messageAction;
    private String flag;

    private static int messageCounter = 0;
    private static int totalMessagesSent = 0;

    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();

    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static final List<Message> allMessages = new ArrayList<>();

    private static final Path JSON_FILE = Path.of("messages.json");

    public Message(String recipient, String message) {

        this("Developer", recipient, message);
    }

    public Message(String sender, String recipient, String message) {

        this.messageID = generateMessageID();
        this.messageNumber = messageCounter;
        messageCounter++;

        this.sender = sender;
        this.recipient = recipient;
        this.message = message;

        this.messageHash = createMessageHash();
        this.flag = "";
    }

    private Message(
            String messageID,
            int messageNumber,
            String sender,
            String recipient,
            String message,
            String messageHash,
            String flag) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = messageHash;
        this.flag = flag;
    }

    private String generateMessageID() {

        long number = ThreadLocalRandom.current()
                .nextLong(1000000000L, 10000000000L);

        return String.valueOf(number);
    }

    public boolean checkMessageID() {

        return messageID.length() <= 10;
    }

    public String checkRecipientCell() {

        if (recipient.matches("^\\+27\\d{9}$")) {

            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {

        if (message.length() <= 250) {

            return "Message ready to send.";
        }

        int charactersOver = message.length() - 250;

        return "Message exceeds 250 characters by "
                + charactersOver
                + ", please reduce the size.";
    }

    public String createMessageHash() {

        String[] words = message.trim().split("\\s+");

        String firstWord = words[0]
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();

        String lastWord = words[words.length - 1]
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();

        return messageID.substring(0, 2)
                + ":"
                + messageNumber
                + ":"
                + firstWord
                + lastWord;
    }

    public void setMessageAction(int messageAction) {

        this.messageAction = messageAction;
    }

    public String SentMessage() {

        switch (messageAction) {

            case 1:

                flag = "Sent";

                sentMessages.add(this);
                allMessages.add(this);

                messageHashes.add(messageHash);
                messageIDs.add(messageID);

                totalMessagesSent++;

                return "Message successfully sent.";

            case 2:

                flag = "Disregard";

                disregardedMessages.add(this);
                allMessages.add(this);

                messageHashes.add(messageHash);
                messageIDs.add(messageID);

                return "Press 0 to delete the message.";

            case 3:

                flag = "Stored";

                storedMessages.add(this);
                allMessages.add(this);

                messageHashes.add(messageHash);
                messageIDs.add(messageID);

                storeMessage();

                return "Message successfully stored.";

            default:

                return "Invalid option.";
        }
    }

    public String printMessages() {

        return displaySentMessages();
    }

    public static String displaySentMessages() {

        if (sentMessages.isEmpty()) {

            return "No messages have been sent.";
        }

        StringBuilder output = new StringBuilder();

        for (Message currentMessage : sentMessages) {

            output.append("Message ID: ")
                    .append(currentMessage.messageID)
                    .append("\n");

            output.append("Message Hash: ")
                    .append(currentMessage.messageHash)
                    .append("\n");

            output.append("Recipient: ")
                    .append(currentMessage.recipient)
                    .append("\n");

            output.append("Message: ")
                    .append(currentMessage.message)
                    .append("\n\n");
        }

        return output.toString().trim();
    }

    // Returns only the sent message text
    public static String getSentMessagesText() {

        StringBuilder output = new StringBuilder();

        for (Message currentMessage : sentMessages) {

            if (output.length() > 0) {

                output.append("\n");
            }

            output.append(currentMessage.message);
        }

        return output.toString();
    }

    public int returnTotalMessages() {

        return totalMessagesSent;
    }

    public static int getTotalMessagesSent() {

        return totalMessagesSent;
    }

    public void storeMessage() {

        writeStoredMessagesToJson();
    }

    private static void writeStoredMessagesToJson() {

        try (FileWriter writer = new FileWriter(JSON_FILE.toFile())) {

            writer.write("[\n");

            for (int i = 0; i < storedMessages.size(); i++) {

                Message stored = storedMessages.get(i);

                writer.write("  {\n");

                writer.write(
                        "    \"messageID\": \""
                                + escapeJSON(stored.messageID)
                                + "\",\n"
                );

                writer.write(
                        "    \"messageNumber\": "
                                + stored.messageNumber
                                + ",\n"
                );

                writer.write(
                        "    \"sender\": \""
                                + escapeJSON(stored.sender)
                                + "\",\n"
                );

                writer.write(
                        "    \"recipient\": \""
                                + escapeJSON(stored.recipient)
                                + "\",\n"
                );

                writer.write(
                        "    \"message\": \""
                                + escapeJSON(stored.message)
                                + "\",\n"
                );

                writer.write(
                        "    \"messageHash\": \""
                                + escapeJSON(stored.messageHash)
                                + "\",\n"
                );

                writer.write(
                        "    \"flag\": \""
                                + escapeJSON(stored.flag)
                                + "\"\n"
                );

                writer.write("  }");

                if (i < storedMessages.size() - 1) {

                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]");

        } catch (IOException exception) {

            System.out.println(
                    "An error occurred while storing messages."
            );
        }
    }

    public static void loadStoredMessagesFromJson() {

        if (!Files.exists(JSON_FILE)) {

            return;
        }

        try {

            String json = Files.readString(JSON_FILE);

            Pattern objectPattern = Pattern.compile(
                    "\\{(.*?)\\}",
                    Pattern.DOTALL
            );

            Matcher objectMatcher = objectPattern.matcher(json);

            // Remove previously loaded stored messages
            allMessages.removeIf(
                    current -> "Stored".equals(current.flag)
            );

            storedMessages.clear();

            while (objectMatcher.find()) {

                String object = objectMatcher.group(1);

                String id = extractJsonString(
                        object,
                        "messageID"
                );

                int number = extractJsonInt(
                        object,
                        "messageNumber"
                );

                String sender = extractJsonString(
                        object,
                        "sender"
                );

                String recipient = extractJsonString(
                        object,
                        "recipient"
                );

                String messageText = extractJsonString(
                        object,
                        "message"
                );

                String hash = extractJsonString(
                        object,
                        "messageHash"
                );

                String flag = extractJsonString(
                        object,
                        "flag"
                );

                Message loadedMessage = new Message(
                        id,
                        number,
                        sender,
                        recipient,
                        messageText,
                        hash,
                        flag
                );

                storedMessages.add(loadedMessage);
                allMessages.add(loadedMessage);

                if (!messageIDs.contains(id)) {

                    messageIDs.add(id);
                }

                if (!messageHashes.contains(hash)) {

                    messageHashes.add(hash);
                }

                if (number >= messageCounter) {

                    messageCounter = number + 1;
                }
            }

        } catch (IOException exception) {

            System.out.println(
                    "Unable to load stored messages."
            );
        }
    }

    private static String extractJsonString(
            String object,
            String key) {

        Pattern pattern = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*\"(.*?)\"",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(object);

        if (matcher.find()) {

            return unescapeJSON(matcher.group(1));
        }

        return "";
    }

    private static int extractJsonInt(
            String object,
            String key) {

        Pattern pattern = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*(\\d+)"
        );

        Matcher matcher = pattern.matcher(object);

        if (matcher.find()) {

            return Integer.parseInt(matcher.group(1));
        }

        return 0;
    }

    private static String escapeJSON(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private static String unescapeJSON(String text) {

        return text
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    // Part 3:// 
    public static String displayStoredSendersAndRecipients() {

        if (storedMessages.isEmpty()) {

            return "No stored messages found.";
        }

        StringBuilder output = new StringBuilder();

        for (Message current : storedMessages) {

            if (output.length() > 0) {

                output.append("\n");
            }

            output.append("Sender: ")
                    .append(current.sender)
                    .append(", Recipient: ")
                    .append(current.recipient);
        }

        return output.toString();
    }

    public static String getLongestStoredMessage() {

        if (storedMessages.isEmpty()) {

            return "No stored messages found.";
        }

        Message longest = storedMessages.get(0);

        for (Message current : storedMessages) {

            if (current.message.length()
                    > longest.message.length()) {

                longest = current;
            }
        }

        return longest.message;
    }

    public static String getLongestMessage() {

        if (allMessages.isEmpty()) {

            return "No messages found.";
        }

        Message longest = allMessages.get(0);

        for (Message current : allMessages) {

            if (current.message.length()
                    > longest.message.length()) {

                longest = current;
            }
        }

        return longest.message;
    }

    public static String searchMessageByID(
            String searchID) {

        for (Message current : allMessages) {

            if (current.messageID.equals(searchID)) {

                return current.message;
            }
        }

        return "Message ID not found.";
    }

    public static String searchMessagesByRecipient(
            String searchRecipient) {

        StringBuilder output = new StringBuilder();

        for (Message current : allMessages) {

            if (current.recipient.equals(searchRecipient)) {

                if (output.length() > 0) {

                    output.append("\n");
                }

                output.append(current.message);
            }
        }

        if (output.length() == 0) {

            return "No messages found for recipient.";
        }

        return output.toString();
    }

    public static String deleteMessageByHash(
            String searchHash) {

        Message messageToDelete = null;

        for (Message current : allMessages) {

            if (current.messageHash.equals(searchHash)) {

                messageToDelete = current;
                break;
            }
        }

        if (messageToDelete == null) {

            return "Message hash not found.";
        }

        String deletedMessage =
                messageToDelete.message;

        sentMessages.remove(messageToDelete);
        disregardedMessages.remove(messageToDelete);
        storedMessages.remove(messageToDelete);
        allMessages.remove(messageToDelete);

        messageHashes.remove(
                messageToDelete.messageHash
        );

        messageIDs.remove(
                messageToDelete.messageID
        );

        writeStoredMessagesToJson();

        return "Message \""
                + deletedMessage
                + "\" successfully deleted.";
    }

    public static String displayReport() {

        if (sentMessages.isEmpty()) {

            return "No sent messages found.";
        }

        StringBuilder report = new StringBuilder();

        for (Message current : sentMessages) {

            if (report.length() > 0) {

                report.append("\n\n");
            }

            report.append("Message Hash: ")
                    .append(current.messageHash)
                    .append("\n");

            report.append("Recipient: ")
                    .append(current.recipient)
                    .append("\n");

            report.append("Message: ")
                    .append(current.message);
        }

        return report.toString();
    }

    public static String displayStoredReport() {

        if (storedMessages.isEmpty()) {

            return "No stored messages found.";
        }

        StringBuilder report = new StringBuilder();

        for (Message current : storedMessages) {

            if (report.length() > 0) {

                report.append("\n\n");
            }

            report.append("Message ID: ")
                    .append(current.messageID)
                    .append("\n");

            report.append("Message Hash: ")
                    .append(current.messageHash)
                    .append("\n");

            report.append("Sender: ")
                    .append(current.sender)
                    .append("\n");

            report.append("Recipient: ")
                    .append(current.recipient)
                    .append("\n");

            report.append("Message: ")
                    .append(current.message);
        }

        return report.toString();
    }

    public String getMessageID() {

        return messageID;
    }

    public int getMessageNumber() {

        return messageNumber;
    }

    public String getSender() {

        return sender;
    }

    public String getRecipient() {

        return recipient;
    }

    public String getMessage() {

        return message;
    }

    public String getMessageHash() {

        return messageHash;
    }

    public String getFlag() {

        return flag;
    }

    public static int getSentMessageCount() {

        return sentMessages.size();
    }

    public static int getStoredMessageCount() {

        return storedMessages.size();
    }

    public static int getDisregardedMessageCount() {

        return disregardedMessages.size();
    }

    public static void resetMessageData() {

        messageCounter = 0;
        totalMessagesSent = 0;

        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();

        messageHashes.clear();
        messageIDs.clear();

        allMessages.clear();

        try {

            Files.deleteIfExists(JSON_FILE);

        } catch (IOException exception) {

            
        }
    }
}