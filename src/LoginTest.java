import static org.junit.Assert.*;
import org.junit.Test;
public class LoginTest {

    // Test data supplied in the assignment

    @Test
    public void testUsernameCorrectlyFormatted() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "Ch&8sec@ke99!",
                "+27838968976"
        );

        assertTrue(user.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyle!!!!!!!",
                "Ch&8sec@ke99!",
                "+27838968976"
        );

        assertFalse(user.checkUserName());
    }

    @Test
    public void testPasswordMeetsComplexity() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "Ch&8sec@ke99!",
                "+27838968976"
        );

        assertTrue(user.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "password",
                "+27838968976"
        );

        assertFalse(user.checkPasswordComplexity());
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "Ch&8sec@ke99!",
                "+27838968976"
        );

        assertTrue(user.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "Ch&8sec@ke99!",
                "08966553"
        );

        assertFalse(user.checkCellPhoneNumber());
    }

    @Test
    public void testLoginSuccessful() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "Ch&8sec@ke99!",
                "+27838968976"
        );

        user.setLoginDetails("kyl_1", "Ch&8sec@ke99!");

        assertTrue(user.loginUser());
    }

    @Test
    public void testLoginFailed() {

        Login user = new Login(
                "Ethan",
                "Smith",
                "kyl_1",
                "Ch&8sec@ke99!",
                "+27838968976"
        );

        user.setLoginDetails("kyl_1", "password");

        assertFalse(user.loginUser());
    }
}