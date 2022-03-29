package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalismDetails implements Serializable {

    private Long id;

    private String title;

    private String content;

    private String image;

    private Long jid;

}
