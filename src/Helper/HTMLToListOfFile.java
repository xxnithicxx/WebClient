package Helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Vector;
import java.util.regex.Pattern;

public class HTMLToListOfFile {
    public String[] getListOfFiles(String html) {
//        Jsoup is external library that parse html document to Document object
        Document doc = Jsoup.parse(html);
        Vector<String> files = new Vector<>();
        doc.select("a").forEach(link -> files.add(link.attr("href")));

        Pattern pattern = Pattern.compile("^.*\\.(xls|xlsx|doc|docx|pdf|ppt|tex|txt)$");
        return files.stream().filter(pattern.asPredicate()).toArray(String[]::new);
    }
}