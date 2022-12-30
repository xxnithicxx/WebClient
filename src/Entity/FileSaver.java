package Entity;

import Helper.URLToString;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSaver {
    public static void saveFile(String url, byte[] data) {
        String filename = URLToString.getFileName(url);

        if (filename.equals(URLToString.getHost(url)))
            filename = "index.html";

        filename = URLToString.getHost(url) + "_" + filename;

        File file = new File("src/Resource/" + filename);

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("Overwrite file: " + file.getName());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't not create file in Resource directory", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Can't not find file in Resource directory", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't not write file in Resource directory", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveFile(String url, byte[] data, String parentFolder) {
        String filename = URLToString.getFileName(url);

        if (filename.equals(URLToString.getHost(url)))
            filename = "index.html";

        String temp = "src/Resource/" + parentFolder + "/" + filename;
        File file = new File("src/Resource/" + parentFolder + "/" + filename);

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("Overwrite file: " + file.getName());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't not create file in Resource directory", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Can't not find file in Resource directory", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't not write file in Resource directory", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}