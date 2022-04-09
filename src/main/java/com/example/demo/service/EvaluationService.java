package com.example.demo.service;

import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.OrderSetting;
import com.example.demo.pojo.Reason;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EvaluationService {
    List<Reason> getBtnData();


    Map<String,Object> getResultByUid(Long realUid);

    void addOrderSetting(List<OrderSetting> orderSettingList);

    List<OrderSetting> getOrderSettings();

    Integer makeAnAppointment(String name, String telephone, String idCard, String orderDate1,String orderDate2);

    List<Evaluation> getUserAppointmentStatus(String telephone);

    void cancelBooking(String telephone, String orderDate);

    void submitResult(Long uid, Double score, Integer[] reasonid);
}
