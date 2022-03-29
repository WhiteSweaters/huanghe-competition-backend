package com.example.demo.service.impl;

import com.example.demo.constants.IPConstant;
import com.example.demo.mapper.RadioMapper;
import com.example.demo.pojo.Radio;
import com.example.demo.service.RadioService;
import com.example.demo.vo.Pagination;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RadioServiceImpl implements RadioService {

    @Autowired
    private RadioMapper radioMapper;

    /**
     * 实现用户上传音频文件
     *
     * @param radio
     */
    @Override
    public void addRadio(Radio radio) {
        radioMapper.addRadio(radio);
    }

    /**
     * 获取分页对象
     *
     * @param currentPageStr
     * @param pageSizeStr
     * @return
     */
    @Override
    public Pagination<Radio> showCardList(String currentPageStr, String pageSizeStr) {

        Integer currentPage = Integer.parseInt(currentPageStr);
        Integer pageSize = Integer.parseInt(pageSizeStr);

//        查询数据库 获取数据总条数
        Integer totalCount = radioMapper.getTotalCount();
//        获取总页数
        Integer totalPage = totalCount / pageSize + 1;
//        获取开始位置索引
        Integer begin = (currentPage - 1) * pageSize;
//        获取截至索引位置
        Integer end = begin + pageSize;
//        通过开始和结束索引位置从数据库中查询出对应的Radio集合
        List<Radio> radioList = radioMapper.findPageList(begin, end);

//        给radio中的radio资源与图片资源与七牛云中的可用域名进行拼接 以供用户直接访问
        for (Radio radio : radioList) {
            String radio1 = radio.getRadio();
            String radio2 = IPConstant.url + radio1;
            String cover = radio.getCover();
            String cover2 = IPConstant.url + cover;
            radio.setRadio(radio2);
            radio.setCover(cover2);
        }

//        返回对应的分页对象
        return new Pagination<Radio>(currentPage, pageSize, totalPage, totalCount, radioList);
    }

    /**
     * 点赞数 +1
     *
     * @param lid
     */
    @Override
    public void pointToShowLove(Long lid) {
        radioMapper.pointToShowLove(lid);
    }

    @Override
    public Radio getRadioById(Long id) {
        return radioMapper.getRadioById(id);
    }

    @Override
    public void editRadioInfo(Radio radio) {
        radioMapper.editRadioInfo(radio);
    }

    @Override
    public Pagination<Radio> getRadioByNickname(String nickname) {
        Pagination<Radio> pagination = new Pagination<>();
        pagination.setCurrentPage(1);
        pagination.setPageSize(10);
        pagination.setTotalCount(radioMapper.getTotalCountByNickname(nickname));
        pagination.setPageList(radioMapper.getRadioListByNickname(nickname));
        return pagination;
    }

    @Override
    public List<Radio> getRadioByUid(String id) {
        List<Radio> radioList = radioMapper.getRadioListByUid(id);
        for (Radio radio : radioList) {
            String radio1 = radio.getRadio();
            String radio2 = IPConstant.url + radio1;
            radio.setRadio(radio2);
        }
        return radioList;
    }
}
