package com.example.demo.service;

import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EvaluationService {
    List<Map<String, Object>> getBtnData();

    void result(Integer result, Integer uid);

    Integer getResultByUid(Long realUid);

    void addOrderSetting(List<OrderSetting> orderSettingList);

    List<OrderSetting> getOrderSettings();

    Integer makeAnAppointment(String name, String telephone, String idCard, String orderDate1,String orderDate2);

    List<Evaluation> getUserAppointmentStatus(String telephone);

    void cancelBooking(String telephone, String orderDate);
}
