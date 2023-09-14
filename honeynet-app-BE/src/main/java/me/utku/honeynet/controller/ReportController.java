package me.utku.honeynet.controller;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.report.ReportFilter;
import me.utku.honeynet.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final SpringTemplateEngine springTemplateEngine;
    @PostMapping
    public ResponseEntity<byte[]> generateDocument(@RequestBody ReportFilter filter) {
        String finalHtml = null;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf");
        Context dataContext = reportService.setData(filter.getCategoryFilter(), filter.getCountryFilter(), filter.getSourceFilter());
        finalHtml = springTemplateEngine.process("report",dataContext);
        byte[] pdfBytes = reportService.htmlToPdf(finalHtml);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(pdfBytes);
    }

}
