package com.hanmlet.myblog.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class ArticlePO {
	private Long articleId;

	private String author;

	private String createDate;

	private String updateDate;

	private String articleTag;

	private String content;

	private String img;

	@JsonIgnore
	private MultipartFile imgFile;

	private String articleTitle;

	private String articleKeyword;

	private Integer favourNum;

	private Integer readNum;

	private Integer supportNum;

	private Integer commentNum;

	private Integer opposeNum;

	private String status;

	private String summary;

}