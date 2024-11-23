package com.caoccao.javet.demo.react.ssr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SsrController {
    @GetMapping("/render")
    public String render() {
        try {
            return "Hello SSR!";
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return "Error rendering component: " + e.getMessage();
        }
    }
}
