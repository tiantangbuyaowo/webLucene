package org.tj.lucene.service;

import org.tj.lucene.bean.Content;

public interface ContentService {

	void addContent(Content content) throws Exception;

	Content getContentById(String string);

}
