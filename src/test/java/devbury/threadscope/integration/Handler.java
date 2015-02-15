package devbury.threadscope.integration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Handler {

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
