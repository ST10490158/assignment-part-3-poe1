public class Login {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    private String loginUsername;
    private String loginPassword;

    public Login(String firstName, String lastName, String username,
                 String password, String cellPhoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    // Checks if username contains "_" and is 5 characters or less
    public boolean checkUserName() {
        return username.contains("_") && username.length() <= 5;
    }

    // Checks password 
    public boolean checkPasswordComplexity() {

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (char character : password.toCharArray()) {

            if (Character.isUpperCase(character)) {
                hasCapital = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return password.length() >= 8
                && hasCapital
                && hasNumber
                && hasSpecialCharacter;
    }

    // Checks South African international cellphone format
    public boolean checkCellPhoneNumber() {
        return cellPhoneNumber.matches("^\\+27\\d{9}$");
    }

    // Returns registration message
    public String registerUser() {

        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that "
                    + "your username contains an underscore and is no more than "
                    + "five characters in length.";
        }

        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that "
                    + "the password contains at least eight characters, a capital "
                    + "letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain "
                    + "international code.";
        }

        return "User has been registered successfully.";
    }

    // Stores username and password entered during login
    public void setLoginDetails(String loginUsername, String loginPassword) {
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
    }

    // Checks if login details match registration details
    public boolean loginUser() {
        return username.equals(loginUsername)
                && password.equals(loginPassword);
    }

    // Returns successful or failed login message
    public String returnLoginStatus() {

        if (loginUser()) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}