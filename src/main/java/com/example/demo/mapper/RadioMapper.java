package com.example.demo.mapper;

import com.example.demo.pojo.Radio;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RadioMapper {

    /**
     * 将前端传递过来的uid title copy radio四个字段存入数据库中
     *
     * @param radio
     */
    @Insert("insert into tb_radio(uid,title,copy,radio,cover,currentTime) values(#{uid},#{title},#{copy},#{radio},#{cover},#{currentTime}) ")
    void addRadio(Radio radio);

    /**
     * 查询radio表中的数据总条数
     *
     * @return
     */
    @Select("select count(*) from tb_radio")
    Integer getTotalCount();

    /**
     * 通过开始位置索引和结束位置索引得出对应的Radio集合
     *
     * @param begin
     * @param end
     * @return
     */
    @Select("select r.id,r.title,r.copy,r.hearts,r.currentTime,r.radio,u.nickname,r.uid,r.cover,u.header_image,u.gender from tb_radio as r,tb_user as u where r.uid=u.id limit #{begin},#{end} ")
    List<Radio> findPageList(Integer begin, Integer end);

    @Update("update tb_radio set hearts=hearts+1 where id=#{lid}")
    void pointToShowLove(Long lid);

    @Select("select r.title,r.copy,r.hearts,r.currentTime,u.nickname from tb_radio as r,tb_user as u where r.id =#{id} and r.uid=u.id")
    Radio getRadioById(Long id);

    @Update("update tb_radio set title=#{title},copy=#{copy},hearts=#{hearts} where id=#{id}")
    void editRadioInfo(Radio radio);

    @Select("select count(*) from tb_radio,tb_user where tb_radio.uid=tb_user.id and tb_user.nickname=#{nickname}")
    Integer getTotalCountByNickname(String nickname);

    @Select("select * from tb_radio,tb_user where tb_radio.uid = tb_user.id and tb_user.nickname = #{nickname} limit 1,10")
    List<Radio> getRadioListByNickname(String nickname);

    @Select("select r.title,r.copy,r.radio,r.hearts,r.cover,r.currentTime,u.nickname from tb_radio as r,tb_user as u where  r.uid = #{uid} and r.uid = u.id ")
    List<Radio> getRadioListByUid(@Param("uid") String id);
}
