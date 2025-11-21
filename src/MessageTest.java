import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    private Message[] loadPart3TestData() {
        Message.resetArrays();

        Message m1 = new Message("1111111111", 1, "+27834557896",
                "Did you get the cake?");
        m1.setAction(1);
        m1.SentMessage();

        Message m2 = new Message("2222222222", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m2.setAction(3);
        m2.SentMessage();

        Message m3 = new Message("3333333333", 3, "+27834484567",
                "Yohoooo, I am at your gate.");
        m3.setAction(2);
        m3.SentMessage();

        Message m4 = new Message("4444444444", 4, "0838884567",
                "It is dinner time !");
        m4.setAction(1);
        m4.SentMessage();

        Message m5 = new Message("5555555555", 5, "+27838884567",
                "Ok, I am leaving without you.");
        m5.setAction(3);
        m5.SentMessage();

        return new Message[]{m1, m2, m3, m4, m5};
    }

    @Test
    void messageLength_successAndFailure() {
        Message ok = new Message("0012345678", 0, "+27821234567", "Hi there");
        assertEquals("Message ready to send.", ok.messageLengthStatus());

        String over = "a".repeat(260);
        Message tooLong = new Message("0012345678", 0, "+27821234567", over);
        assertEquals("Message exceeds 250 characters by 10, please reduce size.", tooLong.messageLengthStatus());
    }

    @Test
    void recipientFormat_successAndFailure() {
        Message good = new Message("0012345678", 0, "+27821234567", "x");
        assertEquals(1, good.checkRecipientCell());
        Message bad = new Message("0012345678", 0, "0821234567", "x");
        assertEquals(0, bad.checkRecipientCell());
    }

    @Test
    void messageHash_example() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight");
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    void messageId_isTenDigits() {
        String id = Message.generateId();
        assertTrue(id.matches("\\d{10}"));
        Message m = new Message(id, 1, "+27821234567", "hi");
        assertTrue(m.checkMessageID());
    }

    @Test
    void sentMessage_variants() {
        Message send = new Message("0012345678", 2, "+27821234567", "ok");
        send.setAction(1);
        assertEquals("Message successfully sent.", send.SentMessage());

        Message discard = new Message("0012345678", 3, "+27821234567", "ok");
        discard.setAction(2);
        assertEquals("Press 0 to delete message.", discard.SentMessage());

        Message store = new Message("0012345678", 4, "+27821234567", "ok");
        store.setAction(3);
        assertEquals("Message successfully stored.", store.SentMessage());
    }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        loadPart3TestData();
        String[] expected = {"Did you get the cake?", "It is dinner time !"};
        String[] actual = Arrays.copyOf(Message.sentMessages, Message.sentIndex);
        assertArrayEquals(expected, actual);
    }

    @Test
    void displayLongestMessage() {
        loadPart3TestData();
        String longest = Message.getLongestMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    @Test
    void searchForMessageId() {
        Message[] msgs = loadPart3TestData();
        String idOf4 = msgs[3].getMessageId();
        String result = Message.searchByMessageId(idOf4);
        assertTrue(result.contains("0838884567"));
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    void searchAllMessagesForRecipient() {
        loadPart3TestData();
        String[] found = Message.searchMessagesByRecipient("+27838884567");
        String[] expected = {
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."
        };
        assertArrayEquals(expected, found);
    }

    @Test
    void deleteMessageUsingHash() {
        Message[] msgs = loadPart3TestData();
        String hashOf2 = msgs[1].getMessageHash();
        String result = Message.deleteByHash(hashOf2);
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("successfully deleted"));
    }

    @Test
    void displayReportOfSentMessages() {
        loadPart3TestData();
        String report = Message.buildSentReport();
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time !"));
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Recipient:"));
    }
}
