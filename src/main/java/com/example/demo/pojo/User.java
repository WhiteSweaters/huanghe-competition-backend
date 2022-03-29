package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
    //    用户编号
    private Long id;
    //    手机号码
    private String telephone;
    //    是否为新用户
    private Boolean isNew;
    //    用户性别
    private String gender;
    //    用户出生日期
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    //    用户注册时所在的街道
    private String street;
    //    用户的个性签名
    private String motto;
    //    用户的昵称
    private String nickname;
    //    用户头像
    private String headerImage;
    //    用户年龄
    private Integer age;
//    用户之前是否填写过最初的问卷
    private Boolean newForEvaluation;

}
