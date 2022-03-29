package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveMood implements Serializable {

//    主键
    private Long id;

//    问题的标题
    private String title;

//    问题的主体
    private String content;

//    对应的图片文件
    private String image;
}
