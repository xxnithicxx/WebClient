package Business;

import Entity.Command;
import Entity.FileSaver;
import Entity.FolderSaver;
import Helper.HTMLToListOfFile;
import Helper.OpenResponse;

import javax.swing.*;
import java.io.IOException;

public class GetDirectoryCommand extends Command {
    @Override
    public void run() {
//        Get list of file in directory
        byte[] content;
        this.connection.setRequestDirectory(this.url);
        try {
            content = this.connection.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] files = new HTMLToListOfFile().getListOfFiles(new String(content));

//        Create folder
        FolderSaver.saveFolder(this.url);
        String parentFolder = FolderSaver.getFolder(this.url);

//        Run all request file in directory in parallel
        try {
            this.connection.executeMultiple(this.url, files, parentFolder);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection closed", "Error", JOptionPane.ERROR_MESSAGE);
        }

        OpenResponse.showResponse(this.url);
    }
}

