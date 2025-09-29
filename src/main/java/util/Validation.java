package util;

public class ValidationUtil {

    private ValidationUtil() {
        throw new UnsupportedOperationException("Classe utilitaire");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return email.contains("@") && email.contains(".");
    }

    public static boolean isNumber(String str) {
        if (isEmpty(str)) return false;
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositive(double number) {
        return number > 0;
    }
}