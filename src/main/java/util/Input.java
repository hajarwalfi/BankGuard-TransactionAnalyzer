package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtil() {
        throw new UnsupportedOperationException("Classe utilitaire");
    }

    public static String format(LocalDateTime date) {
        if (date == null) return "N/A";
        return date.format(FORMAT);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}