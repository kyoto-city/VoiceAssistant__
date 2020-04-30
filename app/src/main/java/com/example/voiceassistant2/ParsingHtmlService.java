package com.example.voiceassistant2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2020";

    public static String getHoliday(String date) throws IOException {
        Document document = Jsoup.connect(URL).get();
        String result = "Не удалось найти праздник в этот день";
        String cssQuery = "div.month_cel_date";
        Elements elements = document.select(cssQuery);
        for (Element el : elements) {
            if (el.select(">span").text().equals(date)) {
                Elements search = el.siblingElements();
                String search_result = "";
                for (Element elem : search.select("li")) {
                    search_result += elem.text() + "; ";
                }
                result = search_result;
                break;
            }
        }
        return result;
    }
}