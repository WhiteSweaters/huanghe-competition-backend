package com.example.demo.controller;

import com.example.demo.pojo.Community;
import com.example.demo.result.Result;
import com.example.demo.service.CommunityService;
import com.example.demo.utils.QiNiuYunUtil;
import com.example.demo.vo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    /**
     * 用户上传动态
     *
     * @param map
     * @return
     */
    @PostMapping("/uploadIdea")
    public Result uploadIdea(@RequestBody Map map) {


        try {
            //        2、获取其他内容
//        2.1动态文字
            String content = (String) map.get("content");
//        2.2用户当前的经度
            Double lon = (Double) map.get("lon");
//        用户当前的纬度
            Double lat = (Double) map.get("lat");
//        动态所属的话题
            String tag = (String) map.get("tag");
//        是谁发的动态？
            Long uid = Long.valueOf((String) map.get("uid"));
//            街道？
            String street = (String) map.get("street");

//        将数据封装到实体类中
            Community community = new Community();
            community.setContent(content);
            community.setCurrentTime(new Date());
            community.setLon(lon);
            community.setLat(lat);
            community.setTag(tag);
            community.setUid(uid);
            community.setStreet(street);
            community.setUuid(UUID.randomUUID().toString());

            Long cid = communityService.uploadIdea(community);
            System.out.println(cid);

//        1、获取用户上传的动态图片
//        将string类型的集合重新转换为List
            List<Map> tempImageList = (List<Map>) map.get("tempImageList");

//        遍历集合
            for (Map o : tempImageList) {
                List imgList = (List) o.get("assets");
                Map o1 = (HashMap) imgList.get(0);
                String base64 = (String) o1.get("base64");
                BASE64Decoder base64Decoder = new BASE64Decoder();
                //            得到每一张图片的字节数组
                byte[] bytes = base64Decoder.decodeBuffer(base64);
                QiNiuYunUtil.uploadFile2(QiNiuYunUtil.ACCESS_KEY, QiNiuYunUtil.SECRET_KEY, QiNiuYunUtil.BUCKET_NAME, bytes);
                //            获取此时的hashname 并将其存入数据库中 动态图片表： tb_community_images
                communityService.storageImg(QiNiuYunUtil.hashName, cid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发布失败，请联系后台管理员解决", null);
        }
        return new Result(true, "发布成功", null);
    }

    /**
     * 分页查询用户动态信息
     *
     * @param map
     * @return
     */
    @PostMapping("/getCommunityInfo")
    public Result getCommunityInfo(@RequestBody Map map) {

        String tag = (String) map.get("tag");
        Integer currentPage = Integer.parseInt((String) map.get("currentPage"));
        Integer pageSize = Integer.parseInt((String) map.get("pageSize"));
        Pagination<Community> pagination = null;
        try {
            pagination = communityService.getCommunityPagination(currentPage, pageSize, tag);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "拉取动态失败，请联系管理员解决", null);
        }
        return new Result(true, "拉取动态成功", pagination);
    }

    /**
     * 获取指定用户的动态信息
     *
     * @param uidStr
     * @param currentPageStr
     * @param pageSizeStr
     * @return
     */
    @GetMapping("/getLifeByUid")
    public Result getLifeByUid(@RequestParam(name = "uid") String uidStr,
                               @RequestParam(name = "currentPage") String currentPageStr,
                               @RequestParam(name = "pageSize") String pageSizeStr) {

        Long uid = Long.valueOf(uidStr);
        Integer currentPage = Integer.parseInt(currentPageStr);
        Integer pageSize = Integer.parseInt(pageSizeStr);

        Pagination<Community> pagination = null;
        try {
            pagination = communityService.getPaginationByUid(uid, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回用户信息失败，请联系后台管理员", null);
        }

        return new Result(true, "", pagination);
    }

    /**
     * 用户上传评论信息
     * @param map
     * @return
     */
    @PostMapping("/uploadComment")
    public Result uploadComment(@RequestBody Map map) {

//        获取评论内容、动态id、评论用户id
        String content = (String) map.get("content");
        String cidStr = (String) map.get("cid");
        String uidStr = (String) map.get("uid");

//        将id转换为Long类型
        Long cid = Long.valueOf(cidStr);
        Long uid = Long.valueOf(uidStr);

//        调用业务层 完成对评论内容的持久化
        try {
            communityService.uploadComment(content, cid, uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "评论失败，请联系后台管理员解决", null);
        }

        return new Result(true, "发布评论成功", null);
    }


    @GetMapping("/getCaredOfNum/{uid}")
    public Result getCaredOfNum(@PathVariable(value = "uid") String uidStr){

        Long uid = Long.valueOf(uidStr);
        Integer caredOfNum = null;

        try {
            caredOfNum = communityService.getCaredOfNum(uid);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"返回用户关注失败，请联系管理员解决",null);
        }

        return new Result(true,"返回用户关注数量成功",caredOfNum);
    }

}
