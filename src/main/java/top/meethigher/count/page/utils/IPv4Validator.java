package top.meethigher.count.page.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPv4Validator {

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidIPv4(String ipAddress) {
        Matcher matcher = IPV4_PATTERN.matcher(ipAddress);
        return matcher.matches();
    }
}
