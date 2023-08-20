package me.utku.honeynet.controller;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.service.PDFGeneratorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class PDFExportController {

    private final PDFGeneratorService pdfGeneratorService;

    @GetMapping("/report")
    public void generatePDF(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("application/pdf");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
        httpServletResponse.setHeader("Content-Disposition", "inline; filename=report.pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, httpServletResponse.getOutputStream());
        document.open();
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.tableDate(document);
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.reportHeader(document);
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.addHorizontalLine(document);
//        document.add(new com.lowagie.text.Paragraph(""));
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.tableHeader(document, "source");
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.createTable(document);
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.tableHeader(document, "country");
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.createTable(document);
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.tableHeader(document, "category");
        document.add(new com.lowagie.text.Paragraph("\n"));
        pdfGeneratorService.createTable(document);
        document.close();
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=pdf_"+ currentDateTime + ".pdf";
//        httpServletResponse.setHeader(headerKey,headerValue);

//        this.pdfGeneratorService.export(httpServletResponse);
    }





}
