package org.tj.lucene.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tj.lucene.bean.Content;
import org.tj.lucene.service.ContentService;
import org.tj.lucene.util.LuceneUtils;

import com.alibaba.druid.util.StringUtils;

@Controller
@RequestMapping("/content")
public class ContentController {
	@Resource
	private ContentService contentService;

	@RequestMapping("/addContentUI.do")
	public String toAddContentUI() {
		return "addContent";
	}
	@RequestMapping("/addContent.do")
	public String toAddContent(Content content) throws Exception {
		contentService.addContent(content);
		return "redirect:addContentUI.do";
	}
	@RequestMapping("/toSearchUI.do")
	public String toSearchUI(String key, Model model)
			throws CorruptIndexException, IOException, ParseException, InvalidTokenOffsetsException {
		if (null != key && key != "") {
			// 使用lucene搜索
			IndexReader indexReader = LuceneUtils.getIndexReader();
			IndexSearcher indexSearcher = LuceneUtils.createIndexSearcher(indexReader);
			// 单个filed中查询
			// Query query = LuceneUtils.createQuery("content", key); // 简单搜索
			String[] filed = new String[] { "title", "content" };
			String[] keys = new String[] { key, key };
			// 多个filed
			Query query = LuceneUtils.createMultiFieldQuery(filed, keys);
			TopDocs hits = indexSearcher.search(query, 10); // 查十条
			// 遍历结果
			List<Content> contents = new ArrayList<Content>();
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = indexSearcher.doc(scoreDoc.doc);
				Content content = contentService.getContentById(doc.get("id"));
				String c = LuceneUtils.getBestFragment(query, "content", content.getContent());
				if (!StringUtils.isEmpty(c)) {
					content.setContent(c);
				}
				String t = LuceneUtils.getBestFragment(query, "title", content.getTitle());
				if (!StringUtils.isEmpty(t)) {
					content.setTitle(t);
				}
				contents.add(content);
			}
			model.addAttribute("contents", contents);
			model.addAttribute("key", key);
			indexReader.close();
		}
		return "searchUI";
	}
}
