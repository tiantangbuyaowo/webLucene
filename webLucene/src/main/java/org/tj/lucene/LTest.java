package org.tj.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void write() throws IOException {
		WriteIndex index = new WriteIndex();
		index.closeWriter();
	}

	public static void main(String[] args) throws IOException, ParseException {
		// 指定存索引的文件夹
		Directory directory = FSDirectory.open(Paths.get("E:/lucene"));
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher is = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
		QueryParser parser = new QueryParser("contents", analyzer);
		// q是要查询的东西
		String q = "t";
		Query query = parser.parse(q);
		long start = System.currentTimeMillis();
		TopDocs hits = is.search(query, 10);
		long end = System.currentTimeMillis();
		System.out.println("匹配 " + q + " ，总共花费" + (end - start) + "毫秒" + "查询到"
				+ hits.totalHits + "个记录");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
		reader.close();
	}

}
