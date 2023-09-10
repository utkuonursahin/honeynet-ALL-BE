package me.utku.honeynet.controller;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.report.ReportFilter;
import me.utku.honeynet.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final SpringTemplateEngine springTemplateEngine;
    @PostMapping
    public String generateDocument(@RequestBody ReportFilter filter) {
        String finalHtml = null;
        Context dataContext = reportService.setData(filter.getCategoryFilter(), filter.getCountryFilter(), filter.getSourceFilter());
        finalHtml = springTemplateEngine.process("report",dataContext);
        reportService.htmlToPdf(finalHtml);
        return "report";
    }
}
