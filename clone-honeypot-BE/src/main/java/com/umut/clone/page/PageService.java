package com.umut.clone.page;

import com.umut.clone.clone.CloneReach;
import com.umut.clone.restservice.RestService;
import com.umut.clone.suspiciousactivity.Origin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PageService {
    private final RestService restService;

    public String returnPage(String url){
        return url.split("\\.")[0];
    }

    public void handlePageLoad(String ip, String url){
        Origin origin = restService.getOriginDetails(ip);
        CloneReach cloneReach = new CloneReach(url, origin);
        restService.postSuspiciousActivity(cloneReach);
    }
}
