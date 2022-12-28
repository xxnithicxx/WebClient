package Business;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class HTTPTCPConnection {
    Socket connectionSocket;
    String requestMessage;

    public HTTPTCPConnection(String url) throws IOException {
        this.connectionSocket = new Socket(url, 80);
    }

    public void setRequestFile(String requestFile, String host) {
        StringBuilder sb = new StringBuilder();

        sb.append("GET /").append(requestFile).append(" HTTP/1.1\r\n");
        sb.append("Connection: keep-alive\r\n");
        sb.append("Host: ").append(host).append("\r\n");
        sb.append("\r\n"); // Important do not miss this line

        this.requestMessage = sb.toString();
    }

    public Map<String, String> getResponseHeader(InputStream inFromServer) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inFromServer.read()) != -1) {
            sb.append((char) c);
            if (sb.toString().endsWith("\r\n\r\n")) {
                break;
            }
        }

        return HTTPResponseParser.parse(sb.toString());
    }

//    Should we extract getResponseHeader() and getResponseContent() into separate function call and input
//    InputStream instead of let getResponseContent() call getResponseHeader()?
    public byte[] getResponseContent() throws IOException {
//        Send request to server
        OutputStream outToServer = connectionSocket.getOutputStream();
        outToServer.write(requestMessage.getBytes());
        outToServer.flush();

//        Get response from server
        InputStream inFromServer = connectionSocket.getInputStream();
        Map<String, String> responseHeader = getResponseHeader(inFromServer);

        int contentLength = Integer.parseInt(responseHeader.get("Content-Length"));
        byte[] response = new byte[contentLength];
        int bytesRead = 0;

        while (bytesRead < contentLength) {
            bytesRead += inFromServer.read(response, bytesRead, contentLength - bytesRead);
        }

        return response;
    }

    public static void main(String[] args) {
        try {
            HTTPTCPConnection connection = new HTTPTCPConnection("gaia.cs.umass.edu");
            connection.setRequestFile("wireshark-labs/alice.txt", "gaia.cs.umass.edu");
            byte[] response = connection.getResponseContent();
            System.out.println(new String(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
