package com.example.demo.service;

import com.example.demo.pojo.Radio;
import com.example.demo.vo.Pagination;

import java.util.List;

public interface RadioService {
    void addRadio(Radio radio);
    Pagination<Radio> showCardList(String currentPage, String pageSize);

    void pointToShowLove(Long lid);

    Radio getRadioById(Long id);

    void editRadioInfo(Radio radio);

    Pagination<Radio> getRadioByNickname(String nickname);

    List<Radio> getRadioByUid(String id);
}
