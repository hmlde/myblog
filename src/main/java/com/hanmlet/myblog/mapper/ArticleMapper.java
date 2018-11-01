package com.hanmlet.myblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.po.ArticlePO;

@Mapper
public interface ArticleMapper extends BaseMapper<ArticlePO> {

	int countByAuthor(@Param("author") String author);

	Page<ArticlePO> listArticle(@Param("queryForm") ArticleQueryForm form);

}