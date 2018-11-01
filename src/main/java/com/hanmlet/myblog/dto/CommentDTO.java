package com.hanmlet.myblog.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTO {

	private Long commentId;

	private Long refId;

	private Long articleId;

	private String content;

	private String creator;

	private String createDate;

	private String status;

	private String displayName;

	private List<CommentDTO> subCommentList;
}
