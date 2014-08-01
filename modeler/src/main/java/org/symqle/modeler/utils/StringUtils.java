package org.symqle.modeler.utils;

/**
 * @author lvovich
 */
public final class StringUtils {

    private StringUtils() {
    }

    static {
        new StringUtils();
    }

    public static String camelize(final String source) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean nextToUpper = false;
        for (char c: source.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                nextToUpper = true;
                // do not append to builder
            } else {
                stringBuilder.append(nextToUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                nextToUpper = false;
            }
        }
        return stringBuilder.toString();
    }

    public static String firstToUpper(final String source) {
        return Character.toUpperCase(source.charAt(0)) + source.substring(1);
    }
}
