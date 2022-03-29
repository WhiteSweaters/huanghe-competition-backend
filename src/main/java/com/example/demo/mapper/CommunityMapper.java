package com.example.demo.mapper;

import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Community;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommunityMapper {

    @Insert("insert into tb_community (content,uid,currentTime,tag,lat,lon,street,uuid) values(#{content},#{uid},#{currentTime},#{tag},#{lat},#{lon},#{street},#{uuid})")
    void uploadIdea(Community community);

    @Select("select id from tb_community where uuid = #{uuid}")
    Long getCommunityId(@Param("uuid") String uuid);

    @Insert("insert into tb_community_images (image,cid) values(#{image},#{cid})")
    void storageImg(@Param(value = "image") String hashName, Long cid);

    List<Community> getCommunityInfo();

    Integer getTotalCount();

    List<Community> getPagination(Integer begin, Integer end, String tag);

    List<String> getImageList(Community community);

    Integer selectCountByUid(Long uid);

    List<Community> getListByUid(Integer begin, Integer end, Long uid);

    void uploadComment(String content, Long cid, Long uid);

    Integer getCommentNumByCid(Long cid);


    List<Comment> getCommentListByCid(Long cid);

    List<Community> getPagination2(Integer begin, Integer end);

    @Select("select count(*) from tb_attention where care=#{uid}")
    Integer getCaredOfNum(Long uid);
}
