package com.hanmlet.myblog.service.impl;

import com.hanmlet.myblog.mapper.ArticleRecommentMapper;
import com.hanmlet.myblog.po.ArticleRecomment;
import com.hanmlet.myblog.service.IArticleRecommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FileName : ArticleRecommentService
 *
 * @author : hanml
 * @date : 2018/11/18 17:38
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
@Service
public class ArticleRecommentService implements IArticleRecommentService {

    @Autowired
    private ArticleRecommentMapper articleRecommentMapper;
    @Override
    public List<ArticleRecomment> queryAtricaleOfReco() {
        return articleRecommentMapper.queryAtricaleOfReco();
    }
}
