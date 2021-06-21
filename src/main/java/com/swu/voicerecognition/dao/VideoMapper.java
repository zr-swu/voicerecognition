package com.swu.voicerecognition.dao;

import com.swu.voicerecognition.dataobject.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Video record);

    int insertSelective(Video record);

    Video selectByPrimaryKey(Integer id);

    List<Video> selectByKeyWord(@Param("keyword") String keyword);

    List<Video> selectAll();

    int updateByPrimaryKeySelective(Video record);

    int updateByPrimaryKey(Video record);

}