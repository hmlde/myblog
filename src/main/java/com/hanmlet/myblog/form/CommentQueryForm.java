package com.hanmlet.myblog.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentQueryForm {

	private Long refId;

	private Long articleId;

	private String status;

}
