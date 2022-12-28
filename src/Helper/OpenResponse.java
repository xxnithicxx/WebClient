package Helper;

public class OpenResponse {
    public static void showResponse(String url) {
        if (url.charAt(url.length() - 1) == '/') {
            System.out.println("Directory");
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
