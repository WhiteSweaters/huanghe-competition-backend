package com.example.demo.service.impl;

import com.example.demo.constants.IPConstant;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.*;
import com.example.demo.service.UserService;
import com.example.demo.vo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findUserByTelephone(String telephone) {

        User user = userMapper.findUserByTelephone(telephone);

        if (user == null) {
            User user1 = new User();
            user1.setTelephone(telephone);
            userMapper.addUser(telephone);
            User user2 = userMapper.findUserByTelephone(telephone);
            return user2;
        }

        return user;
    }

    @Override
    public void register(User user) {
        userMapper.register(user);
    }


    @Override
    public void editUserInfo(User user) {
        String headerImage = user.getHeaderImage();
        if (headerImage == null || headerImage.length() == 0) {
            userMapper.editUserInfoExceptHeaderImage(user);
        } else {
            userMapper.editUserInfo(user);
        }
    }

    @Override
    public Pagination<User> getUserList(Integer realCurrentPage, Integer realPageSize) {
//        查询数据库 获取数据总条数
        Integer totalCount = userMapper.getTotalCount();
//        获取总页数
        Integer totalPage = totalCount % realPageSize == 0 ? totalCount / realPageSize : totalCount / realPageSize + 1;
//        获取开始位置索引
        Integer begin = (realCurrentPage - 1) * realPageSize;
//        获取截至索引位置
        Integer end = begin + realPageSize;
//        根据开始位置索引和结束位置索引获取对应用户信息集合
        List<User> userList = userMapper.getUserList(begin, end);
        return new Pagination<User>(realCurrentPage, realPageSize, totalPage, totalCount, userList);
    }

    @Override
    public User getUserInfoByTelephone(String telephone) {
        return userMapper.getUserInfoByTelephone(telephone);

    }

    @Override
    public void deleteUserById(Long realId) {
        userMapper.deleteUserById(realId);
    }

    @Override
    public Boolean takeCareOf(Long caredId, Long beCaredOfId) {
        Long id = userMapper.findCareRelationShip(caredId, beCaredOfId);
        if (id == null || id == 0) {
            userMapper.takeCareOf(caredId, beCaredOfId);
            return true;
        }
        return false;
    }

    @Override
    public void toReport(String cid) {
        userMapper.toReport(cid);
    }

    @Override
    public User searchUserByNickname(String nickname) {
        User user = userMapper.searchUserByNickname(nickname);
        if (user == null) {
            return null;
        }
        String headerImage = user.getHeaderImage();
        String headerImage2 = IPConstant.url + headerImage;
        user.setHeaderImage(headerImage2);
        return user;
    }

    @Override
    public List<User> getBeCaredOfUsers(Long realUid) {

        List<User> beCaredOfUsers = userMapper.getBeCaredOfUsers(realUid);
        if (beCaredOfUsers == null) {
            return null;
        }
        for (User beCaredOfUser : beCaredOfUsers) {
            String headerImage = beCaredOfUser.getHeaderImage();
            String headerImage2 = IPConstant.url + headerImage;
            beCaredOfUser.setHeaderImage(headerImage2);
        }
        return beCaredOfUsers;
    }

    @Override
    public List<ActiveMood> activeMoodFirst() {
        List<ActiveMood> activeMoodList = userMapper.activeMoodFirst();
//        对图片文件的路径做处理
        for (ActiveMood activeMood : activeMoodList) {
            String image1 = activeMood.getImage();
            String image2 = IPConstant.url + image1;
            activeMood.setImage(image2);
        }
        return activeMoodList;
    }

    @Override
    public List<QueAndAns> getQuestionDetailsById(Long acId) {
        return userMapper.getQuestionDetailsById(acId);
    }

    @Override
    public List<Meditation> getMeditationList() {
        List<Meditation> meditationList = userMapper.getMeditationList();
        for (Meditation meditation : meditationList) {
            String image1 = meditation.getImage();
            String image2 = IPConstant.url + image1;
            String video1 = meditation.getVideo();
            String video2 = IPConstant.url + video1;
            meditation.setImage(image2);
            meditation.setVideo(video2);
        }
        return meditationList;
    }

    @Override
    public List<Sentence> getSentence() {

//        随机获取十条记录
        List<Sentence> sentenceList = userMapper.getSentence();
        for (Sentence sentence : sentenceList) {
            String image1 = sentence.getImage();
            String image2 = IPConstant.url + image1;
            sentence.setImage(image2);
        }

        return sentenceList;
    }

    @Override
    public List<Book> getBookList(String name) {
        List<Book> bookList = userMapper.getBookList(name);
        for (Book book : bookList) {
            String image1 = book.getImage();
            String image2 = IPConstant.url + image1;
            book.setImage(image2);
        }
        return bookList;
    }

    @Override
    public List<Journalism> getJournalismList() {
        List<Journalism> journalismList = userMapper.getJournalismList();

        for (Journalism journalism : journalismList) {
            String image1 = journalism.getImage();
            String image2 = IPConstant.url + image1;
            journalism.setImage(image2);
        }
        return journalismList;
    }

    @Override
    public List<JournalismDetails> getDetailsByJid(Long jid) {

        List<JournalismDetails> journalismDetailsList = userMapper.getDetailsByJid(jid);
        for (JournalismDetails journalismDetails : journalismDetailsList) {
            String image1 = journalismDetails.getImage();
            String image2 = IPConstant.url + image1;
            journalismDetails.setImage(image2);
        }

        return journalismDetailsList;
    }

    @Override
    public List<Diary> getDiaryListByUid(Long uid) {
        List<Diary> diaryList = userMapper.getDiaryListByUid(uid);
        for (Diary diary : diaryList) {
            String image1 = diary.getImage();
            String image2 = IPConstant.url + image1;
            diary.setImage(image2);
        }
        return diaryList;
    }

    @Override
    public void commitDiary(Diary diary) {
        Long uid = diary.getUid();
        Date date = diary.getDate();
//        判断该用户当天是否已经上传过日记
        Integer count = userMapper.findDiaryStatus(uid, date);
        if (count == null || count == 0) {
//            若不存在，则进行插入操作
            userMapper.commitDiary(diary);
        } else {
//            若存在，则执行更新操作
            userMapper.updateDiary(diary);
        }

    }

    @Override
    public Integer getConsecutiveDays(Long uid) {
        List<Diary> diaryList = userMapper.getDiaryListByUid(uid);
//        如何利用连续这个条件？
        Integer count = 1;
        for (int i = 0; i < diaryList.size() - 1; i++) {
            Long time1 = diaryList.get(i).getDate().getTime();
            Long time2 = diaryList.get(i + 1).getDate().getTime();
            if (time1 - time2 <= 86400000) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public Pagination<Diary> getDiaryListPagination(Integer currentPage, Integer pageSize, Long uid) {

        Integer totalCount = userMapper.getDiaryTotalCount(uid);
        Integer totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        Integer begin = (currentPage - 1) * pageSize;
        Integer end = currentPage * pageSize;
        List<Diary> diaryList = userMapper.getDiaryListPagination(begin, end, uid);
        for (Diary diary : diaryList) {
            String image1 = diary.getImage();
            String image2 = IPConstant.url + image1;
            diary.setImage(image2);
        }
        return new Pagination<Diary>(currentPage, pageSize, totalPage, totalCount, diaryList);
    }

    @Override
    public String getBgImgById(Long valueOf) {
        return IPConstant.url + userMapper.getBgImgById(valueOf);
    }

    @Override
    public User getUserById(Long realUid) {
        User user = userMapper.getUserById(realUid);
//        将用户的出生日期转为年龄
        Date birthday = user.getBirthday();
//        将日期解析为年份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
//        获取出生年份
        String birthYear = sdf.format(birthday);
//        获取当前年份
        Date date = new Date();
        String currentYear = sdf.format(date);
//        运算获取用户年龄
        Integer age = Integer.parseInt(currentYear) - Integer.parseInt(birthYear);
        user.setAge(age);

//        用户头像的处理
        String headerImage = user.getHeaderImage();
        String reaHeaderImage = IPConstant.url + headerImage;
        user.setHeaderImage(reaHeaderImage);
        return user;
    }

}
