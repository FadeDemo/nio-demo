package org.fade.demo.niodemo.tomcatnio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fade
 * @date 2022/06/23
 */
@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/hi", method = { RequestMethod.GET })
    public String index() {
        return "{\"hello\":\"world\"}";
    }

}
