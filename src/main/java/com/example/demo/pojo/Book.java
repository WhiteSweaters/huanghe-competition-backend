package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {

//    主键
    private Long id;

//    书名
    private String title;

//    图片
    private String image;

//    作者
    private String author;

//    外链
    private String link;

//    译者
    private String translator;

//    出版社
    private String press;

//    隶属标签
    private String name;

//    内容
    private String content;
}
