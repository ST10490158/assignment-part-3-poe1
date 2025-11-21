import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Message {

    private static final List<Message> SENT = new ArrayList<>();
    private static int totalSent = 0;

    public static final int MAX_MESSAGES = 100;

    public static String[] sentMessages = new String[MAX_MESSAGES];
    public static String[] sentRecipients = new String[MAX_MESSAGES];
    public static String[] disregardedMessages = new String[MAX_MESSAGES];
    public static String[] storedMessages = new String[MAX_MESSAGES];
    public static String[] allMessageHashes = new String[MAX_MESSAGES];
    public static String[] allMessageIds = new String[MAX_MESSAGES];
    public static String[] allMessages = new String[MAX_MESSAGES];
    public static String[] allRecipients = new String[MAX_MESSAGES];
    public static int[] allActions = new int[MAX_MESSAGES];

    public static int sentIndex = 0;
    public static int disregardedIndex = 0;
    public static int storedIndex = 0;
    public static int allIndex = 0;

    private String messageId;
    private int messageNumber;
    private String recipient;
    private String text;
    private String messageHash;
    private int action;

    public Message(String messageId, int messageNumber, String recipient, String text) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    public Message(int messageNumber, String recipient, String text) {
        this(generateId(), messageNumber, recipient, text);
    }

    public static void resetArrays() {
        sentMessages = new String[MAX_MESSAGES];
        sentRecipients = new String[MAX_MESSAGES];
        disregardedMessages = new String[MAX_MESSAGES];
        storedMessages = new String[MAX_MESSAGES];
        allMessageHashes = new String[MAX_MESSAGES];
        allMessageIds = new String[MAX_MESSAGES];
        allMessages = new String[MAX_MESSAGES];
        allRecipients = new String[MAX_MESSAGES];
        allActions = new int[MAX_MESSAGES];

        sentIndex = 0;
        disregardedIndex = 0;
        storedIndex = 0;
        allIndex = 0;

        SENT.clear();
        totalSent = 0;
    }

    public static String generateId() {
        StringBuilder sb = new StringBuilder(10);
        while (sb.length() < 10) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    public boolean checkMessageID() {
        return messageId != null && messageId.matches("\\d{10}") && messageId.length() <= 10;
    }

    public int checkRecipientCell() {
        return (recipient != null && recipient.matches("^\\+\\d{1,3}\\d{1,10}$")) ? 1 : 0;
    }

    public String messageLengthStatus() {
        if (text == null) {
            return "Message exceeds 250 characters by 250, please reduce size.";
        }
        int len = text.length();
        if (len <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (len - 250) + ", please reduce size.";
    }

    public String createMessageHash() {
        String firstTwo = (messageId != null && messageId.length() >= 2) ? messageId.substring(0, 2) : "00";
        String firstWord = firstWordUpper(text);
        String lastWord = lastWordUpper(text);
        return firstTwo + ":" + messageNumber + ":" + (firstWord + lastWord).toUpperCase();
    }

    private static String firstWordUpper(String s) {
        if (s == null || s.isBlank()) return "";
        String[] parts = s.trim().split("\\s+");
        return parts[0].replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    private static String lastWordUpper(String s) {
        if (s == null || s.isBlank()) return "";
        String[] parts = s.trim().split("\\s+");
        return parts[parts.length - 1].replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    public void setAction(int action) {
        this.action = action;
    }

    private void addToArrays() {
        if (allIndex < MAX_MESSAGES) {
            allMessageIds[allIndex] = this.messageId;
            allMessageHashes[allIndex] = this.messageHash;
            allMessages[allIndex] = this.text;
            allRecipients[allIndex] = this.recipient;
            allActions[allIndex] = this.action;
            allIndex++;
        }

        if (action == 1 && sentIndex < MAX_MESSAGES) {
            sentMessages[sentIndex] = this.text;
            sentRecipients[sentIndex] = this.recipient;
            sentIndex++;
        } else if (action == 2 && disregardedIndex < MAX_MESSAGES) {
            disregardedMessages[disregardedIndex] = this.text;
            disregardedIndex++;
        } else if (action == 3 && storedIndex < MAX_MESSAGES) {
            storedMessages[storedIndex] = this.text;
            storedIndex++;
        }
    }

    public String SentMessage() {
        if (action == 1) {
            SENT.add(this);
            totalSent++;
            addToArrays();
            return "Message successfully sent.";
        } else if (action == 2) {
            addToArrays();
            return "Press 0 to delete message.";
        } else if (action == 3) {
            addToArrays();
            return "Message successfully stored.";
        }
        return "No action selected.";
    }

    public static String printMessages() {
        if (SENT.isEmpty()) return "No messages sent.";
        StringBuilder sb = new StringBuilder();
        for (Message m : SENT) {
            sb.append("ID=").append(m.messageId)
                    .append(", Hash=").append(m.messageHash)
                    .append(", To=").append(m.recipient)
                    .append(", Msg=").append(m.text)
                    .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalSent;
    }

    public void storeMessage() {
        String json = toJson();
        Path p = Path.of("messages.json");
        try {
            if (Files.exists(p)) {
                String existing = Files.readString(p, StandardCharsets.UTF_8).trim();
                if (existing.isEmpty() || existing.equals("[]")) {
                    Files.writeString(p, "[" + json + "]", StandardCharsets.UTF_8);
                } else if (existing.endsWith("]")) {
                    String updated = existing.substring(0, existing.length() - 1);
                    updated += (existing.length() > 2 ? ",\n" : "") + json + "]";
                    Files.writeString(p, updated, StandardCharsets.UTF_8);
                }
            } else {
                Files.writeString(p, "[" + json + "]", StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }
    }

    private String toJson() {
        String esc = text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{"
                + "\"id\":\"" + messageId + "\","
                + "\"number\":" + messageNumber + ","
                + "\"recipient\":\"" + recipient + "\","
                + "\"message\":\"" + esc + "\","
                + "\"hash\":\"" + messageHash + "\""
                + "}";
    }

    public String getMessageId() {
        return messageId;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public static String listSenderAndRecipientOfSent() {
        StringBuilder sb = new StringBuilder();
        String sender = "0838884567";
        for (int i = 0; i < sentIndex; i++) {
            if (sentMessages[i] != null) {
                sb.append("Sender: ").append(sender)
                        .append(", Recipient: ").append(sentRecipients[i])
                        .append(System.lineSeparator());
            }
        }
        return sb.toString().trim();
    }

    public static String getLongestMessage() {
        String longest = "";
        int maxLen = -1;
        for (int i = 0; i < allIndex; i++) {
            String msg = allMessages[i];
            if (msg != null && msg.length() > maxLen) {
                maxLen = msg.length();
                longest = msg;
            }
        }
        return longest;
    }

    public static String searchByMessageId(String id) {
        for (int i = 0; i < allIndex; i++) {
            if (allMessageIds[i] != null && allMessageIds[i].equals(id)) {
                return "Recipient: " + allRecipients[i] + ", Message: " + allMessages[i];
            }
        }
        return "Message not found.";
    }

    public static String[] searchMessagesByRecipient(String recipient) {
        String[] temp = new String[MAX_MESSAGES];
        int idx = 0;
        for (int i = 0; i < allIndex; i++) {
            if (allRecipients[i] != null
                    && allRecipients[i].equals(recipient)
                    && allActions[i] != 2) {
                temp[idx++] = allMessages[i];
            }
        }
        String[] result = new String[idx];
        System.arraycopy(temp, 0, result, 0, idx);
        return result;
    }

    public static String deleteByHash(String hash) {
        for (int i = 0; i < allIndex; i++) {
            if (allMessageHashes[i] != null && allMessageHashes[i].equals(hash)) {
                String deletedMsg = allMessages[i];

                allMessageHashes[i] = null;
                allMessageIds[i] = null;
                allMessages[i] = null;
                allRecipients[i] = null;
                allActions[i] = 0;

                for (int j = 0; j < sentIndex; j++) {
                    if (sentMessages[j] != null && sentMessages[j].equals(deletedMsg)) {
                        sentMessages[j] = null;
                        sentRecipients[j] = null;
                    }
                }
                for (int j = 0; j < storedIndex; j++) {
                    if (storedMessages[j] != null && storedMessages[j].equals(deletedMsg)) {
                        storedMessages[j] = null;
                    }
                }
                for (int j = 0; j < disregardedIndex; j++) {
                    if (disregardedMessages[j] != null && disregardedMessages[j].equals(deletedMsg)) {
                        disregardedMessages[j] = null;
                    }
                }
                return "Message \"" + deletedMsg + "\" successfully deleted.";
            }
        }
        return "Message not found.";
    }

    public static String buildSentReport() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allIndex; i++) {
            if (allActions[i] == 1 && allMessages[i] != null) {
                sb.append("Message Hash: ").append(allMessageHashes[i])
                        .append(", Recipient: ").append(allRecipients[i])
                        .append(", Message: ").append(allMessages[i])
                        .append(System.lineSeparator());
            }
        }
        return sb.toString().trim();
    }
}

