package com.bingo.qiniu.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.bingo.qiniu.listener.QFileInputstreamListener;
import com.google.gson.Gson;
import com.qiniu.common.Config;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Recorder;
import com.qiniu.storage.model.ResumeBlockInfo;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import com.qiniu.util.UrlSafeBase64;

public class CustomResumeUploader {
	public static final Long BUFFER_SIZE = 1024L * 4096;// 4194304L

	private final String upToken;

	private final String key;

	private final File f;

	private final long size;

	private final StringMap params;

	private final String mime;

	private final String[] contexts;

	private final Client client;

	private final byte[] blockBuffer;

	private final Recorder recorder;

	private final String recorderKey;

	private final long modifyTime;

	private final RecordHelper helper;

	private QFileInputstream file;

	private String host;

	private QFileInputstreamListener inputstreamListener;

	CustomResumeUploader(Client client, String upToken, String key, File file, StringMap params, String mime, Recorder recorder,
			String recorderKey) {
		this.client = client;
		this.upToken = upToken;
		this.key = key;
		this.f = file;
		this.size = file.length();
		this.params = params;
		this.mime = (mime == null ? "application/octet-stream" : mime);
		this.host = Config.zone.upHost;
		long count = (this.size + BUFFER_SIZE - 1L) / BUFFER_SIZE;
		this.contexts = new String[(int) count];
		this.blockBuffer = new byte[BUFFER_SIZE.intValue()];
		this.recorder = recorder;
		this.recorderKey = recorderKey;
		this.modifyTime = this.f.lastModified();
		this.helper = new RecordHelper();
	}

	public void addListener(QFileInputstreamListener listener) {
		this.inputstreamListener = listener;
	}

	public Response upload() throws QiniuException {
		long uploaded = this.helper.recoveryFromRecord();
		try {
			this.file = new QFileInputstream(this.f);
			this.file.addListener(inputstreamListener);
		} catch (FileNotFoundException e) {
			throw new QiniuException(e);
		}
		boolean retry = false;
		int contextIndex = 0;
		int blockSize;
		Response response;
		while (uploaded < this.size) {
			blockSize = nextBlockSize(uploaded);
			try {
				this.file.read(this.blockBuffer, 0, blockSize);
			} catch (IOException e) {
				close();
				throw new QiniuException(e);
			}

			response = null;
			try {
				response = makeBlock(this.blockBuffer, blockSize);
			} catch (QiniuException e) {
				if (e.code() < 0) {
					this.host = Config.zone.upHostBackup;
				}
				if ((e.response == null) || (e.response.needRetry())) {
					retry = true;
				} else {
					close();
					throw e;
				}
			}
			if (retry) {
				try {
					response = makeBlock(this.blockBuffer, blockSize);
					retry = false;
				} catch (QiniuException e) {
					close();
					throw e;
				}
			}

			ResumeBlockInfo blockInfo = (ResumeBlockInfo) response.jsonToObject(ResumeBlockInfo.class);

			this.contexts[(contextIndex++)] = blockInfo.ctx;
			uploaded += blockSize;
			this.helper.record(uploaded);
		}
		close();
		try {
			return makeFile();
		} catch (QiniuException e) {
			try {
				return makeFile();
			} catch (QiniuException e1) {
				throw e1;
			}
		} finally {
			this.helper.removeRecord();
		}
	}

	private Response makeBlock(byte[] block, int blockSize) throws QiniuException {
		String url = new StringBuilder().append(this.host).append("/mkblk/").append(blockSize).toString();
		return post(url, block, 0, blockSize);
	}

	private void close() {
		try {
			this.file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String fileUrl() {
		String url = new StringBuilder().append(this.host).append("/mkfile/").append(this.size).append("/mimeType/")
				.append(UrlSafeBase64.encodeToString(this.mime)).toString();
		final StringBuilder b = new StringBuilder(url);
		if (this.key != null) {
			b.append("/key/");
			b.append(UrlSafeBase64.encodeToString(this.key));
		}
		if (this.params != null) {
			this.params.forEach(new StringMap.Consumer() {
				public void accept(String key, Object value) {
					b.append("/");
					b.append(key);
					b.append("/");
					b.append(value);
				}
			});
		}
		return b.toString();
	}

	private Response makeFile() throws QiniuException {
		String url = fileUrl();
		String s = StringUtils.join(this.contexts, ",");
		return post(url, StringUtils.utf8Bytes(s));
	}

	private Response post(String url, byte[] data) throws QiniuException {
		return this.client.post(url, data,
				new StringMap().put("Authorization", new StringBuilder().append("UpToken ").append(this.upToken).toString()));
	}

	private Response post(String url, byte[] data, int offset, int size) throws QiniuException {
		return this.client.post(url, data, offset, size,
				new StringMap().put("Authorization", new StringBuilder().append("UpToken ").append(this.upToken).toString()),
				"application/octet-stream");
	}

	private int nextBlockSize(long uploaded) {
		if (this.size < uploaded + BUFFER_SIZE) {
			return (int) (this.size - uploaded);
		}
		return BUFFER_SIZE.intValue();
	}

	private class RecordHelper {
		private RecordHelper() {
		}

		long recoveryFromRecord() {
			try {
				return recoveryFromRecord0();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return 0L;
		}

		long recoveryFromRecord0() {
			if (CustomResumeUploader.this.recorder == null) {
				return 0L;
			}
			byte[] data = CustomResumeUploader.this.recorder.get(CustomResumeUploader.this.recorderKey);
			if (data == null) {
				return 0L;
			}
			String jsonStr = new String(data);
			Record r = (Record) new Gson().fromJson(jsonStr, Record.class);
			if ((r.offset == 0L) || (r.modify_time != CustomResumeUploader.this.modifyTime) || (r.size != CustomResumeUploader.this.size)
					|| (r.contexts == null) || (r.contexts.length == 0)) {
				return 0L;
			}
			for (int i = 0; i < r.contexts.length; i++) {
				CustomResumeUploader.this.contexts[i] = r.contexts[i];
			}

			return r.offset;
		}

		void removeRecord() {
			try {
				if (CustomResumeUploader.this.recorder != null)
					CustomResumeUploader.this.recorder.del(CustomResumeUploader.this.recorderKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void record(long offset) {
			try {
				if ((CustomResumeUploader.this.recorder == null) || (offset == 0L)) {
					return;
				}
				String data = new Gson().toJson(new Record(CustomResumeUploader.this.size, offset,
						CustomResumeUploader.this.modifyTime, CustomResumeUploader.this.contexts));
				CustomResumeUploader.this.recorder.set(CustomResumeUploader.this.recorderKey, data.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private class Record {
			long size;

			long offset;

			long modify_time;

			String[] contexts;

			Record() {
			}

			Record(long size, long offset, long modify_time, String[] contexts) {
				this.size = size;
				this.offset = offset;
				this.modify_time = modify_time;
				this.contexts = contexts;
			}
		}
	}
}
