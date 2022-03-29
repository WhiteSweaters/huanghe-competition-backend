package com.example.demo.controller;

import com.example.demo.pojo.Radio;
import com.example.demo.result.Result;
import com.example.demo.service.RadioService;
import com.example.demo.utils.QiNiuYunUtil;
import com.example.demo.vo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 心灵驿站模块
 */
@RestController
@RequestMapping("/radio")
@CrossOrigin
public class RadioController {

    @Autowired
    private RadioService radioService;

    /**
     * 心灵驿站板块 根据用户提供的分页数据查询列表给出对应卡片
     *
     * @return
     */
    @GetMapping("/showCardList")
    public Result showRadio(String currentPage, String pageSize) {
        Pagination<Radio> pagination = radioService.showCardList(currentPage, pageSize);
        return new Result(true, "电台卡片获取成功", pagination.getPageList());
    }

    @GetMapping("/showCardListBackground")
    public Result showRadioPagination(@RequestParam(name = "currentPage") String currentPageStr,
                                      @RequestParam(name = "pageSize") String pageSizeStr) {
        Pagination<Radio> pagination = null;
        try {
            pagination = radioService.showCardList(currentPageStr, pageSizeStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "获取作品列表失败，请联系后台技术人员解决", null);
        }
        return new Result(true, "获取作品列表成功", pagination);
    }

    /**
     * 分享模块 用户上传音频文件 并通过uid将tb_user与tb_radio两张表关联起来
     *
     * @return
     */
    @PostMapping("/uploadRadio")
    public Result uploadRadio(@RequestBody Map map) throws IOException {

//        获取前端传递过来的uid,title(作品标题),copy(作品文案),currentTime(作品时长)
        String uidStr = (String) map.get("uid");
        Long uid = Long.valueOf(uidStr);
        String title = (String) map.get("title");
        String copy = (String) map.get("copy");
        String currentTime = (String) map.get("currentTime");
        System.out.println("----------------------------------------");
        System.out.println(currentTime);
        System.out.println("----------------------------------------");

//        将以上数据封装到Radio对象中 存入数据库实现持久化
        Radio radio = new Radio();
        radio.setTitle(title);
        radio.setCopy(copy);
        radio.setUid(uid);
        radio.setCurrentTime(currentTime);


//        获取录音文件
        Map file = (HashMap) map.get("file");
        String base64 = (String) file.get("base64");
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] bytes = base64Decoder.decodeBuffer(base64);
        QiNiuYunUtil.uploadFile2(QiNiuYunUtil.ACCESS_KEY, QiNiuYunUtil.SECRET_KEY, QiNiuYunUtil.BUCKET_NAME, bytes);
        radio.setRadio(QiNiuYunUtil.hashName);


//        获取作品封面
        String cover = (String) map.get("cover");
        byte[] bytes1 = base64Decoder.decodeBuffer(cover);
        QiNiuYunUtil.uploadFile2(QiNiuYunUtil.ACCESS_KEY, QiNiuYunUtil.SECRET_KEY, QiNiuYunUtil.BUCKET_NAME, bytes1);
        radio.setCover(QiNiuYunUtil.hashName);


//        调用业务层
        try {
            radioService.addRadio(radio);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "写入数据失败，请联系管理员解决", null);
        }

        return new Result(true, "上传成功", null);
    }

    /**
     * 点赞功能
     *
     * @return
     */
    @GetMapping("/pointToShowLove/{id}")
    public Result pointToShowLove(@PathVariable(value = "id") String id) {
        Long Lid = Long.valueOf(id);
        try {
            radioService.pointToShowLove(Lid);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "点赞失败，请与管理员联系", null);
        }
        return new Result(true, "点赞成功！", null);
    }

    /**
     * 获取作品列表
     * @param idStr
     * @return
     */
    @GetMapping("/getRadioById/{id}")
    public Result getRadioById(@PathVariable(value = "id") String idStr) {
        Long id = Long.valueOf(idStr);
        Radio radio = radioService.getRadioById(id);
        return new Result(true, "", radio);
    }

    /**
     * 修改作品信息
     * @param radio
     * @return
     */
    @PostMapping("/editRadioInfo")
    public Result editRadioInfo(@RequestBody Radio radio) {
        try {
            radioService.editRadioInfo(radio);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改作品信息失败，请联系后台技术人员解决", null);
        }
        return new Result(true, "修改作品信息成功", null);
    }

    /**
     * 根据用户名称获取作品数据
     * @param nickname
     * @return
     */
    @GetMapping("/getRadioPaginationByNickname/{nickname}")
    public Result getRadioPaginationByNickname(@PathVariable(value = "nickname") String nickname) {
        Pagination<Radio> pagination = null;
        try {
            pagination = radioService.getRadioByNickname(nickname);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回对应作品失败，请联系后台技术人员解决", null);
        }
        if (pagination == null) {
            return new Result(false, "作者" + nickname + "并未发布任何作品", null);
        }
        return new Result(true, "返回数据成功", pagination);
    }

    /**
     * 获取用户的作品列表
     * @param id
     * @return
     */
    @GetMapping("/getRadioListByUid/{uid}")
    public Result getRadioListByUid(@PathVariable(name = "uid") String id){
        List<Radio> radioList = null;
        try{
            radioList = radioService.getRadioByUid(id);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"返回作品列表数据失败，请联系管理员解决",null);
        }
        return new Result(true,"返回作品列表数据成功",radioList);
    }

}
