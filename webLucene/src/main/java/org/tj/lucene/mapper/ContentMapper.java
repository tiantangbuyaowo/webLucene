package org.tj.lucene.mapper;

import org.tj.lucene.bean.Content;

public interface ContentMapper {

	public void addContent(Content content);

	public Content getContent(String id);

}
