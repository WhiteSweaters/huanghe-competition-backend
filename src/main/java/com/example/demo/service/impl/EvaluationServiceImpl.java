package com.example.demo.service.impl;

import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.OrderSetting;
import com.example.demo.service.EvaluationService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 问卷调查板块
 */
@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Map<String, Object>> getBtnData() {
        return evaluationMapper.getBtnData();
    }

    @Override
    public void result(Integer result, Integer uid) {
        Integer resultBefore = evaluationMapper.selectResultByUid(uid);
        if (resultBefore == null || resultBefore == 0) {
            evaluationMapper.result(result, uid);
            userMapper.changeUserForEvaluation(uid);
        } else {
            evaluationMapper.changeResult(result, uid);
        }

    }

    @Override
    public Integer getResultByUid(Long realUid) {
        return evaluationMapper.getResultByUid(realUid);
    }

    @Override
    public void addOrderSetting(List<OrderSetting> orderSettingList) {
        if (orderSettingList != null && orderSettingList.size() > 0) {
            for (OrderSetting orderSetting : orderSettingList) {
                Long count = evaluationMapper.findCountByOrderDate(orderSetting.getOrderDate());
                if (count > 0) {
                    evaluationMapper.editNumberByOrderDate(orderSetting);
                } else {
                    evaluationMapper.addOrderSetting(orderSetting);
                }
            }
        }
    }

    @Override
    public List<OrderSetting> getOrderSettings() {
        return evaluationMapper.getOrderSettings();
    }

    @Override
    public Integer makeAnAppointment(String name, String telephone, String idCard,String orderDate1,String orderDate2) {

//        判断当天的预约数是否已满
        OrderSetting orderSetting = evaluationMapper.getOrderSettingByOrderDate(orderDate2);
//        如果当天不开放预约  则返回对应提示
        if(orderSetting == null){
            return 0;
        }
        Integer number = orderSetting.getNumber();
        Integer reservations = orderSetting.getReservations();
//        如果已经预约满了 就给用户返回相应信息
        if(reservations >= number){
            return 1;
        }
//        没满的话，则储存用户信息，并给当天的预约人数+1
//        tb_appointment表
        evaluationMapper.makeAnAppointment(name, telephone, idCard, orderDate1);
//        tb_ordersetting表
        evaluationMapper.changeReservations(orderDate2);
//        tb_appointment_ordersetting表
        Long aid = evaluationMapper.getAidByOrderDate1(orderDate1);
        Long oid = evaluationMapper.getOidByOrderDate2(orderDate2);
        evaluationMapper.link2Tables(aid,oid);
        return 2;
    }

    @Override
    public List<Evaluation> getUserAppointmentStatus(String telephone) {
        return evaluationMapper.getUserAppointmentStatus(telephone);
    }

    @Override
    public void cancelBooking(String telephone, String orderDate) {
        evaluationMapper.cancelBooking(telephone,orderDate);
    }


}
