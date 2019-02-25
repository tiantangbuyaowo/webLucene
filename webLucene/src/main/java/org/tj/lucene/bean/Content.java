package org.tj.lucene.bean;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 文章实体bean
 * @author 唐靖
 * @date 2016年2月25日下午10:11:21
 */
public class Content implements Serializable {
	private static final long serialVersionUID = 1L;
	@Field
	private String id;
	@Field
	private String title;
	@Field
	private String content;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
