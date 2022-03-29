package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meditation implements Serializable {
//    主键
    private Long id;
//    冥想标题
    private String title;
//    冥想说明
    private String tag;
//    冥想对应图片
    private String image;
//    音频
    private String video;
}
