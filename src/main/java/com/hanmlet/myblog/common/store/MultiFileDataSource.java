package com.hanmlet.myblog.common.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.springframework.web.multipart.MultipartFile;

public class MultiFileDataSource implements DataSource {

	private MultipartFile file;

	public MultiFileDataSource(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String getContentType() {
		return this.file.getContentType();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return this.file.getInputStream();
	}

	@Override
	public String getName() {
		return this.file.getOriginalFilename();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
