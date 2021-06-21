### 1.项目背景

依据使用文字对视频内容进行搜索的需求，开发出一个能对视频内容进行搜索、定位播放的软件。

### 2.需求分析

首先系统应向管理员提供上传视频的功能，其次系统要对上传的视频进行存储，提取音频，字幕提取等一系列操作。为用户提供一个网页界面展示视频内容，网页内搜索关键字的提交表单，用户通过关键字可以搜索到有相关内容的视频并可以从该内容出现的位置开始播放。

### 3.系统设计

#### 3.1 开发环境

开发工具：Intellj IDEA 2020.3.2
编程语言：Java（jdk1.8）

#### 3.2 总体设计

![](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/总体方案.png)

##### 3.2.1 用户接口设计

项目采用前后端分离架构，前端基于element-ui进行UI设计，使用H5的“video”标签对视频进行展示，通过vue访问后端接口获取数据。

##### 3.2.2 服务接口

基于Springboot web框架搭建一个服务后台，为前端提供Restful风格的crud接口，实现视频的上传存储，删除，修改以关键词及查询功能。

##### 3.2.3 音频提取模块

后台将上传的视频(.mp4)进行音轨分离的操作，得到音频文件(.mp3)


##### 3.2.4 音频识别模块

调用该模块，将音频提取模块得到的音频文件(.mp3)转换为字幕(文本)

##### 3.2.5 数据存储

将视频内容存储在硬盘当中，使用关系型数据库对视频的属性以及提取完的字幕进行存储，并使用字幕建立倒排索引。

![](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/存储示意图.png)

##### 3.2.6 关键词搜索

鉴于字幕是非结构化的数据，无法使用Sql语句高效的进行查询，项目中使用**全文检索**技术，首先将字幕中的词提取出来，组成索引，通过查询索引达到字幕搜索的目的。

![全文检索](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/全文检索.png)



### 4. 关键技术实现

#### 4.1 依赖管理

为了方便依赖的管理以及项目的打包等工作，使用maven进行依赖管理。

Maven版本：apache-maven-3.2.2

依赖详情见：

http://localhost:8080/site/dependencies.html

#### 4.2 音轨提取

ffmpeg 是强大的媒体文件转换工具，常用于转码，可选命令非常多，编码器、视频时长、帧率、分辨率、像素格式、采样格式、码率、裁剪选项、声道数等等都可以自由选择。

本项目中使用ffmpeg的音轨提取功能

版本：N-101658-g75fd3e1519

- FFmpeg包官方下载地址：http://ffmpeg.org/download.html

- 分离指令：

  ```
  ffmpeg.exe -i videoPath -vn -ar 44100 -ac 2 -ab 192 -f mp3 voicePath
  ```



#### 4.3 字幕提取

音频识别

使用科大讯飞提供的语音听写SDK 

版本：lfasr-sdk-3.0.1

​	官网注册https://www.xfyun.cn/services/voicedictation

​	创建项目下载SDK![image-20210324141107009](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/sdk.png)



将SDK加入项目依赖 复制官方示例DEMO代码 填入申请获得的APP_ID和SECRET_KEY即可进行语音的识别。

![](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/appid.png)



音频提取主要代码：

```java
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
```



#### 4.4 web框架搭建

SpringBoot版本: 2.3.7.RELEASE

Mybatis版本：2.0.1

本项目基于SpringBoot web框架 按照MVC设计模式搭建后端，后端项目结构树形图如下：



数据持久层使用MyBatis操作Mysql数据库。

#### 4.5 字幕搜索

##### 4.5.1 倒排索引

我们传统的索引叫做正向索引: 当用户发起查询时（假设查询为一个关键词），搜索引擎会扫描索引库中的所有文档，找出所有包含关键词的文档，这样依次从文档中去查找是否含有关键词的方法叫做正向索引。互联网上存在的网页（或称文档）不计其数，这样遍历的索引结构效率低下，无法满足用户需求。

正向索引结构如下:

文档1的ID→单词1的信息；单词2的信息；单词3的信息…

文档2的ID→单词3的信息；单词2的信息；单词4的信息…

…

![image-20210620223938387](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/2.png)



为了增加效率，搜索引擎会把正向索引变为反向索引（倒排索引）即把“文档→单词”的形式变为“单词→文档”的形式。倒排索引具体机构如下:

单词1→文档1的ID；文档2的ID；文档3的ID…

单词2→文档1的ID；文档4的ID；文档7的ID…

![image-20210620224000212](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/3.png)

##### 4.5.2 Lucene

Lucene是apache下的开放源代码的全文检索引擎工具包，可以实现全文检索功能，本项目基于Lucene实现对字幕的搜索功能。在上传视频的时候进行音轨及字幕的提取并使用字幕建立索引库，用户通过关键词搜索时，后端创建查询对象 在索引库中进行查询。

![](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/Lucene.png)

##### 4.5.2 导入相关的jar包

```java
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-core</artifactId>
    <version>4.10.2</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queries</artifactId>
    <version>4.10.2</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-test-framework</artifactId>
    <version>4.10.2</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-analyzers-common</artifactId>
    <version>4.10.2</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queryparser</artifactId>
    <version>4.10.2</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-highlighter</artifactId>
    <version>4.10.2</version>
</dependency> 
```

##### 4.5.3 lucene工作流程

![](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/lucene流程.png)

##### 4.5.4 Lucene集成IKAnalyzer 中文分词器

Analyzer: 分词器:

用于对文档中的数据进行分词, 其分词的效果取决于分词器的选择, Lucene中根据各个国家制定了各种语言的分词器,对中文有一个ChineseAnalyzer 但是其分词的效果, 是将中文进行一个一个字的分开

针对中文分词一般只能使用第三方的分词词,比如IKAnalyzer

首先要引入jar包:

```java
<!-- 引入IK分词器 -->
<dependency>
    <groupId>com.janeluo</groupId>
    <artifactId>ikanalyzer</artifactId>
    <version>2012_u6</version>
</dependency>
```

ik分词器在2012年更新后, 就在没有更新, 其原因就取决于其强大的扩展功能,以保证ik能够持续使用

ik支持对自定义词库, 其可以定义两个扩展的词典

1) 扩展词典（新创建词功能）:有些词IK分词器不识别 例如：“龙江花园”，“红星美凯龙”

2) 停用词典（停用某些词功能）有些词不需要建立索引  例如：“哦”，“啊”，“的”



![](https://markdown-zr.oss-cn-chengdu.aliyuncs.com/typora/voicerecognition/documentImages/1.png)

在resources内创建者三个文件，在ext.dic中设置需要进行分词的内容即可, 在stopword中设置不被分词的内容即可,xml文件内容格式如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">  
<properties>  
    <comment>IK Analyzer 扩展配置</comment>
    <!--用户可以在这里配置自己的扩展字典 -->
    <entry key="ext_dict">local.dic;</entry> 


    <!--用户可以在这里配置自己的扩展停止词字典-->
    <entry key="ext_stopwords">stopword.dic;</entry> 

</properties>
```

停用词典和扩展词典的格式：

```
的
好
了
啊
呢
```

```
红星美凯龙
巴滨路
龙江花园
```

后期考虑将房地产网站爬虫的数据处理后导入扩展词典，从而提升搜索的准确性。

##### 4.5.4 lucene配置类

创建一个配置类，将lucene搜索中用到的indexWriter Analyzer等实例交给Spring管理 在需要的时候直接注入。

```java
/**
 * @author zr
 * @create 2021-04-02-10:07
 */

@Configuration
public class LuceneConfig {
    /**
     * lucene索引,存放位置
     */
    private static final String LUCENEINDEXPATH = "lucene/indexDir/";

    /**
     * 创建一个 Analyzer 实例
     *
     * @return
     */
    @Bean
    public Analyzer analyzer() {
        return new IKAnalyzer();
    }

    /**
     * 索引位置
     *
     * @return
     * @throws IOException
     */
    @Bean
    public Directory directory() throws IOException {

        Path path = Paths.get(LUCENEINDEXPATH);
        File file = path.toFile();

        if (!file.exists()) {
            //如果文件夹不存在,则创建
            file.mkdirs();
        }
        return FSDirectory.open(file);
    }

    /**
     * 创建indexWriter
     *
     * @param directory
     * @param analyzer
     * @return
     * @throws IOException
     */
    @Bean
    public IndexWriter indexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,analyzer);
//        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        // 清空索引
//        indexWriter.deleteAll();
//        indexWriter.commit();
        return indexWriter;
    }

    /**
     * SearcherManager管理
     *
     * @param directory
     * @return
     * @throws IOException
     */
    @Bean
    public SearcherManager searcherManager(Directory directory, IndexWriter indexWriter) throws IOException {
        SearcherManager searcherManager = new SearcherManager(indexWriter, false, new SearcherFactory());
        ControlledRealTimeReopenThread cRTReopenThead = new ControlledRealTimeReopenThread(new TrackingIndexWriter(indexWriter), searcherManager,
                5.0, 0.025);
        cRTReopenThead.setDaemon(true);
        //线程名称
        cRTReopenThead.setName("更新IndexReader线程");
        // 开启线程
        cRTReopenThead.start();
        return searcherManager;
    }
}
```



#### 4.6 swagger整合

##### 4.6.1 swagger2

Swagger2可以快速帮助我们编写最新的API接口文档.

常用注解

swagger通过注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息的等等。

 @Api：修饰整个类，描述Controller的作用

 @ApiOperation：描述一个类的一个方法，或者说一个接口

 @ApiParam：单个参数描述

 @ApiModel：用对象来接收参数

 @ApiModelProperty：用对象接收参数时，描述对象的一个字段

 @ApiImplicitParam：一个请求参数

@ApiImplicitParams：多个请求参数

##### 4.6.2 swagger2集成

引入依赖：

```java
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

添加swagger2配置类:

```java
/**
 * Swagger2配置信息
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.swu.voicerecognition.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Simple APIs")
                .description("simple apis")
                .termsOfServiceUrl("http://www.gm.com")
                .contact(new Contact("Ran", "http://81.69.3.91", "zhuran_swu@foxmail.com"))
                .version("1.0")
                .build();
    }
}
```



后端接口展示：[接口文档](http://videosearchback.zhurancloud.cn/swagger-ui.html)



### 5. 项目地址

后端项目地址：[后端](https://github.com/zr-swu/voicerecognition)

前端项目地址：[前端](https://github.com/zr-swu/voicerecognitionFront)

演示地址：[演示](http://videosearch.zhurancloud.cn/)