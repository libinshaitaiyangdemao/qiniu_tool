package com.bingo.qiniu.process;

import java.util.Arrays;
import java.util.List;

import com.bingo.qiniu.model.QiniuKey;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

public class QiniuProcessor {

	private static final String QUERY_BLACKETS_URL = "http://rs.qbox.me/buckets";

	public List<String> queryBlackets(QiniuKey key) {

		Auth auth = Auth.create(key.getAk(), key.getSk());
		StringMap map = auth.authorization(QUERY_BLACKETS_URL);
		Client client = new Client();
		try {
			Response res = client.get(QUERY_BLACKETS_URL, map);
			String restr = res.bodyString();
			restr = restr.replace("[", "").replace("]", "").replace("\"", "");
			String[] strs = restr.split(",");
			return Arrays.asList(strs);
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		return null;
	}

}
