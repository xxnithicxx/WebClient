package Helper;

import Entity.FolderSaver;

import java.io.File;
import java.io.IOException;

public class OpenResponse {
    public static void showResponse(String url) {
        if (url.charAt(url.length() - 1) == '/') {
            String path = FolderSaver.getFolder(url);

            File file = new File("");
            path = file.getAbsolutePath() + "\\src\\Resource\\" + path + "\\";

            ProcessBuilder pb = new ProcessBuilder("explorer.exe", path);
            try {
                pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String filename = URLToString.getFileName(url);
            filename = URLToString.getHost(url) + "_" + filename;

            String path = "src\\Resource\\" + filename;
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", path);

            try {
                pb.start();
            } catch (Exception e) {
                System.out.println("Can't open file");
            }
        }
    }
}
