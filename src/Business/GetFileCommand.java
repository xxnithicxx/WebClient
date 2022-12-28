package Business;

import Entity.Command;
import Entity.FileSaver;
import Helper.OpenResponse;

import java.io.IOException;

public class GetFileCommand extends Command {
    @Override
    public void execute() {
        byte[] content;
        try {
            content = this.connection.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileSaver.saveFile(this.url, content);
        OpenResponse.showResponse(this.url);
    }
}
