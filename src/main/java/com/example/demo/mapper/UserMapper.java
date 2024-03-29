package com.example.demo.mapper;

import com.example.demo.pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface UserMapper {

    @Select("select * from tb_user where telephone = #{telephone}")
    User findUserByTelephone(@Param("telephone") String telephone);

    @Insert("insert into tb_user(telephone) values(#{telephone})")
    void addUser(@Param("telephone") String telephone);

    @Update("update tb_user set is_new = false, gender=#{gender},birthday=#{birthday},nickname=#{nickname},street=#{street},motto=#{motto},header_image=#{headerImage} where telephone = #{telephone}")
    void register(User user);

    @Select("select header_image from tb_user where id = #{id}")
    String getBgImgById(@Param("id") Long valueOf);

    @Select("select * from tb_user where id = #{id}")
    User getUserById(@Param("id") Long realUid);

    @Update("update tb_user set gender=#{gender},birthday=#{birthday},nickname=#{nickname},street=#{street},motto=#{motto},header_image=#{headerImage} where telephone = #{telephone}")
    void editUserInfo(User user);

    @Select("select count(*) from tb_user")
    Integer getTotalCount();

    @Select("select * from tb_user limit #{begin},#{end}")
    List<User> getUserList(Integer begin, Integer end);

    @Update("update tb_user set gender=#{gender},birthday=#{birthday},nickname=#{nickname},street=#{street},motto=#{motto} where telephone = #{telephone}")
    void editUserInfoExceptHeaderImage(User user);

    @Select("select * from tb_user where telephone = #{telephone}")
    User getUserInfoByTelephone(String telephone);

    @Delete("delete from tb_user where id = #{id}")
    void deleteUserById(@Param(value = "id") Long realId);

    void takeCareOf(Long caredId, Long beCaredOfId);

    Long findCareRelationShip(Long caredId, Long beCaredOfId);

    void toReport(String cid);

    User searchUserByNickname(String nickname);

    List<User> getBeCaredOfUsers(@Param("care") Long realUid);

    void changeUserForEvaluation(Long uid);

    @Select("select * from tb_active_mood")
    List<ActiveMood> activeMoodFirst();

    @Select("select * from tb_active_details where acid = #{acId}")
    List<QueAndAns> getQuestionDetailsById(Long acId);

    List<Meditation> getMeditationList(Integer begin,Integer pageSize);

    @Select("select * from tb_sentence  limit 0,10")
    List<Sentence> getSentence();

    @Select("select * from tb_book where name = #{name}")
    List<Book> getBookList(String name);

    List<Journalism> getJournalismList();

    List<JournalismDetails> getDetailsByJid(Long jid);

    List<Diary> getDiaryListByUid(Long uid);

    void commitDiary(Diary diary);

    Integer findDiaryStatus(Long uid, Date date);

    void updateDiary(Diary diary);

    @Select("select count(*) from tb_diary where uid=#{uid}")
    Integer getDiaryTotalCount(Long uid);

    List<Diary> getDiaryListPagination(Integer begin, Integer end, Long uid);

    Integer getAllMeditationCount();
}
