package Helper;

import java.util.Vector;

// Example: http://example.com/index.html
public class URLToString {
    public static String getHost(String url) {
        return url.split("/")[2];
    }

    public static String getRequestFile(String url) {
        String[] urlParts = url.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < urlParts.length; i++) {
            sb.append(urlParts[i]);
            if (i != urlParts.length - 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    public static String getFileName(String url) {
        String[] urlParts = url.split("/");
        return urlParts[urlParts.length - 1];
    }

    public static String getProtocol(String url) {
        return url.split(":")[0];
    }

    public static String[] getURLParts(String url) {
        return url.split("/");
    }

    public static void main(String[] args) {
        String url = "http://example.com/index.html";
        System.out.println(getHost(url));
        System.out.println(getRequestFile(url));
        System.out.println(getProtocol(url));
    }
}