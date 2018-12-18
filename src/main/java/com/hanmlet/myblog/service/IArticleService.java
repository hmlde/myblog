package com.hanmlet.myblog.service;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.dto.ArticleDTO;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.po.ArticlePO;

import java.io.IOException;

public interface IArticleService {

	int saveOrUpdate(ArticlePO articlePO);

	int del(long id);

	Page<ArticleDTO> listPage(ArticleQueryForm queryForm);

	ArticleDTO select(long id);

	int countByAuthor(String author);
}
