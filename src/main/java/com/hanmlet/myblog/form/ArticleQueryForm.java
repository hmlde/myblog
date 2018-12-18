package com.hanmlet.myblog.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleQueryForm extends BasePageForm {
	private String searchKey;

	private String author;
}
