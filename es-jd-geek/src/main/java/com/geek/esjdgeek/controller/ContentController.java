package com.geek.esjdgeek.controller;

import com.geek.esjdgeek.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author geek
 */
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws IOException {
        Boolean aBoolean = contentService.parseContent(keyword);
        System.out.println("aBoolean = " + aBoolean);
        return aBoolean;
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize
    ) throws IOException {
//        if (pageNo==null)
        return contentService.searchPageHighlight(keyword, pageNo, pageSize);
    }

}
