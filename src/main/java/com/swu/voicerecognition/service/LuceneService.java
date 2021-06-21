package com.swu.voicerecognition.service;

import com.swu.voicerecognition.dataobject.Video;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zr
 * @create 2021-04-02-10:52
 */
@Service
public class LuceneService {

    @Autowired
    private IndexWriter indexWriter;

    @Autowired
    private Analyzer analyzer;

    @Autowired
    private SearcherManager searcherManager;

    /**
     * 创建索引
     * @param videoList
     * @throws IOException
     */
    public void createVideoIndex(List<Video> videoList) throws IOException {
        List<Document> docs = new ArrayList<Document>();
        for (Video v : videoList) {
            Document doc = new Document();
            doc.add(new StringField("id", v.getId() + "", Field.Store.YES));
            /**
             * TextField即创建索引，又会被分词。StringField会创建索引，但是不会被分词
             */
            doc.add(new TextField("subtitles", v.getSubtitles(), Field.Store.YES));
            doc.add(new TextField("title", v.getTitle(),Field.Store.YES));
            docs.add(doc);
        }
        indexWriter.addDocuments(docs);
        // indexWriter.updateDocument(docs);
        indexWriter.commit();
        //  indexWriter.close();
    }

    /**
     * 新上传一个视频时 向索引文件当中追加该视频的
     * @param video
     * @throws IOException
     */
    public void appendVideoIndex(Video video) throws  IOException{
        List<Video> videos = new ArrayList<>();
        videos.add(video);
        this.createVideoIndex(videos);
    }

    public HashMap<String, Object> searchVideo(String keyWord, int page, int size) throws Exception {
        searcherManager.maybeRefresh();
        IndexSearcher indexSearcher = searcherManager.acquire();

        // 模糊匹配,匹配词

        QueryParser queryParser = null ;
        Query query = null;

        if (keyWord != null ) {
            // 输入空格,不进行模糊查询
            if (!"".equals(keyWord.replaceAll(" ", ""))) {
                queryParser = new QueryParser("subtitles", analyzer);
                query = queryParser.parse("subtitles:" + keyWord);
            }
        }

        System.out.println(query.toString());

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] hits = topDocs.scoreDocs;

        int start = (page - 1) * size;
        int end = page * size;
        if (end > hits.length) {
            end = hits.length;
        }

        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Video> result = new ArrayList<>();

        for (int i = start; i < end; i++) {

            Document doc = indexSearcher.doc(hits[i].doc);

            System.out.println(hits[i].score);

            /*if(hits[i].score > 2){*/
            Video video = new Video();
            video.setId(Integer.parseInt(doc.get("id")));
            video.setTitle(doc.get("title"));
            video.setSubtitles(doc.get("subtitles"));
            result.add(video);

           /* }*/
        }

        map.put("videos", result);
        map.put("total", hits.length);//总记录数

        return map;
    }


}
