package me.utku.honeynet.service;


import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class PDFGeneratorService {



    private final SuspiciousActivityService suspiciousActivityService;

    public void createTable(Document document) throws DocumentException{
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table);
            addTableRow(table);
            document.add(table);
        }

    private void addTableHeader(PdfPTable table){
        table.addCell("Column 1");
        table.addCell("Column 2");
        table.addCell("Column 3");
        table.addCell("Column 4");
        table.addCell("Column 5");
    }

    private void addTableRow(PdfPTable table){
        for (int i = 0; i<2; i++){
            table.addCell("Row "+(i+1) + ", Col 1");
            table.addCell("Row "+(i+1) + ", Col 2");
            table.addCell("Row "+(i+1) + ", Col 3");
            table.addCell("Row "+(i+1) + ", Col 4");
            table.addCell("Row "+(i+1) + ", Col 5");
        }
    }
    public void addHorizontalLine(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new Color(0, 0, 0, 100)); // Customize line color (RGBA)
        document.add(lineSeparator);
    }
    public void reportHeader(Document document) throws DocumentException{
        Font headerFont = new Font(Font.HELVETICA, 26);
        com.lowagie.text.Paragraph reportHeaderContent = new com.lowagie.text.Paragraph("Suspicious Activity Report",headerFont);
        reportHeaderContent.setAlignment(Element.ALIGN_LEFT);
        document.add(reportHeaderContent);
    }
    public void tableHeader(Document document, String tableType){
        Font tableHeaderFont = new Font(Font.HELVETICA,15);
        String getHeader = tableType.substring(0,1).toUpperCase()+tableType.substring(1);
        com.lowagie.text.Paragraph tableHeaderContent = new com.lowagie.text.Paragraph("Last Duration " + getHeader + " Statistics",tableHeaderFont);
        tableHeaderContent.setAlignment(Element.ALIGN_LEFT);
        document.add(tableHeaderContent);
    }
    public void tableDate(Document document){
        Font tableDateFont = new Font(Font.HELVETICA,11);
        com.lowagie.text.Paragraph tableDateContent = new com.lowagie.text.Paragraph("DD/MM/YYYY",tableDateFont);
        tableDateContent.setAlignment(Element.ALIGN_RIGHT);
        document.add(tableDateContent);
    }

//    private void
}
