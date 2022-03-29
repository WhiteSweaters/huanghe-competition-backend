package com.example.demo.service;

import com.example.demo.pojo.Community;
import com.example.demo.vo.Pagination;

public interface CommunityService {
    Long uploadIdea(Community community);

    void storageImg(String hashName, Long cid);

    Pagination<Community> getCommunityPagination(Integer currentPage, Integer pageSize, String tag);

    Pagination<Community> getPaginationByUid(Long uid, Integer currentPage, Integer pageSize);

    void uploadComment(String content, Long cid, Long uid);

    Integer getCaredOfNum(Long uid);
}
