package com.umut.clone.page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PageService {
    public String returnPage(String url){
        return url.split("\\.")[0];
    }
}
