package com.hanmlet.myblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.dto.CommentDTO;
import com.hanmlet.myblog.form.CommentQueryForm;
import com.hanmlet.myblog.po.CommentPO;

@Repository
@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {

	Page<CommentDTO> listCommentPg(CommentQueryForm queryForm);

}