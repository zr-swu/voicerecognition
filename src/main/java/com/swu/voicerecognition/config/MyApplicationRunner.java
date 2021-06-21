package com.swu.voicerecognition.config;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.swu.voicerecognition.dao.VideoMapper;
import com.swu.voicerecognition.dataobject.Video;
import com.swu.voicerecognition.service.LuceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zr
 * @create 2021-04-02-11:32
 */
@Component
@Order(value = 1)
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private LuceneService luceneService;

    @Autowired
    private VideoMapper videoMapper;

//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Video> videos = videoMapper.selectAll();
        luceneService.createVideoIndex(videos);
    }
}
