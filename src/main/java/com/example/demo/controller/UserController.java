package com.example.demo.controller;

import com.example.demo.pojo.*;
import com.example.demo.result.Result;
import com.example.demo.service.UserService;
import com.example.demo.utils.QiNiuYunUtil;
import com.example.demo.utils.SMSUtil;
import com.example.demo.utils.ValidateCodeUtils;
import com.example.demo.vo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户模块
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 根据用户输入的手机号向用户发送验证码 存入redis以便后续验证登录信息
     *
     * @param map
     * @return
     */
    @PostMapping("/sms")
    public Result sms(@RequestBody Map map) {
        String telephone = (String) map.get("telephone");
        if (telephone == null) {
            return null;
        }
//        手机号合法,发送短信
        try {
            String vcode = ValidateCodeUtils.generateValidateCode(4).toString();
            SMSUtil.sendMessage(SMSUtil.LOGIN_TEMPLATE_ID, telephone, vcode);
//            将生成的验证码发送到redis服务器中暂时储存起来
            redisTemplate.opsForValue().set(SMSUtil.LOGIN_TEMPLATE_ID + telephone, vcode, 5 * 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "短信发送失败，请联系管理员解决", null);
        }
        return new Result(true, "短信发送成功，请注意查收", null);
    }

    /**
     * 根据用户提供的手机号和验证码验证用户是否登陆成功
     *
     * @param map
     * @return
     */
    @PostMapping("/checkVcode")
    public Result checkVcode(@RequestBody Map map) {

        String telephone = (String) map.get("telephone");
        String vcode = (String) map.get("vcode");

        if (telephone == null || vcode == null) {
            return new Result(false, "服务器出错，请联系管理员解决", null);
        }

        String vcodeInRedis = (String) redisTemplate.opsForValue().get(SMSUtil.LOGIN_TEMPLATE_ID + telephone);

        if (!vcode.equals(vcodeInRedis)) {
            return new Result(false, "验证码错误", null);
        }

//        验证码输入正确 通过手机号判断用户是否为新用户
        User user = userService.findUserByTelephone(telephone);

        return new Result(true, "验证码输入正确，正在跳转页面", user);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     * @throws IOException
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) throws IOException {
//        获取用户头像base64
        String headerImage = user.getHeaderImage();
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] bytes = base64Decoder.decodeBuffer(headerImage);
//        七牛云上传图片
        QiNiuYunUtil.uploadFile2(QiNiuYunUtil.ACCESS_KEY, QiNiuYunUtil.SECRET_KEY, QiNiuYunUtil.BUCKET_NAME, bytes);
        user.setHeaderImage(QiNiuYunUtil.hashName);

//        根据用户手机号向数据库中存储对应用户相关信息
        try {
            userService.register(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败，请联系管理员解决", null);
        }
        return new Result(true, "注册成功，正在跳转至首页", null);
    }

    /**
     * 根据用户手机号码修改用户信息
     *
     * @param user
     * @return
     * @throws IOException
     */
    @PostMapping("/editUserInfo")
    public Result editUserInfo(@RequestBody User user) throws IOException {

//        获取用户头像base64
        String headerImage = user.getHeaderImage();
        if (headerImage != null && headerImage.length() != 0) {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] bytes = base64Decoder.decodeBuffer(headerImage);
            //        七牛云上传图片
            QiNiuYunUtil.uploadFile2(QiNiuYunUtil.ACCESS_KEY, QiNiuYunUtil.SECRET_KEY, QiNiuYunUtil.BUCKET_NAME, bytes);
            user.setHeaderImage(QiNiuYunUtil.hashName);
        }

//        根据用户手机号向数据库中存储对应用户相关信息
        try {
            userService.editUserInfo(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败，请联系管理员解决", null);
        }
        return new Result(true, "修改成功", null);
    }

    /**
     * 上传图片测试
     *
     * @param headerImg
     */
    @PostMapping("/testFile")
    public void testFile(@RequestParam("headerImg") MultipartFile headerImg) {
        System.out.println(headerImg);
    }

    /**
     * 根据uid获取用户头像
     *
     * @param uid
     * @return
     */
    @GetMapping("/getBgImgById/{uid}")
    public Result getGenderById(@PathVariable(value = "uid") String uid) {

        String gender = userService.getBgImgById(Long.valueOf(uid));

        return new Result(true, "", gender);
    }

    /**
     * 根据uid 获取用户详细信息
     *
     * @param uid
     * @return
     */
    @GetMapping("/getUserById/{uid}")
    public Result getUserById(@PathVariable(value = "uid") String uid) {
        Long realUid = Long.valueOf(uid);
        User user = userService.getUserById(realUid);
        return new Result(true, "", user);
    }

    /**
     * 根据查询条件对用户列表进行分页展示
     *
     * @param currentPage
     * @param pageSize
     * @param queryConditions
     * @return
     */
    @GetMapping("/getUserList")
    public Result getUserList(@RequestParam(name = "currentPage", required = true) String currentPage,
                              @RequestParam(name = "pageSize", required = true) String pageSize,
                              @RequestParam(name = "queryConditions", required = false) String queryConditions) {

//        获取整型当前页和页大小
        Integer realCurrentPage = Integer.parseInt(currentPage);
        Integer realPageSize = Integer.parseInt(pageSize);

        Pagination<User> userPagination = null;

        if (queryConditions == null || queryConditions.length() == 0) {
//            无条件查询
            userPagination = userService.getUserList(realCurrentPage, realPageSize);
        }

        return new Result(true, "请求成功", userPagination);
    }

    /**
     * 根据用户手机号查询用户信息
     *
     * @param telephone
     * @return
     */
    @GetMapping("/getUserInfoByTelephone/{telephone}")
    public Result getUserInfoByTelephone(@PathVariable(value = "telephone") String telephone) {
        User user = userService.getUserInfoByTelephone(telephone);
        if (user == null) {
            return new Result(false, "不存在号码为" + telephone + "的用户，请检查！", null);
        }
//        注意 前端Table表格需要返回的是一个数组类型的User对象 所以这里需要先用List封装一下
        List<User> userList = new ArrayList<User>();
        userList.add(user);
        return new Result(true, "获取用户信息成功！", userList);
    }

    /**
     * 根据用户id删除对应用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteUserById/{id}")
    public Result deleteUserById(@PathVariable(value = "id") String id) {
        Long realId = Long.valueOf(id);
        try {
            userService.deleteUserById(realId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败，请联系后台技术人员解决", null);
        }
        return new Result(true, "操作成功", null);
    }

    /**
     * 点击关注按钮  关注对应用户
     *
     * @param caredIdStr
     * @param beCardOfIdStr
     * @return
     */
    @GetMapping("/takeCareOf")
    public Result takeCareOf(@RequestParam(name = "caredId") String caredIdStr,
                             @RequestParam(name = "beCaredOfId") String beCardOfIdStr) {
        Long caredId = Long.valueOf(caredIdStr);  //关注者ID
        Long beCaredOfId = Long.valueOf(beCardOfIdStr);  //被关注者ID

        Boolean flag = null;

        try {
            flag = userService.takeCareOf(caredId, beCaredOfId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "关注失败，请联系后台管理员解决", null);
        }
        if (flag) {
            return new Result(true, "关注成功", null);
        } else {
            return new Result(false, "你已经关注过该用户了哦", null);
        }
    }

    /**
     * 用户点击举报按钮  举报对应动态
     *
     * @param cid
     * @return
     */
    @GetMapping("/toReport/{cid}")
    public Result toReport(@PathVariable(value = "cid") String cid) {

        Long realCid = Long.valueOf(cid);
        try {
            userService.toReport(cid);
        } catch (Exception e) {
            e.printStackTrace();
            new Result(false, "举报失败，请联系后台管理员解决", null);
        }

        return new Result(true, "举报成功，谢谢您为健康的网络环境做出的贡献！", null);
    }

    /**
     * 搜索框精准查询用户信息，打开聊天功能
     *
     * @param nickname
     * @return
     */
    @GetMapping("/searchUserByNickname/{nickname}")
    public Result searchUserByNickname(@PathVariable(value = "nickname") String nickname) {
        User user = null;
        try {
            user = userService.searchUserByNickname(nickname);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "查询失败，请联系后台管理员解决", null);
        }

        if (user == null) {
            return new Result(false, "用户不存在", null);
        }

        List<User> userList = new ArrayList<User>();
        userList.add(user);

        return new Result(true, "查询成功", userList);
    }

    /**
     * 查询用户的关注对象 作为聊天列表的首次返回值
     *
     * @param uid
     * @return
     */
    @GetMapping("/getBeCaredOfUsers/{uid}")
    public Result getBeCaredOfUsers(@PathVariable(value = "uid") String uid) {
        Long realUid = Long.valueOf(uid);
        List<User> userList = null;
        try {
            userList = userService.getBeCaredOfUsers(realUid);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "查询好友列表失败，请联系后台管理员解决", null);
        }
        return new Result(true, "返回好友列表成功", userList);
    }

    /**
     * 获取每日正念知识列表
     *
     * @return
     */
    @GetMapping("/activeMoodFirst")
    public Result activeMoodFirst() {

        List<ActiveMood> activeMoodList = null;

        try {
            activeMoodList = userService.activeMoodFirst();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回问答失败，请与后台管理员联系", null);
        }

        return new Result(true, "返回问答成功", activeMoodList);
    }

    /**
     * 根据知识列表的Id获取与之对应的问答详情
     *
     * @param acId
     * @return
     */
    @GetMapping("/getQuestionDetailsById/{acId}")
    public Result getQuestionDetailsById(@PathVariable(value = "acId") String acId) {

        List<QueAndAns> queAndAnsList = null;

        try {
            queAndAnsList = userService.getQuestionDetailsById(Long.valueOf(acId));
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回对应问答失败，请联系后台管理员解决", null);
        }

        return new Result(true, "返回对应问答列表成功", queAndAnsList);
    }

    /**
     * 获取冥想音频文件集合
     *
     * @return
     */
    @GetMapping("/getMeditationList")
    public Result getMeditationList() {

        List<Meditation> meditationList = null;

        try {
            meditationList = userService.getMeditationList();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "查询冥想音频文件失败，请与后台管理员联系", null);
        }

        return new Result(true, "查询成功", meditationList);
    }

    /**
     * 获取每日十句
     *
     * @return
     */
    @GetMapping("/getSentence")
    public Result getSentence() {

        List<Sentence> sentenceList = null;

        try {
            sentenceList = userService.getSentence();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回十个句子失败，请联系管理员解决", null);
        }

        return new Result(true, "返回十个句子成功", sentenceList);
    }

    /**
     * 根据分类获取图书集合
     *
     * @param name
     * @return
     */
    @GetMapping("/getBookList/{name}")
    public Result getBookList(@PathVariable(value = "name") String name) {

        List<Book> bookList = null;

        try {
            bookList = userService.getBookList(name);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回图书列表失败，请联系管理员解决", null);
        }
        return new Result(true, "返回图书列表成功", bookList);
    }

    /**
     * 根获取新闻列表
     *
     * @return
     */
    @GetMapping("/getJournalismList")
    public Result getJournalismList() {

        List<Journalism> journalismList = null;

        try {
            journalismList = userService.getJournalismList();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回新闻列表失败，请与管理员联系解决", null);
        }

        return new Result(true, "返回新闻信息成功", journalismList);
    }

    @GetMapping("/getDetailsByJid/{jid}")
    public Result getDetailsByJid(@PathVariable(value = "jid") String jidStr) {
        Long jid = Long.valueOf(jidStr);
        List<JournalismDetails> journalismDetailsList = null;

        try {
            journalismDetailsList = userService.getDetailsByJid(jid);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回对应新闻信息失败，请联系管理员解决", null);
        }

        return new Result(true, "返回新闻信息成功", journalismDetailsList);
    }

    /**
     * 获取用户日记列表
     *
     * @param uidStr
     * @return
     */
    @GetMapping("/getDiaryListByUid/{uid}")
    public Result getDiaryListByUid(@PathVariable(value = "uid") String uidStr) {

        Long uid = Long.valueOf(uidStr);
//        获取日记列表
        List<Diary> diaryList = null;
        try {
            diaryList = userService.getDiaryListByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            new Result(false, "返回日记列表失败，请联系管理员解决", null);
        }

        return new Result(true, "返回日记列表成功", diaryList);
    }

    /**
     * 用户上传日记接口
     *
     * @param map
     * @return
     * @throws IOException
     */
    @PostMapping("/commitDiary")
    public Result commitDiary(@RequestBody Map map) throws IOException, ParseException {

        String content = (String) map.get("content");
        String dateStr = (String) map.get("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(dateStr);
        String mood = (String) map.get("mood");
        String tag = (String) map.get("tag");
        String uidStr = (String) map.get("uid");
        Long uid = Long.valueOf(uidStr);
        String title = (String) map.get("title");
        String weather = (String) map.get("weather");

        List<Map> tempImageList = (List<Map>) map.get("tempImageList");
        Map map1 = tempImageList.get(0);
        List list = (List) map1.get("assets");
        Map map2 = (Map) list.get(0);
        String base64 = (String) map2.get("base64");
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] bytes = base64Decoder.decodeBuffer(base64);
        QiNiuYunUtil.uploadFile2(QiNiuYunUtil.ACCESS_KEY, QiNiuYunUtil.SECRET_KEY, QiNiuYunUtil.BUCKET_NAME, bytes);

//        新建实体对象
        Diary diary = new Diary(null, date, weather, mood, content, uid, QiNiuYunUtil.hashName, tag);

//        存储数据
        try {
            userService.commitDiary(diary);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传日记失败，请联系管理员解决", null);
        }

        return new Result(true, "上传成功", null);
    }

}
