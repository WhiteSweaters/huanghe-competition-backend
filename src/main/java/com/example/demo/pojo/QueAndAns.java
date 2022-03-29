package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueAndAns implements Serializable {

    private Long id;
    private String question;
    private String answer;
    private Long acId;
}
