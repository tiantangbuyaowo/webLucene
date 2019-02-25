package org.tj.lucene.service.impl;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.stereotype.Service;
import org.tj.lucene.bean.Content;
import org.tj.lucene.mapper.ContentMapper;
import org.tj.lucene.service.ContentService;
import org.tj.lucene.util.IDUtil;
import org.tj.lucene.util.LuceneUtils;

@Service("contentService")
public class ContentServiceImpl implements ContentService {
	@Resource
	private ContentMapper contentMapper;

	@Override
	public void addContent(Content content) throws Exception {
		content.setId(IDUtil.getId());
		contentMapper.addContent(content);
		// 将内容加入到索引中
		Document document = new Document();
		IndexWriter indexWriter = LuceneUtils.getIndexWriter();
		document.add(new TextField("id", content.getId(), Field.Store.YES));
		document.add(new TextField("title", content.getTitle(), Field.Store.YES));
		document.add(new TextField("content", content.getContent(), Field.Store.NO)); // Field.Store.NO
																						// 内容不会保存到lucene中，即不能从lucene中得到内容，需要到数据库中查询
		indexWriter.addDocument(document);
		indexWriter.close();
	}
	@Override
	public Content getContentById(String id) {
		// TODO Auto-generated method stub
		return contentMapper.getContent(id);
	}
}
