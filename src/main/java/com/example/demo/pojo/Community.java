package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Community implements Serializable {

    //    动态id  唯一标识
    private Long id;

    //    动态内容  String
    private String content;

    //    动态的获赞数量 Integer
    private Integer hearts;

    //    这条动态所对应的uid
    private Long uid;

    //    动态的发布时间
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date currentTime;

    //    动态发起时所处的经纬度
    private Double lon;
    private Double lat;
    //    动态所属的话题
    private String tag;

    //    用户头像
    private String headerImage;

    //    用户昵称
    private String nickname;

    //    发动态时所在的街道
    private String street;

    //    动态头像
    private List<String> imageList;

    //    动态的唯一标识
    private String uuid;

    //    动态的评论数
    private Integer commentNum;

    //    动态列表
    private List<Comment> commentList;
}
