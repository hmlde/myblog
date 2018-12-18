package com.hanmlet.myblog.mapper;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.po.ArticleRecomment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleRecommentMapper extends BaseMapper<ArticleRecomment> {
    List<ArticleRecomment> queryAtricaleOfReco();
}