import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

public class MessageTest {

    @Before
    public void resetTests() {

        Message.resetMessageData();
    }

    // =========================
    // PART 2 TESTS
    // =========================

    @Test
    public void testMessageLengthSuccess() {

        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertEquals(
                "Message ready to send.",
                message.checkMessageLength()
        );
    }

    @Test
    public void testMessageLengthFailure() {

        String longMessage =
                "A".repeat(251);

        Message message =
                new Message(
                        "+27718693002",
                        longMessage
                );

        assertEquals(
                "Message exceeds 250 characters by 1, please reduce the size.",
                message.checkMessageLength()
        );
    }

    @Test
    public void testRecipientCorrectlyFormatted() {

        Message message =
                new Message(
                        "+27718693002",
                        "Hi Mike, can you join us for dinner tonight?"
                );

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {

        Message message =
                new Message(
                        "0857597589",
                        "Hi Keegan, did you receive the payment?"
                );

        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain "
                        + "an international code. Please correct the number and try again.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testMessageIDCreated() {

        Message message =
                new Message(
                        "+27718693002",
                        "Hi Mike, can you join us for dinner tonight?"
                );

        assertTrue(
                message.checkMessageID()
        );

        assertEquals(
                10,
                message.getMessageID().length()
        );
    }

    @Test
    public void testMessageHash() {

        Message message =
                new Message(
                        "+27718693002",
                        "Hi Mike, can you join us for dinner tonight?"
                );

        String expected =
                message
                        .getMessageID()
                        .substring(0, 2)
                + ":0:HITONIGHT";

        assertEquals(
                expected,
                message.getMessageHash()
        );
    }

    @Test
    public void testSendMessage() {

        Message message =
                new Message(
                        "+27718693002",
                        "Hi Mike, can you join us for dinner tonight?"
                );

        message.setMessageAction(1);

        assertEquals(
                "Message successfully sent.",
                message.SentMessage()
        );
    }

    @Test
    public void testDiscardMessage() {

        Message message =
                new Message(
                        "0857597589",
                        "Hi Keegan, did you receive the payment?"
                );

        message.setMessageAction(2);

        assertEquals(
                "Press 0 to delete the message.",
                message.SentMessage()
        );
    }

    @Test
    public void testStoreMessage() {

        Message message =
                new Message(
                        "+27718693002",
                        "This message will be stored."
                );

        message.setMessageAction(3);

        assertEquals(
                "Message successfully stored.",
                message.SentMessage()
        );
    }

    @Test
    public void testReturnTotalMessages() {

        Message message =
                new Message(
                        "+27718693002",
                        "Hi Mike, can you join us for dinner tonight?"
                );

        message.setMessageAction(1);

        message.SentMessage();

        assertEquals(
                1,
                message.returnTotalMessages()
        );
    }

    @Test
    public void testStoredMessageCreatesJsonFile()
            throws Exception {

        Path filePath =
                Path.of("messages.json");

        Files.deleteIfExists(filePath);

        Message message =
                new Message(
                        "+27718693002",
                        "This message will be stored."
                );

        message.setMessageAction(3);

        assertEquals(
                "Message successfully stored.",
                message.SentMessage()
        );

        assertTrue(
                Files.exists(filePath)
        );

        String content =
                Files.readString(filePath);

        assertTrue(
                content.contains(
                        "+27718693002"
                )
        );

        assertTrue(
                content.contains(
                        "This message will be stored."
                )
        );
    }

    // =========================
    // PART 3 TEST DATA
    // =========================

    private Message[] populatePart3TestData() {

        Message message1 =
                new Message(
                        "Developer",
                        "+27834557896",
                        "Did you get the cake?"
                );

        message1.setMessageAction(1);
        message1.SentMessage();

        Message message2 =
                new Message(
                        "Developer",
                        "+27838884567",
                        "Where are you? You are late! I have asked you to be on time."
                );

        message2.setMessageAction(3);
        message2.SentMessage();

        Message message3 =
                new Message(
                        "Developer",
                        "+27834484567",
                        "Yohoooo, I am at your gate."
                );

        message3.setMessageAction(2);
        message3.SentMessage();

        Message message4 =
                new Message(
                        "Developer",
                        "08338884567",
                        "It is dinner time !"
                );

        message4.setMessageAction(1);
        message4.SentMessage();

        Message message5 =
                new Message(
                        "Developer",
                        "+27838884567",
                        "Ok, I am leaving without you."
                );

        message5.setMessageAction(3);
        message5.SentMessage();

        return new Message[] {
                message1,
                message2,
                message3,
                message4,
                message5
        };
    }

    // =========================
    // PART 3 REQUIRED TESTS
    // =========================

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {

        populatePart3TestData();

        String expected =
                "Did you get the cake?\n"
                + "It is dinner time !";

        assertEquals(
                expected,
                Message.getSentMessagesText()
        );
    }

    @Test
    public void testDisplayLongestMessage() {

        populatePart3TestData();

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                Message.getLongestMessage()
        );
    }

    @Test
    public void testSearchForMessageID() {

        Message[] messages =
                populatePart3TestData();

        String message4ID =
                messages[3].getMessageID();

        assertEquals(
                "It is dinner time !",
                Message.searchMessageByID(
                        message4ID
                )
        );
    }

    @Test
    public void testSearchMessagesForRecipient() {

        populatePart3TestData();

        String expected =
                "Where are you? You are late! I have asked you to be on time.\n"
                + "Ok, I am leaving without you.";

        assertEquals(
                expected,
                Message.searchMessagesByRecipient(
                        "+27838884567"
                )
        );
    }

    @Test
    public void testDeleteMessageUsingMessageHash() {

        Message[] messages =
                populatePart3TestData();

        String message2Hash =
                messages[1].getMessageHash();

        assertEquals(
                "Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                Message.deleteMessageByHash(
                        message2Hash
                )
        );
    }

    @Test
    public void testDisplayReport() {

        Message[] messages =
                populatePart3TestData();

        String expected =
                "Message Hash: "
                + messages[0].getMessageHash()
                + "\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n\n"
                + "Message Hash: "
                + messages[3].getMessageHash()
                + "\n"
                + "Recipient: 08338884567\n"
                + "Message: It is dinner time !";

        assertEquals(
                expected,
                Message.displayReport()
        );
    }
}