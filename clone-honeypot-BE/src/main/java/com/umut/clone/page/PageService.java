package com.umut.clone.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class PageService {
    public String returnPage(String url){
        File html = new File(System.getProperty("user.dir")+"\\src\\main\\resources\\static\\"+url);
        return html.toString();
    }
}
