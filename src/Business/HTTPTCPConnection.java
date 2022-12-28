package Business;

import Helper.URLToString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class HTTPTCPConnection {
    Socket connectionSocket;
    String host;
    String requestMessage;

    public HTTPTCPConnection(String url) throws IOException {
        this.host = URLToString.getHost(url);
        this.connectionSocket = new Socket(host, 80);
    }

    public String getHost() {
        return host;
    }

    public void setRequestFile(String url) {
        String requestFile = URLToString.getRequestFile(url);

        this.requestMessage = "GET /" + requestFile + " HTTP/1.1\r\n" +
                "Connection: keep-alive\r\n" +
                "Host: " + this.host + "\r\n" +
                "\r\n";
    }

    /**
     * @param inFromServer InputStream from the server
     * @return Map of the response headers with key: value pairs
     * @throws IOException if there is an error reading from the server
     * @implNote This method will read the response headers from the server and convert them to a Map, consume input
     * stream
     */
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

//    Should we extract getResponseHeader() and getResponseContent() into separate function call and pass
//    InputStream instead of let getResponseContent() call getResponseHeader()? Yes, we should, to maintain SOLID

    /**
     * @param inFromServer  InputStream from server
     * @param contentLength Content-Length from response header
     * @return Response content
     * @throws IOException If there is any error occurs while reading from InputStream
     * @implNote This method will consume the InputStream, so you should call getResponseHeader() first This method is
     * case of "Content-Length" is present in response header
     */
    public byte[] getResponseContent(InputStream inFromServer, int contentLength) throws IOException {
        byte[] response = new byte[contentLength];
        int bytesRead = 0;

        while (bytesRead < contentLength) {
            bytesRead += inFromServer.read(response, bytesRead, contentLength - bytesRead);
        }

        return response;
    }

    /**
     * @param inFromServer InputStream from server
     * @return Response content
     * @throws IOException If there is any error occurs while reading from InputStream
     * @implNote This method will consume the InputStream, so you should call getResponseHeader() first This method is
     * case of "Transfer-Encoding: chunked" is present in response header
     */
    public byte[] getResponseContent(InputStream inFromServer) throws IOException {
        byte[] response = new byte[0];

        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inFromServer.read()) != -1) {
            sb.append((char) c);
            if (sb.toString().endsWith("\r\n")) {
                int chunkSize = Integer.parseInt(sb.toString().trim(), 16);
                if (chunkSize == 0) {
                    break;
                }

                byte[] chunk = new byte[chunkSize];
                int bytesRead = 0;
                while (bytesRead < chunkSize) {
                    bytesRead += inFromServer.read(chunk, bytesRead, chunkSize - bytesRead);
                }

                byte[] temp = new byte[response.length + chunk.length];
                System.arraycopy(response, 0, temp, 0, response.length);
                System.arraycopy(chunk, 0, temp, response.length, chunk.length);
                response = temp;

                sb = new StringBuilder();
                while ((c = inFromServer.read()) != -1) {
                    sb.append((char) c);
                    if (sb.toString().endsWith("\r\n")) {
                        break;
                    }
                }
            }
        }

        return response;
    }


    //    Using synchronized keyword to prevent multiple threads from accessing the same resource at the same time
    //    (Socket in this case)
    synchronized public byte[] execute() throws IOException {
        if (this.connectionSocket.isConnected()) {
            this.connectionSocket = new Socket(host, 80);
        }

//        Send request to server
        OutputStream outToServer = connectionSocket.getOutputStream();
        outToServer.write(requestMessage.getBytes());
        outToServer.flush();

//        Get response from server
        InputStream inFromServer = connectionSocket.getInputStream();
        Map<String, String> responseHeader = getResponseHeader(inFromServer);

        if (responseHeader.get("Content-Length") == null) {
            return getResponseContent(inFromServer);
        } else {
            return getResponseContent(inFromServer, Integer.parseInt(responseHeader.get("Content-Length")));
        }
    }

    public void closeConnection() throws IOException {
        connectionSocket.close();
    }

    public boolean isClosed() {
        return connectionSocket.isClosed();
    }

    public static void main(String[] args) {
        try {
            HTTPTCPConnection connection = new HTTPTCPConnection("http://web.stanford.edu/class/cs231a/assignments.html");
            connection.setRequestFile("http://web.stanford.edu/class/cs231a/assignments.html");
            byte[] response = connection.execute();

            connection.setRequestFile("http://web.stanford.edu/class/cs231a/project.html");
            response = connection.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
