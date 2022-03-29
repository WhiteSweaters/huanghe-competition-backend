package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sentence implements Serializable {

//    主键
    private Long id;

//    图片
    private String image;

//    内容
    private String content;

//    作者
    private String author;

}
