package com.caisin.wuxiaworld;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author caisin
 */
public class DownloadTask implements Runnable {
    private String url;
    private int threadNum;

    public DownloadTask(String url,int  threadNum) {
        this.url = url;
        this.threadNum = threadNum;
    }

    @Override
    public void run() {
        Document content = null;
        try {
            content = getDocument(url);
        } catch (IOException e) {
            System.out.println(url+"下载失败！");
            e.printStackTrace();
        }
        if (content==null) {
            this.run();
            return;
        }
        Elements paragraphs = content.select(".panel-default .fr-view p");
        StringBuilder builder = new StringBuilder();
        for (Element paragraph : paragraphs) {
            String html = paragraph.text();
            builder.append(html).append("\r\n\t");
        }
        File tmp = new File("tmp");
        tmp.mkdir();
        File file = new File("tmp/" + threadNum);
        try(FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(builder.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Document getDocument(String url) throws IOException {
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .timeout(10000).get();
        } catch (IOException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println(url + "连接超时。。。等待1s重试！");
            document = getDocument(url);
        }
        return document;
    }
}
