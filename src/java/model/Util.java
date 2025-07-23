package model;

/**
 *
 * @author Laky
 */
public class Util {

    public static String generateCode() {
        int x = (int) (Math.random() * 1000000);
        return String.format("%06d", x);
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^.{1,8}$");
    }

    public static boolean isInteger(String value) {
        return value.matches("^\\d+$");
    }

    public static boolean isPrice(String value) {
        // Accept only positive decimal numbers, not zero or leading zeros
        if (!value.matches("^\\d+(\\.\\d{1,2})?$")) {
            return false;
        }
        try {
            double d = Double.parseDouble(value);
            // Reject zero and negative, and leading zeros like 00, 000, 00.00
            return d > 0 && !value.matches("^0+(\\.0{1,2})?$");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLinkValid(String link) {
        // Accept only http:// or https:// links (basic structure)
        String regex = "^(https?://)" // protocol
                + "([\\w.-]+)" // domain or subdomain
                + "(\\.[a-zA-Z]{2,})" // top-level domain
                + "(:\\d{1,5})?" // optional port
                + "(/[^\\s]*)?$";                 // optional path/query

        return link != null && link.matches(regex);
    }

}
