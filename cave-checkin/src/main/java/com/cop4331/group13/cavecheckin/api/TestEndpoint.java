package com.cop4331.group13.cavecheckin.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEndpoint {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String testEndpoint() {
        return "Hello World!";
    }
}
