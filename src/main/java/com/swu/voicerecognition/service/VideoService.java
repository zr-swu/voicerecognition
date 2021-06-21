package com.swu.voicerecognition.service;

import com.swu.voicerecognition.config.PathConfig;
import com.swu.voicerecognition.dao.VideoMapper;
import com.swu.voicerecognition.dataobject.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zr
 * @create 2021-03-23-22:02
 */
@Service
public class VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private LuceneService luceneService;

    @Autowired
    private CaptionService captionService;


    public HashMap<String, Object> findVideoByKeyword(String keyword, int page, int size) throws Exception {

        HashMap<String,Object> result = luceneService.searchVideo(keyword,page,size);

        System.out.println("Lucene取回的list：");
        List<Video> list = (List<Video>) result.get("videos");
        list.stream().forEach(System.out::println);

        return result ;
    }

    @Transactional
    public String uploadFile(MultipartFile file) throws IOException, InterruptedException {
        //判断文件是否为空
        if (file.isEmpty()) {
            return "上传文件不可为空";
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();
        //System.out.println(fileName);
//        String[] split = fileName.split("\\.");

//        String title = split[0];

//        String fileName = file.getName();
        System.out.println(fileName);
        System.out.println("lastIndexOf(\".\")"+fileName.lastIndexOf("."));
        String title = fileName.substring(0,fileName.lastIndexOf("."));
        Video video = new Video();
        video.setTitle(title);

        videoMapper.insertSelective(video);//插入标题  id会自增


        String path = PathConfig.resourcePath + File.separator + "mp4" + File.separator + video.getId() + ".mp4";

        //文件绝对路径
        System.out.print("保存文件绝对路径"+path+"\n");

        //创建文件路径
        File dest = new File(path);

        //判断文件是否已经存在
        if (dest.exists()) {
            return "文件已经存在";
        }

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }

        try {
            //上传文件
            file.transferTo(dest); //保存文件
            System.out.print("保存文件路径"+path+"\n");

        } catch (IOException e) {
            return "上传失败";
        }

        String subTitles = captionService.audioExtractionAndSubtitleParsing(path);
        video.setSubtitles(subTitles);

//        new Thread(()-> {
//            try {
        luceneService.appendVideoIndex(video);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();

        videoMapper.updateByPrimaryKey(video);

        return "上传成功,文件url=="+path;
    }


    public HashMap<String, Object> getVideo(Integer page, Integer size) {

        List<Video> videos = videoMapper.selectAll();

        int start = (page - 1) * size;
        int end = page * size;
        if (end > videos.size()) {
            end = videos.size();
        }


        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Video> pageVideos = new ArrayList<>();
        for (int i = start; i < end; i++) {
            pageVideos.add(videos.get(i));
        }
        map.put("videos", pageVideos);
        map.put("total", videos.size());
        return map;
    }


}
