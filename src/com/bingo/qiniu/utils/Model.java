package com.bingo.qiniu.utils;

import com.bingo.qiniu.model.BlockInfo;
import com.bingo.qiniu.model.QiniuKey;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Model {

    private static Model instance;

    private List<QiniuKey> keys;
    private List<BlockInfo> blockInfos;
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

    public void setBlockInfos(List<BlockInfo> blockInfos) {
        this.blockInfos = blockInfos;
    }

    public List<BlockInfo> getCurrentBlockInfos() {
        if (blockInfos == null || blockInfos.isEmpty()) {
            return null;
        }
        QiniuKey key = getCurrentKey();
        if (key == null) {
            return null;
        }
        return blockInfos.stream().filter(binfo -> Objects.equals(binfo.getQiniuKeyId(), key.getId())).collect(Collectors.toList());
    }
}
