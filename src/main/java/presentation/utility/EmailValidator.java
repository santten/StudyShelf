package presentation.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private EmailValidator(){}

    public static boolean isValidEmail(String email) {

        if (email == null || email.isEmpty() || email.length() > 256) {
            return false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
