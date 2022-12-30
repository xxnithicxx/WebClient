package Entity;

import Business.HTTPTCPConnection;
import GUI.DownloaderMenu;
import Helper.URLToString;

import java.io.IOException;
import java.util.Vector;

// This class is responsible for managing the requests and TCP connections, all work is delegated to Command objects
public class RequestManager {
    final Vector<HTTPTCPConnection> activeConnections = new Vector<>();
    final Vector<Thread> activeThreads = new Vector<>();

    public HTTPTCPConnection addRequest(String url) throws IOException {
        String host = URLToString.getHost(url);
        this.checkConnections();

        for (HTTPTCPConnection connection : activeConnections) {
            if (connection.getHost().equals(host)) {
                return connection;
            }
        }

        HTTPTCPConnection newConnection = new HTTPTCPConnection(url);
        activeConnections.add(newConnection);

        return newConnection;
    }

    public void closeAllConnections() throws IOException {
        for (HTTPTCPConnection connection : activeConnections) {
            connection.closeConnection();
        }

        for (Thread thread : activeThreads) {
            thread.interrupt();
        }
    }

    public void runRequest(String url) throws IOException {
        HTTPTCPConnection connection = addRequest(url);
        Command command = CommandFactory.getCommand(url);

        command.setConnection(connection);
        command.setUrl(url);
        command.setDownloaderMenu(DownloaderMenu.getInstance());

//        Run the command in a new thread
        Thread thread = new Thread(command);
        activeThreads.add(thread);
        thread.start();
    }

    public void checkConnections() {
        activeConnections.removeIf(HTTPTCPConnection::isClosed);
    }

    public static void main(String[] args) {
        RequestManager manager = new RequestManager();
        try {
            manager.runRequest("http://example.com/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
