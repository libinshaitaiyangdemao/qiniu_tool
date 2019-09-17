package com.bingo.qiniu.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.bingo.qiniu.listener.QFileInputstreamListener;

public class QFileInputstream extends FileInputStream {

	private long curBit;

	private QFileInputstreamListener listener;

	private void fireListener() {
		if (listener != null) {
			listener.read(curBit);
		}
	}

	public void addListener(QFileInputstreamListener listener) {
		this.listener = listener;
	}

	public QFileInputstream(File file) throws FileNotFoundException {
		super(file);
		curBit = 0;
	}

	@Override
	public int read() throws IOException {
		int size = super.read();
		curBit += size;
		fireListener();
		return size;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int size = super.read(b);
		curBit += size;
		fireListener();
		return size;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int size = super.read(b, off, len);
		curBit += size;
		fireListener();
		return size;
	}

}
