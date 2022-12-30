package Business;

import Entity.Command;
import Entity.FileSaver;
import Helper.OpenResponse;

import java.io.IOException;

public class GetFileCommand extends Command {
    @Override
    public void run() {
        byte[] content;
        this.connection.setRequestFile(this.url);
        try {
            content = this.connection.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileSaver.saveFile(this.url, content);

        this.downloaderMenu.addLink(this.url);
        OpenResponse.showResponse(this.url);
    }
}
