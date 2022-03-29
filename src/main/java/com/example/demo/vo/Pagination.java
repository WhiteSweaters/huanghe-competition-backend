package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

//分页对象
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pagination<T> {
//    当前页
    private Integer currentPage;
//    页大小
    private Integer pageSize;
//    总页数
    private Integer totalPage;
//    数据条数
    private Integer totalCount;
//    返回的T对象的数据集合
    private List<T> pageList;
}
