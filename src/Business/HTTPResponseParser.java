package Business;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponseParser {
    public static Map<String, String> parse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        Map<String, String> responseHeader = new HashMap<>();
        String[] lines = response.split("\r\n");
        String[] firstLine = lines[0].split(" ");
        responseHeader.put("HTTPVersion", firstLine[0]);
        responseHeader.put("StatusCode", firstLine[1]);
        responseHeader.put("StatusMessage", firstLine[2]);

        for (int i = 1; i < lines.length; i++) {
            if (!lines[i].isEmpty()) {
                String[] header = lines[i].split(": ");
                responseHeader.put(header[0], header[1]);
            }
            else
                break;
        }

        return responseHeader;
    }

    public static void main(String[] args) {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                "Server: Apache\r\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                "ETag: \"34aa387-d-1568eb00\"\r\n" +
                "Accept-Ranges: bytes\r\n" +
                "Content-Length: 51\r\n" +
                "Vary: Accept-Encoding\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Hello World! My payload includes a trailing CRLF.";
        Map<String, String> responseHeader = parse(response);


        System.out.println(responseHeader);
    }
}
