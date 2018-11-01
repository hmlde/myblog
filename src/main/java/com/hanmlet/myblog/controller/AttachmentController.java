package com.hanmlet.myblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("attachment")
public class AttachmentController {

	@PostMapping("editor/upload")
	public void upload(MultipartFile[] file) {
		for (MultipartFile f : file) {

		}
	}

}
