package com.bingo.qiniu.model;

import com.bingo.sql.annotation.Id;
import com.bingo.sql.annotation.Table;
import com.qiniu.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: libin37
 * @Date: 2020/2/17 10:45
 * @Description:
 */
@Table("block_info")
public class BlockInfo {
    @Id
    private Integer id;
    private String name;
    private String host;
    private String keys;
    private Integer qiniuKeyId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public List<String> getkeyList() {
        if (StringUtils.isNullOrEmpty(keys)) {
            return null;
        }
        String[] keyArray = keys.split(";");
        return new ArrayList<>(Arrays.asList(keyArray));
    }

    public void setKeyList(List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            return;
        }
        this.keys = keyList.stream().collect(Collectors.joining(";"));
    }

    public Integer getQiniuKeyId() {
        return qiniuKeyId;
    }

    public void setQiniuKeyId(Integer qiniuKeyId) {
        this.qiniuKeyId = qiniuKeyId;
    }
}
