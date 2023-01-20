package day19.workshop.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import day19.workshop.model.LoveResult;
import day19.workshop.service.LoveCalculatorSvc;

@Controller
@RequestMapping(path = "/love")
public class LoveController {

    @Autowired
    private LoveCalculatorSvc loveSvc;

    @PostMapping
    public String getLove(@RequestParam(required = true) String fname,
            @RequestParam(required = true) String sname, Model model)
            throws IOException {
        Optional<LoveResult> loveResult = loveSvc.getLoveResult(fname, sname);
        model.addAttribute("loveResult", loveResult.get());
        loveSvc.save(loveResult.get());
        System.out.println("SAVED");
        return "result";
    }

    @GetMapping(path = "/list")
    public String listLove(Model model) {
        List<LoveResult> allLoveResults = loveSvc.findAll();
        model.addAttribute("allLoveResults", allLoveResults);
        return "list";
    }

    @PostMapping(path = "/list")
    public String clearList(Model model) {
        loveSvc.clear();
        List<LoveResult> allLoveResults = loveSvc.findAll();
        model.addAttribute("allLoveResults", allLoveResults);
        return "list";
    }

}
