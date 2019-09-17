package com.bingo.qiniu.upload;

import java.io.File;

import com.qiniu.common.Config;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.RecordKeyGenerator;
import com.qiniu.storage.Recorder;
import com.qiniu.util.StringMap;

public class CustomUploadManager {
	private final Client client;

	private final Recorder recorder;

	private final RecordKeyGenerator keyGen;

	public CustomUploadManager() {
		this(null, null);
	}

	public CustomUploadManager(Recorder recorder) {
		this(recorder, new RecordKeyGenerator() {
			public String gen(String key, File file) {
				return key + "_._" + file.getAbsolutePath();
			}
		});
	}

	public CustomUploadManager(Recorder recorder, RecordKeyGenerator keyGen) {
		this.client = new Client();
		this.recorder = recorder;
		this.keyGen = keyGen;
	}

	private static void checkArgs(String key, byte[] data, File f, String token) {
		String message = null;
		if ((f == null) && (data == null))
			message = "no input data";
		else if ((token == null) || (token.equals(""))) {
			message = "no token";
		}
		if (message != null)
			throw new IllegalArgumentException(message);
	}

	private static StringMap filterParam(StringMap params) {
		final StringMap ret = new StringMap();
		if (params == null) {
			return ret;
		}

		params.forEach(new StringMap.Consumer() {
			public void accept(String key, Object value) {
				if (value == null) {
					return;
				}
				String val = value.toString();
				if ((key.startsWith("x:")) && (!val.equals("")))
					ret.put(key, val);
			}
		});
		return ret;
	}

	public Response put(byte[] data, String key, String token) throws QiniuException {
		return put(data, key, token, null, null, false);
	}

	public Response put(byte[] data, String key, String token, StringMap params, String mime, boolean checkCrc) throws QiniuException {
		checkArgs(key, data, null, token);
		if (mime == null) {
			mime = "application/octet-stream";
		}
		params = filterParam(params);
		return new CustomFormUploader(this.client, token, key, data, params, mime, checkCrc).upload();
	}

	public Response put(String filePath, String key, String token) throws QiniuException {
		return put(filePath, key, token, null, null, false);
	}

	public Response put(String filePath, String key, String token, StringMap params, String mime, boolean checkCrc) throws QiniuException {
		return put(new File(filePath), key, token, params, mime, checkCrc);
	}

	public Response put(File file, String key, String token) throws QiniuException {
		return put(file, key, token, null, null, false);
	}

	public Response put(File file, String key, String token, StringMap params, String mime, boolean checkCrc) throws QiniuException {
		checkArgs(key, null, file, token);
		if (mime == null) {
			mime = "application/octet-stream";
		}
		params = filterParam(params);
		long size = file.length();
		if (size <= Config.PUT_THRESHOLD) {
			return new CustomFormUploader(this.client, token, key, file, params, mime, checkCrc).upload();
		}

		String recorderKey = key;
		if (this.keyGen != null) {
			recorderKey = this.keyGen.gen(key, file);
		}
		CustomResumeUploader uploader = new CustomResumeUploader(this.client, token, key, file, params, mime, this.recorder, recorderKey);

		return uploader.upload();
	}

	public CustomResumeUploader buildResumeUploader(File file, String key, String token, StringMap params, String mime, boolean checkCrc)
			throws QiniuException {
		checkArgs(key, null, file, token);
		if (mime == null) {
			mime = "application/octet-stream";
		}
		params = filterParam(params);
		// long size = file.length();
		// if (size <= Config.PUT_THRESHOLD) {
		// return new CustomFormUploader(this.client, token, key, file,
		// params, mime, checkCrc).upload();
		// }

		String recorderKey = key;
		if (this.keyGen != null) {
			recorderKey = this.keyGen.gen(key, file);
		}
		CustomResumeUploader uploader = new CustomResumeUploader(this.client, token, key, file, params, mime, this.recorder, recorderKey);

		return uploader;
	}
}
