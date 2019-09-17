package com.bingo.qiniu.listener;

import java.util.Map;

public class QActionEvent {

	private int key;

	private Map<Object, Object> map;

	public QActionEvent(Map<Object, Object> map) {
		this.map = map;
	}

	public Object get(Object key) {
		if (map == null) {
			return null;
		}
		return map.get(key);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

}
