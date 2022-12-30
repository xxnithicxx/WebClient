package Business;


import Entity.FileSaver;
import GUI.DownloaderMenu;
import Helper.URLToString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;

public class HTTPTCPConnection {
    Socket connectionSocket;
    final String host;
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

    public void setRequestDirectory(String url) {
        String requestDirectory = URLToString.getRequestFile(url);

        this.requestMessage = "GET /" + requestDirectory + "/ HTTP/1.1\r\n" +
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

//        We remove the last 2 bytes of the response, which is "\r\n"
        inFromServer.readNBytes(2);

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

        if (responseHeader.containsKey("Location")) {
            String newUrl = responseHeader.get("Location");
            this.setRequestFile(newUrl);
            return this.execute();
        }

        if (responseHeader.get("Content-Length") == null) {
            return getResponseContent(inFromServer);
        } else {
            return getResponseContent(inFromServer, Integer.parseInt(responseHeader.get("Content-Length")));
        }
    }

    synchronized public void executeMultiple(String url, String[] files, String parentFolder) throws IOException {
        if (this.connectionSocket.isConnected()) {
            this.connectionSocket = new Socket(host, 80);
        }

        Thread output = new Thread(() -> {
            try {
                OutputStream outToServer = connectionSocket.getOutputStream();
                for (String file : files) {
                    String requestMessage = "GET /" + URLToString.getRequestFile(url) + "/" + file + " HTTP/1.1\r\n" +
                            "Connection: keep-alive\r\n" +
                            "Host: " + this.host + "\r\n" +
                            "\r\n";
                    outToServer.write(requestMessage.getBytes());
                    outToServer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread input = new Thread(() -> {
            try {
                InputStream inFromServer = connectionSocket.getInputStream();
                Map<String, String> responseHeader;
                byte[] responseContent;

                for (String file : files) {
                    responseHeader = getResponseHeader(inFromServer);
                    if (responseHeader.get("Content-Length") == null) {
                        responseContent = getResponseContent(inFromServer);
                    } else {
                        responseContent = getResponseContent(inFromServer, Integer.parseInt(responseHeader.get("Content-Length")));
                    }

                    FileSaver.saveFile(url + file, responseContent, parentFolder);
                    DownloaderMenu.getInstance().addLink(url + file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        output.start();
        input.start();

        try {
            output.join();
            input.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws IOException {
        connectionSocket.close();
    }

    public boolean isClosed() {
        return connectionSocket.isClosed();
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("web.stanford.edu", 80)) {
            String requestMessage = "GET /class/cs224w/slides/ HTTP/1.1\r\n" +
                    "Connection: keep-alive\r\n" +
                    "Host: web.stanford.edu\r\n" +
                    "\r\n";

            OutputStream outToServer = socket.getOutputStream();
            outToServer.write(requestMessage.getBytes());
            outToServer.flush();

            InputStream inFromServer = socket.getInputStream();
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = inFromServer.read()) != -1) {
                sb.append((char) c);
                if (sb.toString().endsWith("\r\n\r\n")) {
                    break;
                }
            }

            Map<String, String> responseHeader = HTTPResponseParser.parse(sb.toString());
            System.out.println(responseHeader);

            while ((c = inFromServer.read()) != -1) {
                System.out.print((char) c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @implNote This method is using Content-Length to determine the size of the response content with multiple
     * requests first then receive the response content after that
     */
    private static void TestCase1() {
        try {
            HTTPTCPConnection connection = new HTTPTCPConnection("http://web.stanford.edu/class/cs231a/assignments.html");
            OutputStream outToServer = connection.connectionSocket.getOutputStream();
            InputStream inFromServer = connection.connectionSocket.getInputStream();

            connection.setRequestFile("http://web.stanford.edu/class/cs231a/project.html");
            outToServer.write(connection.requestMessage.getBytes());
            outToServer.flush();

            connection.setRequestFile("http://web.stanford.edu/class/cs231a/assignments.html");
            outToServer.write(connection.requestMessage.getBytes());
            outToServer.flush();

            Map<String, String> responseHeader = connection.getResponseHeader(inFromServer);
            System.out.println(responseHeader);

            byte[] response = connection.getResponseContent(inFromServer, Integer.parseInt(responseHeader.get("Content-Length")));
            System.out.println(new String(response));

            responseHeader = connection.getResponseHeader(inFromServer);
            System.out.println(responseHeader);

            response = connection.getResponseContent(inFromServer, Integer.parseInt(responseHeader.get("Content-Length")));
            System.out.println(new String(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @implNote This method is using Transfer-Encoding: chunked to determine the size of the response content with
     * multiple requests first then receive the response content after that
     */
    private static void TestCase2() {
        try {
            HTTPTCPConnection connection = new HTTPTCPConnection("http://www.google.com");
            OutputStream outToServer = connection.connectionSocket.getOutputStream();
            InputStream inFromServer = connection.connectionSocket.getInputStream();

            connection.setRequestFile("http://www.google.com/index.html");
            outToServer.write(connection.requestMessage.getBytes());
            outToServer.flush();

            connection.setRequestFile("http://www.google.com");
            outToServer.write(connection.requestMessage.getBytes());
            outToServer.flush();

            Map<String, String> responseHeader = connection.getResponseHeader(inFromServer);
            System.out.println(responseHeader);

            StringBuilder sb = new StringBuilder();
            int c;
            byte[] response = new byte[0];
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
            System.out.println(new String(response));

            inFromServer.readNBytes(2);
            responseHeader = connection.getResponseHeader(inFromServer);
            System.out.println(responseHeader);
//            Clear sb buffer
            sb = new StringBuilder();
            response = new byte[0];
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
            System.out.println(new String(response));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
