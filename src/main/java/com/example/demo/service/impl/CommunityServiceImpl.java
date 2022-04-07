package com.example.demo.service.impl;

import com.example.demo.constants.IPConstant;
import com.example.demo.mapper.CommunityMapper;
import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Community;
import com.example.demo.service.CommunityService;
import com.example.demo.vo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Override
    public Long uploadIdea(Community community) {
        communityMapper.uploadIdea(community);
        return communityMapper.getCommunityId(community.getUuid());
    }

    @Override
    public void storageImg(String hashName, Long cid) {
        communityMapper.storageImg(hashName, cid);
    }

    @Override
    public Pagination<Community> getCommunityPagination(Integer currentPage, Integer pageSize, String tag) {

        Integer totalCount = communityMapper.getTotalCount();

        Integer totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

//        获取开始位置索引
        Integer begin = (currentPage - 1) * pageSize;
//        获取截至索引位置
        Integer end = begin + pageSize;


        List<Community> communityList = null;
//        范围查询
        if (tag == null || "".equals(tag)) {
            communityList = communityMapper.getPagination2(begin, end);
        } else {
            communityList = communityMapper.getPagination(begin, end, tag);
        }

        for (Community community : communityList) {
            String headerImage = community.getHeaderImage();
            String headerImage2 = IPConstant.url + headerImage;
            community.setHeaderImage(headerImage2);
            List<String> imageList = communityMapper.getImageList(community);
            for (int i = 0; i < imageList.size(); i++) {
                imageList.set(i, IPConstant.url + imageList.get(i));
            }
            community.setImageList(imageList);
//            获取动态的评论数量
            Long cid = community.getId();
            Integer commentNum = communityMapper.getCommentNumByCid(cid);
            community.setCommentNum(commentNum);
//            获取动态的评论列表
            List<Comment> commentList = communityMapper.getCommentListByCid(cid);
            community.setCommentList(commentList);
        }


        return new Pagination<Community>(currentPage, pageSize, totalPage, totalCount, communityList);
    }

    @Override
    public Pagination<Community> getPaginationByUid(Long uid, Integer currentPage, Integer pageSize) {

        Integer totalCount = communityMapper.selectCountByUid(uid);

        Integer totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

//        获取开始位置索引
        Integer begin = (currentPage - 1) * pageSize;
//        获取截至索引位置
        Integer end = begin + pageSize;

        List<Community> communityList = communityMapper.getListByUid(begin, end, uid);
        for (Community community : communityList) {
            String headerImage = community.getHeaderImage();
            String headerImage2 = IPConstant.url + headerImage;
            community.setHeaderImage(headerImage2);
            List<String> imageList = communityMapper.getImageList(community);
            for (int i = 0; i < imageList.size(); i++) {
                imageList.set(i, IPConstant.url + imageList.get(i));
            }
            community.setImageList(imageList);
        }


        Pagination<Community> pagination = new Pagination<>();
        pagination.setTotalCount(totalCount);
        pagination.setCurrentPage(currentPage);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(totalPage);
        pagination.setPageList(communityList);

        return pagination;
    }

    @Override
    public void uploadComment(String content, Long cid, Long uid) {
        communityMapper.uploadComment(content, cid, uid);
    }

    @Override
    public Integer getCaredOfNum(Long uid) {
        return communityMapper.getCaredOfNum(uid);
    }
}
