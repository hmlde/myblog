package com.hanmlet.myblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.base.BaseService;
import com.hanmlet.myblog.dto.CommentDTO;
import com.hanmlet.myblog.form.CommentQueryForm;
import com.hanmlet.myblog.mapper.CommentMapper;
import com.hanmlet.myblog.po.CommentPO;
import com.hanmlet.myblog.service.ICommentService;

@Service
public class CommentService extends BaseService implements ICommentService {

	@Autowired
	private CommentMapper commentMapper;

	@Override
	public Page<CommentDTO> listComment(CommentQueryForm queryForm) {
		return commentMapper.listCommentPg(queryForm);
	}

	@Override
	public int addComment(CommentPO po) {
		po.setCommentId(this.generateId());
		return commentMapper.insertSelective(po);
	}

	@Override
	public int deleteComment(long id) {
		return commentMapper.deleteByPrimaryKey(id);
	}

}
