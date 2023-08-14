package com.umut.clone.clonepot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
public class ClonePotService {


    public String cloneHtmlPage(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements linkElements = document.select("a");

            File logFile = new File("log_" + UUID.randomUUID() + ".html");
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(document.outerHtml());
            writer.newLine();
            writer.close();

            for (Element linkElement : linkElements) {
                String linkUrl = linkElement.attr("href"); // Get the link URL
                if (!linkUrl.isEmpty()) {
                    cloneLinkedPage(linkUrl, logFile);
                }
            }
            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error cloning HTML page: " + e.getMessage();
        }
    }
    private void cloneLinkedPage(String linkUrl, File targetFile) throws IOException {
        Document linkedDocument = Jsoup.connect(linkUrl).get();
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, true));
        writer.write(linkedDocument.outerHtml());
        writer.newLine();
        writer.close();
    }
}

