package com.swu.voicerecognition.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.msp.lfasr.LfasrClient;
import com.iflytek.msp.lfasr.model.Message;
import com.swu.voicerecognition.config.PathConfig;
import com.swu.voicerecognition.controller.viewobject.videoVO;
import com.swu.voicerecognition.dataobject.Video;
import com.swu.voicerecognition.entity.Sentence;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 跳转逻辑的后端接口
 * @author : zr
 */

@Service
public class CaptionService {

    @Autowired
    private VideoService videoService;

    private static final String APP_ID = "605807c0";
    private static final String SECRET_KEY = "665f37416494bf99a2b2d0456eac3703";

    //音频文件路径
    //1、绝对路径：D:\......\demo-3.0\src\main\resources\audio\lfasr.wav
    //2、相对路径：./resources/audio/lfasr.wav
    //3、通过classpath：
    private static final String MOVIE_FILE_PATH = CaptionService.class.getResource("/").getPath()+"/audio/test2.mp4";
//    private static final String AUDIO_FILE_PATH = LfasrSDKDemo.class.getResource("/").getPath()+"/audio/test2.mp3";
    private static final String AUDIO_FILE_PATH = "D:\\fileUpload\\mp3\\testUpload.mp3";

    /**
     * 跳转到视频中出现关键词的位置
     * @return
     */
    public HashMap<String, Object> jumpToSentence(String keyword, int page, int size) throws Exception {

        HashMap<String,Object> result = videoService.findVideoByKeyword(keyword,page,size);
        List<Video> videos = (List<Video>)result.get("videos");
        List<videoVO> videoVOS = new ArrayList<>();
        for(Video v :videos){
            Sentence targetSentence = getTargetSentence(v.getSubtitles(), keyword);
            if(targetSentence!=null){
                videoVO videoVO = transferFromvideo(v,targetSentence);
                videoVOS.add(videoVO);
            }
        }

        System.out.println("实际返回的List：");
        videoVOS.stream().forEach(System.out::println);

        HashMap<String, Object> map = new HashMap<>();
        map.put("videos", videoVOS);
        map.put("total", result.get("total"));

        return map;

    }


    public Sentence getTargetSentence(String subtitle,String keyword){

        JSONArray  jsonArray = JSONObject.parseArray(subtitle.replaceAll("\\\\", ""));


        ArrayList<Sentence> list = new ArrayList<>();

        for (Object a : jsonArray){
            JSONObject x = (JSONObject) JSON.toJSON(a);
            Sentence sentence = new Sentence(Integer.parseInt(x.get("bg").toString()) , Integer.parseInt(x.get("ed").toString()),  x.get("onebest").toString());
            list.add(sentence);
        }

        Sentence targetSentence = null;

        Boolean isFind = false;
        for(Sentence s : list){
            if(s.getContent().contains(keyword)){
                targetSentence = s;
                isFind = true;
                break;
            }
        }

        return isFind?targetSentence:null;
    }


    public videoVO transferFromvideo(Video video , Sentence sentence){
        videoVO videoVO = new videoVO();
        if(video!=null)
            BeanUtils.copyProperties(video, videoVO);

        if(sentence!=null)
            BeanUtils.copyProperties(sentence, videoVO);
        return videoVO;
    }

    public static void transferVoiceFromMovie(String videoRealPath,String voiceRealPath) throws IOException {

        List<String> command = buildCommand(videoRealPath, voiceRealPath);

        System.out.println("音频path："+voiceRealPath);
        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder();

        builder.command(command);

        builder.redirectErrorStream(true);


        System.out.println("视频语音分离开始...");

        Process process = null;
        try {
            process = builder.start();
            process.waitFor();//等待进程执行结束process.waitFor();//等待进程执行结束
            System.out.println("视频语音分离完成...");
        } catch (IOException e) {
            e.printStackTrace();

            System.out.println("视频语音分离失败！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static List<String> buildCommand(String videoRealPath , String voiceRealPath){
        List<String> command =new ArrayList<String>();

        command.add("ffmpeg");
        command.add("-i");
        command.add(videoRealPath);
//        command.add("-vn");
//        command.add("-ar");
//        command.add("44100");
//        command.add("-ac");
//        command.add("2");
//        command.add("-ab");
//        command.add("192");
//        command.add("-f");
//        command.add("mp3");
        command.add(voiceRealPath);

        return command;
    }

    /**
     * 注意：同时只能执行一个示例
     *
     * @param
     * @throws InterruptedException e
     */

    public static String transferVoiceToText(String voiceRealPath) throws InterruptedException {
        String standard = standard(voiceRealPath);

        return standard;
    }



    public String  audioExtractionAndSubtitleParsing(String videoPath) throws InterruptedException, IOException {

        String path  = videoPath.substring(videoPath.lastIndexOf("/") + 1);
        String name = path.substring(0,path.lastIndexOf("."));

        System.out.println("videoPath"+videoPath);
        String voiceRealPath = PathConfig.resourcePath + File.separator + "mp3" + File.separator +name+".mp3";
        transferVoiceFromMovie(videoPath,voiceRealPath);
        String resultString = transferVoiceToText(voiceRealPath);

        return resultString;
    }





    /**
     * 简单 demo 样例
     *
     * @throws InterruptedException e
     */
    private static String standard(String voiceRealPath) throws InterruptedException {
        //1、创建客户端实例
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID, SECRET_KEY);

        //2、上传
        Message task = lfasrClient.upload(voiceRealPath);
        String taskId = task.getData();
        System.out.println("转写任务 taskId：" + taskId);

        //3、查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(2);
        }
        //4、获取结果
        Message result = lfasrClient.getResult(taskId);
        System.out.println("转写结果: \n" + result.getData());

        return result.getData();
    }



}
