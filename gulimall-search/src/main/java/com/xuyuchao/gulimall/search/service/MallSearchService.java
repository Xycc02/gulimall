package com.xuyuchao.gulimall.search.service;

import com.xuyuchao.gulimall.search.vo.SearchParam;
import com.xuyuchao.gulimall.search.vo.SearchResult;

public interface MallSearchService {

    //根据检索条件检索商品
    SearchResult search(SearchParam searchParam);

}
