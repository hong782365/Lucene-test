package cn.itcast._domain;

import java.util.List;

public class QueryResult {
	private List list; // 一段数据列表
	private int count; // 总记录数

	public QueryResult(List list, int count) {
		this.list = list;
		this.count = count;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
