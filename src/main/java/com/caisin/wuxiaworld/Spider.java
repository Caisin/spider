package com.caisin.wuxiaworld;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Spider {

    @Test
    public void testJsoup() throws IOException, InterruptedException {
        String fileName = "逆天邪神英文版.txt";
        ThreadPoolExecutor executor = new DownloadNovelThreadFactory(200, 4000, 200, TimeUnit.SECONDS,
                new ArrayBlockingQueue(200));
        String host = "https://www.wuxiaworld.com";
        String url = host + "/novel/against-the-gods";
        Document doc = getDocument(url);
        ArrayList<Integer> chapterNames = new ArrayList<>();
        Elements books = doc.select(".panel .panel-default");
        int threadNum = 0;
        for (Element book : books) {
            Elements select = book.select(".title .collapsed");
            String title = select.html();
            System.out.println("title = " + title);
            Elements chapters = book.select(".chapter-item a");
            for (Element chapter : chapters) {
                String chapterName = chapter.select("span").html();
                System.out.println("chapterName = " + chapterName);
                String href = chapter.attr("href");
                String chapterUrl = host + href;
                System.out.println("chapterUrl = " + chapterUrl);
                DownloadTask downloadTask = new DownloadTask(chapterUrl, threadNum);
                chapterNames.add(threadNum);
                executor.execute(downloadTask);
                threadNum++;
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(1000);
        }
        System.out.println("isTerminated:" + executor.isTerminated());
        File file = new File(fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        for (int chapterName : chapterNames) {
            File content = new File("tmp/" + chapterName);
            try (
                    FileReader reader = new FileReader(content);
                    BufferedReader bufferedReader = new BufferedReader(reader);
            ) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    outputStream.write(line.getBytes());
                    outputStream.write("\r\n".getBytes());
                }
            }
           content.delete();
        }
        boolean tmp = new File("tmp").delete();
        System.out.println("tmp is del？ " + tmp);
        outputStream.close();
    }

    private Connection getConnection(String url) {
        return Jsoup.connect(url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36").timeout(5000);
    }

    private Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                .timeout(10000).get();
    }

    @Test
    public void testGetChapter() throws IOException {
        Connection connection = getConnection("https://www.wuxiaworld.com/novel/renegade-immortal/rge-chapter-1");
        Document document = connection.get();
        System.out.println("document = " + document);
        Elements select = document.select(".fr-view p span");
        System.out.println("select = " + select);
    }


    @Test
    public void testExcel() throws IOException {
        String utf8String = FileUtil.readUtf8String("/Users/caisin/study/code/java/wuxiaworld/猪场信息.json");
        JSONArray maps = JSONUtil.parseArray(utf8String);
        ExcelWriter writer = ExcelUtil.getWriter("/Users/caisin/study/code/java/wuxiaworld/猪场信息.xlsx");
        writer.write(maps);
        writer.flush();
        writer.autoSizeColumnAll();
        writer.close();
    }

    @Test
    public void testyzc() throws IOException {
        List<Element> elements = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Connection connection = getConnection("https://b2b.11467.com/search/-732a573a-pn"+i+".htm");
            Document document = connection.get();
            Elements select = document.select("ul.companylist li h4 a");
            elements.addAll(select);
        }
        ArrayList<Map<String, String>> maps = new ArrayList<>();
        for (Element element : elements) {
            String href = element.attr("href");
            Map<String, String> detail = getDetail("http:"+href);
            maps.add(detail);
        }
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(maps),"/Users/caisin/study/code/java/wuxiaworld/猪场信息.json");
        ExcelWriter writer = ExcelUtil.getWriter("/Users/caisin/study/code/java/wuxiaworld/猪场信息.xlsx");
        writer.write(maps);
        writer.flush();
        writer.close();
    }

    public Map<String,String> getDetail(String url) throws IOException {
        Connection connection = getConnection(url);
        Document document = connection.get();
        Elements select = document.select("#contact .codl");
        Elements dt = document.select("#contact .codl dt");
        Elements dd = document.select("#contact .codl dd");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < dt.size(); i++) {
            map.put(dt.get(i).text(),dd.get(i).text());
        }

        Elements gongshang = document.select("#gongshang .boxcontent tr");
        for (Element element : gongshang) {
            Elements td = element.select("td");
            map.put(td.get(0).text(),td.get(1).text());
        }
        return map;
    }

    @Test
    public void testDelFile() {
        File file = new File("tmp/0");
        boolean delete = file.delete();
        System.out.println("delete = " + delete);
    }
}
