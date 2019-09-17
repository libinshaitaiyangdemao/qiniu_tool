package com.bingo.qiniu.utils;

import java.util.List;

import com.bingo.qiniu.model.QiniuKey;

public class Model {

	private static Model instance;

	private List<QiniuKey> keys;

	private QiniuKey currentKey;

	private Model() {
	}

	public static Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public List<QiniuKey> getKeys() {
		return keys;
	}

	public void setKeys(List<QiniuKey> keys) {
		this.keys = keys;
		getCurrentKey();
	}

	public QiniuKey getCurrentKey() {
		if (currentKey == null && keys != null && !keys.isEmpty()) {
			currentKey = keys.get(0);
		}
		return currentKey;
	}

	public void setCurrentKey(QiniuKey currentKey) {
		this.currentKey = currentKey;
	}

}
