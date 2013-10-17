package org.symqle.modeler.utils;

/**
 * @author lvovich
 */
public final class StringUtils {

    public static String camelize(final String source) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean nextToUpper = false;
        for (char c: source.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c)) {
                nextToUpper = true;
                // do not append to builder
            } else {
                stringBuilder.append(nextToUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
            }
        }
        return stringBuilder.toString();
    }

    public static String firstToUpper(final String source) {
        return source.length() == 0 ? source :
                Character.toUpperCase(source.charAt(0)) + source.substring(1);
    }
}
