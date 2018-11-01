package com.hanmlet.myblog.service;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.dto.CommentDTO;
import com.hanmlet.myblog.form.CommentQueryForm;
import com.hanmlet.myblog.po.CommentPO;

/**
 * 评论查看
 * 
 * @author hanml
 *
 */
public interface ICommentService {

	Page<CommentDTO> listComment(CommentQueryForm queryForm);

	int addComment(CommentPO po);

	int deleteComment(long id);
}
