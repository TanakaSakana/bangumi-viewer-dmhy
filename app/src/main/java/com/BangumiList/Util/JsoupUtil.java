package com.BangumiList.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtil {

    public static String grabDmhyRows() {
        final String html = "https://share.dmhy.org/";
        String result = "";
        Document doc;
        try {
            doc = Jsoup.connect(html).get();
            Elements table = doc.getElementsByTag("tbody");
            Elements rows = table.select("tr");

            for (Element row : rows) {
                Elements items = row.select("td");
                result += String.format("Data\t : %s\n", items.eq(0).text());
                result += String.format("Type\t : %s\n", items.eq(1).text());
                result += String.format("Title\t : %s\n", items.eq(2).text());
                result += String.format("Size\t : %s\n", items.eq(4).text());
                result += String.format("Seeds\t : %s\n", items.eq(5).text());
                result += String.format("Peers\t : %s\n", items.eq(6).text());
                result += String.format("Download : %s\n", items.eq(7).text());
                result += String.format("Host\t : %s\n", items.eq(8).text());
                result += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
