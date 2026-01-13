package pack;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String value) {
        try {
            int num = Integer.parseInt(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        // YYYY-MM-DD
        String pattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(pattern, date);
    }

    public static boolean isValidPhone(String phone) {
        return phone.matches("\\d{9}");
    }

    public static boolean isValidName(String name) {
        // imie/naziwkso: tylko litery, spacje i myslniki
        return name.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s\\-]+");
    }

    public static boolean isValidEmail(String email) {
        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(pattern, email);
    }

    public static boolean isValidNrDowodu(String nrDowodu) {
        return nrDowodu != null && nrDowodu.length() == 9;
    }
}