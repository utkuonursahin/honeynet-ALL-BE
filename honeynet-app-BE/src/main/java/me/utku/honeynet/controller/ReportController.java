package me.utku.honeynet.controller;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    @GetMapping("/show")
    public String showReport(Model model) {
        model.addAttribute("categoryList",reportService.getCategoryandCount());
        model.addAttribute("countryList",reportService.getCountryandCount());
        model.addAttribute("sourceList",reportService.getSourceandCount());
        return "report";
    }

}
