package com.umut.clone.page;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @GetMapping(value = {"/","/static/**","/templates/**"})
    public String home(HttpServletRequest httpServletRequest){
        pageService.handlePageLoad(httpServletRequest.getRemoteAddr(),"/");
        return "index";
    }

    @GetMapping("/{url}")
    public String redirect(@PathVariable String url, HttpServletRequest httpServletRequest){
        pageService.handlePageLoad(httpServletRequest.getRemoteAddr(),url);
        return pageService.returnPage(url);
    }
}
