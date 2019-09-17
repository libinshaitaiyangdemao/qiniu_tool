package com.bingo.qiniu.model;

import com.bingo.sql.annotation.Id;
import com.bingo.sql.annotation.Table;

@Table("qiniu_key")
public class QiniuKey {

	@Id
	private Integer id;

	private String name;

	private String ak;

	private String sk;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAk() {
		return ak;
	}

	public void setAk(String ak) {
		this.ak = ak;
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ak == null) ? 0 : ak.hashCode());
		result = prime * result + ((sk == null) ? 0 : sk.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QiniuKey other = (QiniuKey) obj;
		if (ak == null) {
			if (other.ak != null)
				return false;
		} else if (!ak.equals(other.ak))
			return false;
		if (sk == null) {
			if (other.sk != null)
				return false;
		} else if (!sk.equals(other.sk))
			return false;
		return true;
	}

}
