package Entity;

import Business.HTTPTCPConnection;

import java.io.IOException;

public abstract class Command {
    protected HTTPTCPConnection connection;
    protected String url;

    public abstract void execute() throws IOException;

    public void setConnection(HTTPTCPConnection connection) {
        this.connection = connection;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
