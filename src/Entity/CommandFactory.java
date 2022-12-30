package Entity;

import Business.GetDirectoryCommand;
import Business.GetFileCommand;
import Helper.URLToString;

public class CommandFactory {
    public static Command getCommand(String url) {
        String fileName = URLToString.getURLParts(url)[URLToString.getURLParts(url).length - 1];
//        Check if the url is a directory or a file
        if (fileName.contains(".") && !fileName.contains("/")) {
            System.out.println("File");
            return new GetFileCommand();
        } else {
            System.out.println("Directory");
            return new GetDirectoryCommand();
        }
    }
}
