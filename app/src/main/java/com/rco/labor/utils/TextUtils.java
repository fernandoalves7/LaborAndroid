package com.rco.labor.utils;

import java.util.Locale;
import java.util.Vector;

/**
 * Created by Fernando on 8/27/2018.
 */

public class TextUtils {
    public static String mask(String value, char maskChar) {
        if (value == null)
            return null;

        StringBuilder result = new StringBuilder();

        for (int i=0; i<value.length(); i++)
            result.append(maskChar);

        return result.toString();
    }

    public static String toLowerCase(String value) {
        if (isNullOrWhitespaces(value))
            return "";

        return value.toLowerCase(Locale.US);
    }

    public static String spaceTab(String text, int tabSize) {
        if (isNullOrWhitespaces(text))
            return spaceTab(tabSize);

        return text.length() < tabSize ?
                text + spaceTab(tabSize - text.length()) : text;
    }

    public static String spaceTab(int tabSize) {
        if (tabSize <= 0)
            return "";

        StringBuilder result = new StringBuilder();

        for (int i=0; i<tabSize; i++)
            result.append(' ');

        return result.toString();
    }

    public static String dashIfEmpty(String text) {
        return isNullOrWhitespaces(text) ? "-" : text;
    }

    public static boolean equalsIgnoreCase(String text1, String text2) {
        if (text1 == null && text2 == null)
            return true;

        if (text1 == null || text2 == null) // One of them isn't null for sure
            return false;

        return text1.compareToIgnoreCase(text2) == 0;
    }

    public static boolean equalsIgnoreCaseAny(String src, String[] values) {
        for (String v : values)
            if (equalsIgnoreCase(src, v))
                return true;

        return false;
    }

    public static boolean equalsIgnoreCaseAll(String src, String[] values) {
        for (String v : values)
            if (!equalsIgnoreCase(src, v))
                return false;

        return true;
    }

    public static String valueOrDash(String text) {
        if (com.rco.labor.utils.TextUtils.isNullOrWhitespaces(text))
            return "-";

        return text;
    }

    public static String substrWithEndingFlag(String text, int length) {
        if (text == null || text.length() < length)
            return text;

        return text.substring(0, length) + "...";
    }

    public static String substr(String text, int length) {
        if (text == null || text.length() < length)
            return text;

        return text.substring(0, length);
    }

    public static boolean isNullOrWhitespacesAll(String[] values) {
        for (String v : values)
            if (!isNullOrWhitespaces(v))
                return false;

        return true;
    }

    public static boolean isNullOrWhitespacesAny(String[] values) {
        for (String v : values)
            if (isNullOrWhitespaces(v))
                return true;

        return false;
    }

    public static boolean isNullOrWhitespaces(String text) {
        if (text == null)
            return true;

        if (text.trim().compareTo("") == 0)
            return true;

        for (int i=0; i<text.length(); i++)
            if (text.charAt(i) != ' ')
                return false;

        return true;
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.trim().compareTo("") == 0;
    }

    public static boolean isFloatingPoint(String text) {
        for (int i=0; i<text.length(); i++)
            if (!java.lang.Character.isDigit(text.charAt(i)) && text.charAt(i) != '.')
                return false;

        return true;
    }

    public static boolean isNumeric(String text) {
        for (int i=0; i<text.length(); i++)
            if (!java.lang.Character.isDigit(text.charAt(i)))
                return false;

        return true;
    }

    public static boolean hasNumericValue(String text) {
        try {
            Double.parseDouble(text);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    public static boolean isAlphabetic(String text) {
        for (int i=0; i<text.length(); i++)
            if (!isAlphabetic(text.charAt(i)))
                return false;

        return true;
    }

    public static boolean isAlphabetic(char ch) {
        char[] alphabet = new char[] {
                'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
                'f', 'g', 'h', 'j', 'k', 'l', 'A', 'A', 'A', 'z', 'x', 'c', 'v',
                'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
                'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'A', 'A', 'A', 'Z',
                'X', 'C', 'V', 'B', 'N', 'M'
        };

        for (int i=0; i<alphabet.length; i++)
            if (alphabet[i] == ch)
                return true;

        return false;
    }

    public static int countWords(String source, String word) {
        if (source.indexOf(word) == -1)
            return 0;

        int counter = 0;

        while (source.indexOf(word) != -1) {
            counter++;

            if (source.indexOf(word) + word.length() > source.length())
                break;

            source = source.substring(source.indexOf(word) + word.length());
        }

        return counter;
    }

    public static String[] split(String original, String separator) {
        return split(original, separator, false);
    }

    public static String[] split(String original, String separator, boolean trimLines) {
        if (original == null)
            return null;

        Vector nodes = new Vector();

        int index = original.indexOf(separator);

        while(index >= 0) {
            String line = original.substring(0, index);

            if (trimLines && line.trim().compareTo(separator) == 0)
                continue;

            nodes.addElement(line);
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }

        if (!trimLines || (trimLines && original.trim().compareTo(separator) == 0))
            nodes.addElement(original);

        String[] result = new String[nodes.size()];

        if (nodes.size()>0)
            for (int loop=0; loop<nodes.size(); loop++)
                result[loop] = (String) nodes.elementAt(loop);

        return result;
    }

    public static String concat(String[] original, String needle) {
        String result = "";

        for (int i=0; i<original.length; i++)
            result += original[i] + needle;

        return trimEnd(result, needle);
    }

    public static boolean contains(String source, String text) {
        return contains(source, text, true);
    }

    public static boolean contains(String source, String text, boolean isCaseInsensitive) {
        if (source == null && text == null)
            return true;

        if (source == null || text == null)
            return false;

        return isCaseInsensitive ?
                source.toLowerCase().indexOf(text.toLowerCase()) != -1 : source.indexOf(text) != -1;
    }

    public static boolean containsAnyWord(String source, String query) {
        String[] words = split(query, " ");

        for (int i=0; i<words.length; i++)
            if (source.indexOf(words[i]) != -1)
                return true;

        return false;
    }

    public static boolean containsAnyWord(String source, String[] words) {
        for (int i=0; i<words.length; i++)
            if (source.indexOf(words[i]) != -1)
                return true;

        return false;
    }

    public static String trim(String text, String needle) {
        if (text.length() < needle.length())
            return text;

        while (text.substring(0, needle.length()).compareTo(needle) == 0)
            text = text.substring(needle.length());

        while (text.substring(text.length() - needle.length()).compareTo(needle) == 0)
            text = text.substring(0, text.length() - needle.length());

        return text;
    }

    public static String trimEnd(String text, String needle) {
        return
                text != null &&
                        text.length() > needle.length() &&
                        text.substring(text.length()-needle.length()).compareTo(needle) == 0 ?

                        text.substring(0, text.length()-needle.length()) : text;
    }

    public static String replace(String text, String searchString, String newString) {
        StringBuffer sb = new StringBuffer();

        int searchStringPosition = text.indexOf(searchString);
        int startPosition = 0;
        int searchStringLength = searchString.length();

        while (searchStringPosition != -1) {
            sb.append(text.substring(startPosition, searchStringPosition)).append(newString);
            startPosition = searchStringPosition + searchStringLength;
            searchStringPosition = text.indexOf(searchString, startPosition);
        }

        sb.append(text.substring(startPosition, text.length()));

        return sb.toString();
    }

    public static String removeAccents(String text) {
        if (isNullOrEmpty(text))
            return text;

        text = TextUtils.replaceOneOf(text, "Ãƒâ‚¬ÃƒAÃƒâ€šÃƒÆ’Ãƒâ€žÃƒâ€¦".toCharArray(), 'A');
        text = TextUtils.replaceOneOf(text, "Ãƒâ€™Ãƒâ€œÃƒâ€?Ãƒâ€¢Ãƒâ€¢Ãƒâ€“ÃƒËœ".toCharArray(), 'O');
        text = TextUtils.replaceOneOf(text, "ÃƒË†Ãƒâ€°ÃƒÅ Ãƒâ€¹".toCharArray(), 'E');
        text = TextUtils.replaceOneOf(text, "Ãƒâ€¡".toCharArray(), 'C');
        text = TextUtils.replaceOneOf(text, "Ãƒï¿½".toCharArray(), 'D');
        text = TextUtils.replaceOneOf(text, "ÃƒÅ’Ãƒï¿½ÃƒÅ½Ãƒï¿½".toCharArray(), 'I');
        text = TextUtils.replaceOneOf(text, "Ãƒâ„¢ÃƒÅ¡Ãƒâ€ºÃƒÅ“".toCharArray(), 'U');
        text = TextUtils.replaceOneOf(text, "Ãƒâ€˜".toCharArray(), 'N');
        text = TextUtils.replaceOneOf(text, "Ã…Â ".toCharArray(), 'S');
        text = TextUtils.replaceOneOf(text, "Ã…Â¸".toCharArray(), 'Y');
        text = TextUtils.replaceOneOf(text, "Ã…Â½".toCharArray(), 'Z');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ Ã¡ÃƒÂ¢Ã£ÃƒÂ¤ÃƒÂ¥".toCharArray(), 'a');
        text = TextUtils.replaceOneOf(text, "Ã§".toCharArray(), 'c');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ¨ÃƒÂ©ÃªÃƒÂ«".toCharArray(), 'e');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ¬Ã­ÃƒÂ®ÃƒÂ¯".toCharArray(), 'i');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ±".toCharArray(), 'n');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ²ÃƒÂ³ÃƒÂ´ÃµÃƒÂ¶ÃƒÂ¸ÃƒÂ°".toCharArray(), 'o');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ¹ÃƒÂºÃƒÂ»ÃƒÂ¼".toCharArray(), 'u');
        text = TextUtils.replaceOneOf(text, "ÃƒÂ½ÃƒÂ¿".toCharArray(), 'y');
        text = TextUtils.replaceOneOf(text, "Ã…Â¡".toCharArray(), 's');
        text = TextUtils.replaceOneOf(text, "Ã…Â¾".toCharArray(), 'z');

        return text;
    }

    public static String replaceOneOf(String text, char[] chars, char newChar) {
        for (int i=0; i<chars.length; i++)
            text = text.replace(chars[i], newChar);

        return text;
    }

    public static String getStringEnumeration(String[] enumeration, int startIndex, int length) {
        String result = "";

        for (int i=startIndex; i<startIndex+length; i++)
            if (!isNullOrEmpty(enumeration[i]))
                result += ", " + enumeration[i];

        return trim(result, ", ");
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + (text.length() > 1 ? text.substring(1).toLowerCase() : "");
    }

    public static String println(String[] text) {
        if (text == null || text.length == 0)
            return null;

        String result = "";

        for (int i=0; i<text.length; i++)
            result += text[i] + "\n";

        return result;
    }

    public static boolean isValidEmail(CharSequence email) {
        try {
            if (email == null) {
                return false;
            } else {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }

        } catch (Exception e) {
            System.out.println("error is: " + e.getMessage());
        }
        return false;
    }

    public static String convertToHex(String input)
    {
        int no = Integer.parseInt(input);
        String hex = Integer.toHexString(no);

        if (hex.length() < 2)
            hex = "0" + hex;

        return hex;
    }

    public static String addCeroToLeft(int value) {
        String convert = "";
        if (value < 10) {
            convert = "0" + value;
        } else {
            convert = "" + value;
        }
        return convert;
    }

    public static String replaceAllQuotes(String value) {
        if (value == null) return null;
        String convert = value.replace("\"", "");
        convert = convert.replace("'", "");
        return convert;
    }

    public static String removeDoubleFromStartAndEndOfStringAndTrim(String inputStr) {
        String result = inputStr;
        if (result != null) {
            result = result.trim();
            int firstQuote = result.indexOf('\"');
            if (firstQuote == 0)
            {
                result = result.substring(1);
            }
            int lastQuote = result.lastIndexOf('\"');
            int strLength = result.length();
            if (lastQuote == strLength - 1)
            {
                result = result.substring(0, strLength - 1);
            }
            result = result.trim();
        }
        return result;
    }

    public static String checkText(String text, String value) {
        if (!text.equals("")) {
            text = text + "," + value;
        } else {
            text = value;
        }
        return text;
    }
}
