package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSetting implements Serializable {

    private Integer id;
    private Date orderDate;//预约设置日期
    private Integer number;//可预约人数
    private Integer reservations;//已预约人数
}
