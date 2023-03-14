package com.xuyuchao.gulimall.search.controller;

import com.xuyuchao.gulimall.search.service.MallSearchService;
import com.xuyuchao.gulimall.search.vo.SearchParam;
import com.xuyuchao.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: SearchController
 * @Author XYC
 * @Date: 2023/3/13 16:02
 * @Version 1.0
 */

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model) {
        SearchResult result = mallSearchService.search(searchParam);

        model.addAttribute(result);
        return "list";
    }


}
