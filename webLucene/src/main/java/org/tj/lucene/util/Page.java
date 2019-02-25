package org.tj.lucene.util;

import java.util.List;

/**
 * 基类page中包含公有翻页参数及保存查询到的结果以被页面遍历， 被子类继承后将增加不同的查询条件 。
 * @author David
 */
public class Page<T> {
	/** 每页显示条数默认为30条 */
	public static final int DEFAULT_SIZE = 30;
	/** 一共多少页 */
	private int pageNum;
	/** 每页条数 */
	private int pageSize;
	/** 总条数 */
	private long total;
	/** 当前页数据 */
	private List<T> datas;

	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
	public Page(int pageNum, int pageSize, long total, List<T> datas) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
		this.datas = datas;
	}
	public Page() {
		super();
	}
}
