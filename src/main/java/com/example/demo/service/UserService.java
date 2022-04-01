package com.example.demo.service;

import com.example.demo.pojo.*;
import com.example.demo.vo.Pagination;

import java.util.List;

public interface UserService {
    User findUserByTelephone(String telephone);

    void register(User user);

    String getBgImgById(Long valueOf);

    User getUserById(Long realUid);

    void editUserInfo(User user);

    Pagination<User> getUserList(Integer realCurrentPage, Integer realPageSize);

    User getUserInfoByTelephone(String telephone);

    void deleteUserById(Long realId);

    Boolean takeCareOf(Long caredId, Long beCaredOfId);

    void toReport(String cid);

    User searchUserByNickname(String nickname);

    List<User> getBeCaredOfUsers(Long realUid);

    List<ActiveMood> activeMoodFirst();

    List<QueAndAns> getQuestionDetailsById(Long acId);

    List<Meditation> getMeditationList();

    List<Sentence> getSentence();

    List<Book> getBookList(String name);

    List<Journalism> getJournalismList();

    List<JournalismDetails> getDetailsByJid(Long jid);

    List<Diary> getDiaryListByUid(Long uid);

    void commitDiary(Diary diary);

    Integer getConsecutiveDays(Long uid);
}
