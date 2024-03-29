package com.example.demo.mapper;

import com.example.demo.pojo.Evaluation;
import com.example.demo.pojo.OrderSetting;
import com.example.demo.pojo.Reason;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EvaluationMapper {

    @Select("select * from tb_reason")
    List<Reason> getBtnData();

    @Insert("insert into tb_result (result,uid) values(#{result},#{uid})")
    void result(Integer result, Integer uid);

    @Select("select result from tb_result where uid = #{uid}")
    Map<String,Object> getResultByUid(@Param("uid") Long realUid);

    @Select("select result from tb_result where uid = #{result}")
    Integer selectResultByUid(Integer uid);

    @Update("update tb_result set result = #{result} where uid = #{uid}")
    void changeResult(Integer result, Integer uid);

    @Select("select count(id) from tb_ordersetting where orderDate = #{orderDate}")
    Long findCountByOrderDate(Date orderDate);

    @Update("update tb_ordersetting set number = #{number} where orderDate=#{orderDate}")
    void editNumberByOrderDate(OrderSetting orderSetting);

    @Insert("insert into tb_ordersetting (orderDate,number,reservations) values(#{orderDate},#{number},#{reservations})")
    void addOrderSetting(OrderSetting orderSetting);

    @Select("select * from tb_ordersetting")
    List<OrderSetting> getOrderSettings();

    void makeAnAppointment(String name, String telephone, String idCard, String orderDate);

    void changeReservations(String orderDate);

    @Select("select * from tb_ordersetting where orderDate=#{orderDate}")
    OrderSetting getOrderSettingByOrderDate(@Param("orderDate") String orderDate);

    @Select("select * from tb_appointment where telephone = #{telephone}")
    List<Evaluation> getUserAppointmentStatus(String telephone);

    @Select("select id from tb_appointment where orderDate = #{orderDate1}")
    Long getAidByOrderDate1(String orderDate1);

    @Select("select id from tb_ordersetting where orderDate = #{orderDate2}")
    Long getOidByOrderDate2(String orderDate2);

    @Insert("insert into tb_appointment_ordersetting(aid,oid) values(#{aid},#{oid}) ")
    void link2Tables(Long aid, Long oid);

    @Delete("delete from tb_appointment where telephone = #{telephone} and orderDate=#{orderDate}")
    void cancelBooking(String telephone, String orderDate);

    @Select("select count(uid) from tb_result")
    Integer findUserResult(Long uid);

    @Insert("insert into tb_result (uid,score,reasonid) values(#{uid},#{score},#{reid})")
    void addResult(Long uid, Double score, Integer reid);

    List<Reason> getResultListByUid(Long realUid);

    @Select("select score from tb_result where uid = #{realUid}")
    Integer[] getScoreByUid(Long realUid);

    void updateResult(Long uid, Integer score, Integer reid);

    void deleteResultByUid(Long uid);
}
