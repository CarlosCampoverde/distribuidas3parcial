package com.entregaexpress.logiflow.graphqlservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphiQLController {
    
    @GetMapping("/")
    public String redirectToGraphiQL() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/graphiql")
    public String graphiql() {
        return "redirect:/index.html";
    }
}
