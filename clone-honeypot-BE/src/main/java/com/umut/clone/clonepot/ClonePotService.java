package com.umut.clone.clonepot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClonePotService {
    private final File indexHtml = new File(System.getProperty("user.dir")+"\\src\\main\\resources\\templates\\"+"index.html");
    private PrintWriter writer = null;

    public String getPage(String url) {
        Document doc = clone(url);
        try{
            Elements linkElements = doc.select("a");
            for (Element linkElement : linkElements) {
                String linkUrl = linkElement.attr("href");
                if(linkUrl.equals("#")) linkElement.attr("href", indexHtml.getName());
                else if (linkUrl.contains("http") || linkUrl.contains("https")) {
                    String uuid = UUID.randomUUID().toString();
                    linkElement.attr("href", uuid + ".html");
                    File page = new File(System.getProperty("user.dir")+"\\src\\main\\resources\\static\\"+ uuid + ".html");
                    write(page,clone(linkUrl).outerHtml());
                }
            }
            write(indexHtml,doc.outerHtml());

        }catch (Exception error) {
            log.error("Clone pot service init error: {}", error.getMessage());
        }
        return doc.outerHtml();
    }

    public void write(File file, String outerHtml){
        try{
            writer = new PrintWriter(file, StandardCharsets.UTF_8);
            writer.write(outerHtml);
            writer.close();
        }catch (Exception error){
            log.error("Clone pot service clone error: {}", error.getMessage());
        }
    }
    public Document clone(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return new Document(null);
        }
    }
}