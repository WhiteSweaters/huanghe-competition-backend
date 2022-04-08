package com.example.demo.controller;


import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.OrderSetting;
import com.example.demo.pojo.Reason;
import com.example.demo.result.Result;
import com.example.demo.service.EvaluationService;
import com.example.demo.utils.POIUtils;
import com.example.demo.utils.SMSUtil;
import com.example.demo.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 问卷调查板块
 */
@RestController
@RequestMapping("/evaluation")
@CrossOrigin
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取标签以及标签所占有的分值
     *
     * @return
     */
    @GetMapping("/getBtnData")
    public Result getBtnData() {

        List<Reason> reasonList = null;

        try {
            reasonList = evaluationService.getBtnData();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回选项失败，请联系后台管理员解决", null);
        }
        return new Result(true, "返回按钮数据成功", reasonList);
    }



    /**
     * 根据用户ID获取用户的心理状况
     *
     * @param uid
     * @return
     */
    @GetMapping("/getResultByUid/{uid}")
    public Result getResultByUid(@PathVariable(value = "uid") String uid) {
        Long realUid = Long.valueOf(uid);
       Map<String,Object> map = null;
        try {
           map =  evaluationService.getResultByUid(realUid);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回用户心理健康状况失败，请联系后台管理员解决", null);
        }

        return new Result(true, "返回用户心理健康信息成功", map);
    }

    /**
     * 上传EXCEL文件组件
     *
     * @param file
     */
    @PostMapping("/uploadOrderSetting")
    public Result uploadOrderSetting(MultipartFile file) {
        try {
            List<String[]> list = POIUtils.readExcel(file);
            List<OrderSetting> orderSettingList = new ArrayList<OrderSetting>();
            for (String[] strings : list) {
                OrderSetting orderSetting = new OrderSetting();
                String orderDate = strings[0];
                String number = strings[1];
                orderSetting.setOrderDate(new Date(orderDate));
                orderSetting.setNumber(Integer.parseInt(number));
                orderSettingList.add(orderSetting);
            }
            evaluationService.addOrderSetting(orderSettingList);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "文件上传失败，请联系技术人员解决", null);
        }
        return new Result(true, "上传预约数据成功", null);
    }

    /**
     * 后台管理系统获取预约设置信息
     *
     * @return
     */
    @GetMapping("/getOrderSettings")
    public Result getOrderSettings() {

        List<OrderSetting> orderSettingList = null;

        try {
            orderSettingList = evaluationService.getOrderSettings();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回预约列表失败，请联系技术人员解决", null);
        }

        return new Result(true, "返回预约数据成功", orderSettingList);
    }

    /**
     * 根据用户手机号发送验证码
     *
     * @param telephone
     * @return
     */
    @GetMapping("/sendVcode/{telephone}")
    public Result sendVcode(@PathVariable(value = "telephone") String telephone) {
        if (telephone == null) {
            return null;
        }
        try {
            String vcode = ValidateCodeUtils.generateValidateCode(4).toString();
            SMSUtil.sendMessage(SMSUtil.APPOINTMENT_TEMPLATE_ID, telephone, vcode);
            redisTemplate.opsForValue().set(SMSUtil.APPOINTMENT_TEMPLATE_ID + telephone, vcode, 5 * 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "短信发送失败，请联系后台管理员解决", null);
        }

        return new Result(true, "短信发送成功，请注意查收", null);
    }

    /**
     * 咨询预约验证码校验以及用户提交表单
     *
     * @param map
     * @return
     */
    @PostMapping("/makeAnAppointment")
    public Result makeAnAppointment(@RequestBody Map<String, Object> map) throws ParseException {

        String name = (String) map.get("name");
        String telephone = (String) map.get("telephone");
        String vcode = (String) map.get("vcode");
//        校验验证码是否正确
        String vcodeInRedis = (String) redisTemplate.opsForValue().get(SMSUtil.APPOINTMENT_TEMPLATE_ID + telephone);
        if (!vcode.equals(vcodeInRedis)) {
            return new Result(false, "验证码错误！", null);
        }
        String idCard = (String) map.get("idCard");
        String orderDate1 = (String) map.get("orderDate1");
        String orderDate2 = (String) map.get("orderDate2");
//        将预约数据存入数据库中：tb_appointment,tb_ordersetting
        Integer flag = null;
        try {
            flag = evaluationService.makeAnAppointment(name, telephone, idCard, orderDate1, orderDate2);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "预约失败，请联系后台管理员解决", null);
        }
        if (flag == 0) {
            return new Result(false, "当天不开放预约，请选择其他时间", null);
        }
        if (flag == 1) {
            return new Result(false, "当天预约已满，请选择其他时间", null);
        }
        return new Result(true, "预约成功", null);
    }

    /**
     * 获取用户的预约列表
     *
     * @param telephone
     * @return
     */
    @GetMapping("/getUserAppointmentStatus/{telephone}")
    public Result getUserAppointmentStatus(@PathVariable(value = "telephone") String telephone) {

        List<Evaluation> evaluationList = null;

        try {
            evaluationList = evaluationService.getUserAppointmentStatus(telephone);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "返回当前用户预约信息失败，请联系后台管理员解决", null);
        }

        if (evaluationList == null) {
            return new Result(true, "返回当前用户预约信息成功", false);
        }

        return new Result(true, "返回当前用户预约信息成功", evaluationList);
    }

    /**
     * 取消预约
     *
     * @param telephone
     * @param orderDate
     * @return
     */
    @RequestMapping("/cancelBooking")
    public Result cancelBooking(String telephone, String orderDate) {
        try {
            evaluationService.cancelBooking(telephone, orderDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "取消预约失败，请联系后台管理员解决", null);
        }
        return new Result(true, "取消预约成功", null);
    }

    /**
     * 将用户选项信息存入数据库
     * @param uidStr
     * @param scoreStr
     * @param reasonid
     * @return
     */
    @GetMapping("/submitResult")
    public Result submitResult(@RequestParam(value = "uid") String uidStr,
                               @RequestParam(value = "score") String scoreStr,
                               @RequestParam(value = "reasonid") Integer[] reasonid) {
        Long uid = Long.valueOf(uidStr);
        Integer score = Integer.parseInt(scoreStr);
        try {
            evaluationService.submitResult(uid,score,reasonid);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"上传测评结果失败，请联系管理员解决",null);
        }
        return new Result(true,"上传测评信息成功",null);
    }
}
