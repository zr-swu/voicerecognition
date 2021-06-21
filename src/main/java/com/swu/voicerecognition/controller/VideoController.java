package com.swu.voicerecognition.controller;

import com.swu.voicerecognition.service.CaptionService;
import com.swu.voicerecognition.service.VideoService;
import com.swu.voicerecognition.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author zr
 * @create 2021-03-22-15:05
 */

@Api(tags = "视频操作")
@Controller
@RequestMapping("/v1/video")
public class VideoController {

    @Autowired
    private CaptionService captionService;


    @Autowired
    private VideoService videoService;


    @ApiOperation(value = "根据关键词搜索视频并定位关键词")
    @RequestMapping(value = "/jump",method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> jumpToSentence(@RequestParam(name = "keyword") String keyword,
                                                  @RequestParam(name = "page",required = false)Integer page,
                                                  @RequestParam(name = "size",required = false)Integer size) throws Exception {

        page = page==null?1:page.intValue();
        size = size==null?6:size.intValue();

        HashMap<String, Object> map = captionService.jumpToSentence(keyword, page, size);

        return map;
    }

    @ApiOperation(value = "获取所有视频")
    @RequestMapping(value = "/getVideo",method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getAllVideo(@RequestParam(name = "page",required = false)Integer page,
                                               @RequestParam(name = "size",required = false)Integer size){

        page = page==null?1:page.intValue();
        size = size==null?6:size.intValue();

        HashMap<String, Object> map = videoService.getVideo(page, size);
        return map;
    }

    
    @ApiOperation(value = "上传视频")
    @RequestMapping(value="/uploadVideo",produces="application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public Result uploadFile(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {

        System.out.println("上传文件===");
        videoService.uploadFile(file);
        return Result.ok();
    }

}
