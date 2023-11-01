package me.star.tools.shufa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Star Chou
 * @description /
 * @create 2022/2/28
 */
@Controller
public class IndexController {
    @RequestMapping(value = "/template")
    public String redirectToTemp() {
        return "template";
    }
}
