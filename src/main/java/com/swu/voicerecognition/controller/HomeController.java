package com.swu.voicerecognition.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author zr
 * @create 2021-03-24-9:08
 */
@Controller
@Api(tags = "跳转主页面")
public class HomeController {

    @RequestMapping(value = "/",method = RequestMethod.GET) //若是@RequestMapping("/home")，则浏览器访问http://localhost:8080/home路径
    public String hello() {
        return "index";
    }

}