package com.hanmlet.myblog.controller;

import com.hanmlet.myblog.common.store.MultiFileDataSource;
import com.hanmlet.myblog.dto.StoreDTO;
import com.hanmlet.myblog.service.IStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("attachment")
public class AttachmentController {

	private Logger logger = LoggerFactory.getLogger(AttachmentController.class);

	@Autowired
	private IStoreService storeService;

	@PostMapping("editor/upload")
	@ResponseBody
	public Map<String,Object> upload(MultipartFile[] file) {
		Map<String,Object> result = new HashMap<>();
		List<String> paths = new ArrayList<>();
		for (MultipartFile f : file) {
			try {
				StoreDTO dto = storeService.save(new MultiFileDataSource(f));
				paths.add(dto.getKey());
			} catch (IOException e) {
				logger.error("保存上传的文件异常",e);
			}
		}
		result.put("errno",0);
		result.put("data",paths);

		return result;
	}

}
