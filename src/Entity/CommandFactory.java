package Entity;

import Business.GetDirectoryCommand;
import Business.GetFileCommand;
import Helper.URLToString;

public class CommandFactory {
    public static Command getCommand(String url) {
        String fileName = URLToString.getURLParts(url)[URLToString.getURLParts(url).length - 1];
        if (!fileName.contains(".") && url.endsWith("/"))
            return new GetDirectoryCommand();
        else
            return new GetFileCommand();
    }
}
