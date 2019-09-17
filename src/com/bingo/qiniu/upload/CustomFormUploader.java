package com.bingo.qiniu.upload;

import java.io.File;
import java.io.IOException;

import com.qiniu.common.Config;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.util.Crc32;
import com.qiniu.util.StringMap;

public class CustomFormUploader {
	private final String token;

	private final String key;

	private final File file;

	private final byte[] data;

	private final String mime;

	private final boolean checkCrc;

	private StringMap params;

	private Client client;

	private String fileName;

	CustomFormUploader(Client client, String upToken, String key, byte[] data, StringMap params, String mime, boolean checkCrc) {
		this(client, upToken, key, data, null, params, mime, checkCrc);
	}

	CustomFormUploader(Client client, String upToken, String key, File file, StringMap params, String mime, boolean checkCrc) {
		this(client, upToken, key, null, file, params, mime, checkCrc);
	}

	private CustomFormUploader(Client client, String upToken, String key, byte[] data, File file, StringMap params, String mime,
			boolean checkCrc) {
		this.client = client;
		this.token = upToken;
		this.key = key;
		this.file = file;
		this.data = data;
		this.params = params;
		this.mime = mime;
		this.checkCrc = checkCrc;
	}

	Response upload() throws QiniuException {
		buildParams();
		if (this.data != null) {
			return this.client.multipartPost(Config.zone.upHost, this.params, "file", this.fileName, this.data, this.mime,
					new StringMap());
		}
		return this.client.multipartPost(Config.zone.upHost, this.params, "file", this.fileName, this.file, this.mime, new StringMap());
	}

	private void buildParams() throws QiniuException {
		this.params.put("token", this.token);
		if (this.key == null) {
			this.fileName = "filename";
		} else {
			this.fileName = this.key;
			this.params.put("key", this.key);
		}
		if (this.checkCrc) {
			long crc32 = 0L;
			if (this.file != null)
				try {
					crc32 = Crc32.file(this.file);
				} catch (IOException e) {
					throw new QiniuException(e);
				}
			else {
				crc32 = Crc32.bytes(this.data);
			}
			this.params.put("crc32", "" + crc32);
		}
	}
}
