package controller.sales;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class SalesInputValidator {

    private static final Pattern HEX_COLOR = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final Set<String> ALLOWED_OPPORTUNITY_STAGES = new HashSet<>(Arrays.asList(
        "Qualification", "Need Analysis", "Product Proposal", "Quotation", "Negotiation", "Closed Won", "Closed Lost"
    ));
    private static final Set<String> ALLOWED_OPPORTUNITY_SOURCE = new HashSet<>(Arrays.asList(
        "Manual", "Lead", "Campaign"
    ));

    private SalesInputValidator() {}

    public static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    public static String requireText(String field, String value, int minLen, int maxLen) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " không được để trống");
        }
        if (normalized.length() < minLen) {
            throw new IllegalArgumentException(field + " phải có ít nhất " + minLen + " ký tự");
        }
        if (normalized.length() > maxLen) {
            throw new IllegalArgumentException(field + " không được vượt quá " + maxLen + " ký tự");
        }
        return normalized;
    }

    public static String optionalText(String value, int maxLen) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > maxLen) {
            throw new IllegalArgumentException("Nội dung không được vượt quá " + maxLen + " ký tự");
        }
        return normalized;
    }

    public static int parsePositiveInt(String field, String value) {
        try {
            int number = Integer.parseInt(requireText(field, value, 1, 20));
            if (number <= 0) {
                throw new IllegalArgumentException(field + " phải lớn hơn 0");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(field + " không hợp lệ");
        }
    }

    public static int parsePositiveIntOrDefault(String field, String value, int defaultValue) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return defaultValue;
        }
        return parsePositiveInt(field, normalized);
    }

    public static Integer parseNullablePositiveInt(String field, String value) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }
        return parsePositiveInt(field, normalized);
    }

    public static BigDecimal parseNonNegativeDecimal(String field, String value, BigDecimal defaultValue) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return defaultValue;
        }
        try {
            BigDecimal amount = new BigDecimal(normalized);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(field + " không được âm");
            }
            return amount;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(field + " không hợp lệ");
        }
    }

    public static double parseDoubleInRange(String field, String value, double defaultValue, double min, double max) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return defaultValue;
        }
        try {
            double number = Double.parseDouble(normalized);
            if (number < min || number > max) {
                throw new IllegalArgumentException(field + " phải nằm trong khoảng " + (int) min + " - " + (int) max);
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(field + " không hợp lệ");
        }
    }

    public static Date parseOptionalDate(String field, String value) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            return sdf.parse(normalized);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(field + " không đúng định dạng");
        }
    }

    public static String parseOpportunityStage(String value, String defaultValue) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return defaultValue;
        }
        if (!ALLOWED_OPPORTUNITY_STAGES.contains(normalized)) {
            throw new IllegalArgumentException("Stage không hợp lệ");
        }
        return normalized;
    }

    public static String parseOpportunitySource(String value, String defaultValue) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return defaultValue;
        }
        if (!ALLOWED_OPPORTUNITY_SOURCE.contains(normalized)) {
            throw new IllegalArgumentException("Nguồn không hợp lệ");
        }
        return normalized;
    }

    public static String parseRequiredColor(String value) {
        String normalized = requireText("Màu stage", value, 7, 7);
        if (!HEX_COLOR.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Màu stage không hợp lệ");
        }
        return normalized;
    }

    public static boolean parseBoolean(String value) {
        String normalized = normalize(value);
        return "true".equalsIgnoreCase(normalized)
                || "1".equals(normalized)
                || "on".equalsIgnoreCase(normalized);
    }
}
