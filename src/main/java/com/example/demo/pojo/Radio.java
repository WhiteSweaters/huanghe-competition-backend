package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Radio implements Serializable {
//    tb_radio表的id
    private Long id;
//    外链 关联 tb_user
    private Long uid;
//    作品标题
    private String title;
//    作品文案
    private String copy;
//    作品文件名称 配合七牛云提供的域名访问
    private String radio;
//    作品点赞数量
    private Integer hearts;
//    作者名称
    private String nickname;
//    作者头像
    private String headerImage;
//    作者性别
    private String gender;
//    作品封面
    private String cover;

//    作品时长
    private String currentTime;

}
