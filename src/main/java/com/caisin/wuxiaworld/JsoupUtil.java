package com.caisin.wuxiaworld;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupUtil {
    public static Connection getConnection(String url) {
        return Jsoup.connect(url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36").timeout(5000);
    }

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                .timeout(10000).get();
    }

}
