package Entity;

import Helper.URLToString;

import java.io.File;

public class FolderSaver {
    public static void saveFolder(String url) {
        String folderName = FolderSaver.getFolder(url);

//        Create folder
        File folder = new File("src/Resource/" + folderName);
        if (folder.mkdir()) {
            System.out.println("Folder created: " + folder.getName());
        } else {
            System.out.println("Overwrite folder: " + folder.getName());
        }
    }

    public static String getFolder(String url) {
        String folderName = URLToString.getFileName(url);
        folderName = URLToString.getHost(url) + "_" + folderName;

        return folderName;
    }
}
