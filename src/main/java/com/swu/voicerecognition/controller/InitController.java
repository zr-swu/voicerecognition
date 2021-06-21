package com.swu.voicerecognition.controller;

import com.swu.voicerecognition.config.PathConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author zr
 * @create 2021-04-08-13:54
 */
@Controller
@Api(tags = "前端项目初始化")
@RequestMapping(value = "/init")
public class InitController {

    @ApiOperation(value = "获取初始化参数")
    @ResponseBody
    @RequestMapping(value = "/getParam",method = RequestMethod.GET)
    public HashMap<String,String> getInitParam(){

        HashMap<String, String> map = new HashMap<>();
        map.put("curVersion", "v1");
//        map.put("resourcePath", PathConfig.resourcePath);

        return map;
    }
}
