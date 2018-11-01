package com.hanmlet.myblog.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleDTO {

	private long userId;
	private String username;
	private String displayName;
	private String email;
	private String mobile;

	private Long articleId;

	private String author;

	private String createDate;

	private String updateDate;

	private String articleTag;

	private String content;

	private String articleTitle;

	private String articleKeyword;

	private Integer favourNum;

	private Integer readNum;

	private Integer supportNum;

	private Integer opposeNum;

	private String status;

	private String summary;

}
