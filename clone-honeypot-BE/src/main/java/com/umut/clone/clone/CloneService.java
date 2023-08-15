package com.umut.clone.clone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloneService {
    private static final String WORKING_DIRECTORY = "C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\clone-honeypot-BE\\";
    private static final String TEMPLATES_PATH = "src\\main\\resources\\templates\\";
    private static final String FILE_TYPE = ".html";
    private final File indexHtml = new File(WORKING_DIRECTORY+TEMPLATES_PATH+"index"+FILE_TYPE);
    private final HashMap<String,String> routesMap = new HashMap<>();
    private final Pattern pattern = Pattern.compile("https:\\/\\/[^\\/]+\\/([^\\/]+)\\/");

    public void init() {
        clearDirectories();
    }

    public CloneResponse completeClone(String url){
        CloneResponse cloneResponse = new CloneResponse("","",false);
        try{
            Document mainDocument = clone(url);
            Elements linkElements = mainDocument.select("a");
            replaceLinks(linkElements);
            write(indexHtml,mainDocument.outerHtml());
            pairPageLinks();
            cloneResponse = new CloneResponse(url,"Clone completed successfully.",true);
        }catch (Exception error) {
            log.error("Clone pot service completeClone error: {}", error.getMessage());
            cloneResponse = new CloneResponse(url,"Clone failed.",false);
        }
        return cloneResponse;
    }

    public void replaceLinks(Elements linkElements){
        Matcher matcher;
        String route;
        for (Element linkElement : linkElements) {
            String exactUrl = linkElement.attr("href");
            matcher = pattern.matcher(exactUrl);
            if(matcher.find() && (exactUrl.contains("http") || exactUrl.contains("https"))){
                route = matcher.group(1);
                linkElement.attr("href", route + FILE_TYPE);
                File htmlFile = new File(WORKING_DIRECTORY+TEMPLATES_PATH+ route + FILE_TYPE);
                write(htmlFile,clone(exactUrl).outerHtml());
                routesMap.put(exactUrl,route);
            } else if(!exactUrl.contains("#")){
                //If it is not matched by regex, and it is not a hash link,then it is a link to the direct domain.
                linkElement.attr("href",indexHtml.getName());
                routesMap.put(exactUrl,"index");
            }
        }
    }

    public void pairPageLinks(){
        File templateFolder = new File(WORKING_DIRECTORY+TEMPLATES_PATH);
        File[] files = templateFolder.listFiles();
        Document document;
        try{
            if(files == null) throw new Exception("Files is null");
            for(File file : files){
                if(!file.isFile()) throw new Exception("File is not a file");
                document = Jsoup.parse(file, "UTF-8");
                Elements linkElements = document.select("a");
                for (Element linkElement : linkElements) {
                    String exactUrl = linkElement.attr("href");
                    if(routesMap.containsKey(exactUrl)){
                        linkElement.attr("href", routesMap.get(exactUrl) + FILE_TYPE);
                    }
                    write(file,document.outerHtml());
                }
            }
        }catch (Exception error){
            log.error("Clone pot service pairPageLinks error: {}", error.getMessage());
        }
    }

    public void clearDirectories(){
        try{
            File directory = new File(WORKING_DIRECTORY+"\\src\\main\\resources\\static\\");
            FileUtils.cleanDirectory(directory);
            directory = new File(WORKING_DIRECTORY+TEMPLATES_PATH);
            FileUtils.cleanDirectory(directory);
        } catch (Exception error){
            log.error("Clone pot service clearDirectories error: {}", error.getMessage());
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

    public void write(File file, String outerHtml){
        try{
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
            writer.write(outerHtml);
            writer.close();
        }catch (Exception error){
            log.error("Clone pot service clone error: {}", error.getMessage());
        }
    }
}