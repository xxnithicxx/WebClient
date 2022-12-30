package Entity;

import Business.HTTPTCPConnection;
import GUI.DownloaderMenu;

public abstract class Command implements Runnable {
    protected HTTPTCPConnection connection;
    protected String url;
    protected DownloaderMenu downloaderMenu;

    public abstract void run();

    public void setConnection(HTTPTCPConnection connection) {
        this.connection = connection;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setDownloaderMenu(DownloaderMenu downloaderMenu) {
        this.downloaderMenu = downloaderMenu;
    }
//    TODO: Handle interrupt thread call here

}
