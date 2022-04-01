package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary implements Serializable {

//    主键
    private Long id;

//    日记的日期
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

//    当天天气
    private String weather;

//    用户写日记时的心情
    private String mood;

//    日记内容
    private String content;

//    用户id
    private Long uid;

//    用户附加的图片
    private String image;

//    这篇日记的情绪色彩
    private String tag;

}
