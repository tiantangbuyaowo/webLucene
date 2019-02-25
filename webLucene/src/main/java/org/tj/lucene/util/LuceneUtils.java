package org.tj.lucene.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneUtils {
	// 当前目录位置
	// public static final String USERDIR = System.getProperty("user.dir");
	// 存放索引的目录
	private static final String INDEXPATH = "D:" + File.separator + "lucene";
	// 数据源
	// private static final String INDEXSOURCE = USERDIR + File.separator
	// + "source" + File.separator + "lucene.txt";
	// 使用版本
	public static final Version version = Version.LUCENE_5_3_1;

	/**
	 * 获取分词器
	 */
	public static Analyzer getAnalyzer() {
		// 分词器，使用中文分词器
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		// IKAnalyzer5x analyzer = new IKAnalyzer5x();
		return analyzer;
	}
	/**
	 * 获取IndexReader
	 * @author 唐靖
	 * @date 2016年3月1日下午10:00:52
	 * @return
	 * @throws IOException
	 */
	public static IndexReader getIndexReader() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(INDEXPATH));
		IndexReader indexReader = DirectoryReader.open(dir);
		return indexReader;
	}
	/**
	 * 创建一个索引器的操作类
	 * @param openMode
	 * @return
	 * @throws Exception
	 */
	public static IndexWriter getIndexWriter() throws Exception {
		// 索引存放位置设置
		Directory dir = FSDirectory.open(Paths.get(INDEXPATH));
		// 索引配置类设置
		IndexWriterConfig conf = new IndexWriterConfig(getAnalyzer());
		IndexWriter indexWriter = new IndexWriter(dir, conf);
		return indexWriter;
	}
	/***************************************************************************
	 * 创建一个搜索的索引器
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static IndexSearcher createIndexSearcher(IndexReader indexReader) throws CorruptIndexException, IOException {
		IndexSearcher searcher = new IndexSearcher(indexReader);
		return searcher;
	}
	/**
	 * 创建一个查询器
	 * @param filed 在哪些字段上进行查询
	 * @param key 查询内容
	 * @return
	 * @throws ParseException
	 */
	public static Query createQuery(String filed, String key) throws ParseException {
		QueryParser parser = new QueryParser(filed, LuceneUtils.getAnalyzer());
		Query query = parser.parse(key);
		return query;
	}
	/**
	 * 创建一个多字段，多关键字的查询器
	 * @param filed 在哪些字段上进行查询
	 * @param key 查询内容
	 * @return
	 * @throws ParseException
	 */
	public static Query createMultiFieldQuery(String[] filed, String[] key) throws ParseException {
		Query query = MultiFieldQueryParser.parse(key, filed, LuceneUtils.getAnalyzer());
		return query;
	}
	/**
	 * 获取最佳显示内容，比如百度搜索，会出现最符合搜索内容的摘要
	 * @author 唐靖
	 * @date 2016年3月1日下午10:31:38
	 * @param query ：查询器
	 * @param field ：被查询的字段
	 * @param content :总内容
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public static String getBestFragment(Query query, String field, String content)
			throws IOException, InvalidTokenOffsetsException {
		QueryScorer scorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		// 设置关键字的高亮形式
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		TokenStream tokenStream = getAnalyzer().tokenStream(field, new StringReader(content));
		return highlighter.getBestFragment(tokenStream, content);
	}
	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
		System.out.println(LuceneUtils.class.getClassLoader().getResource(""));
		System.out.println(ClassLoader.getSystemResource(""));
		System.out.println(LuceneUtils.class.getResource(""));
		System.out.println(LuceneUtils.class.getResource("/")); // Class文件所在路径
		System.out.println(new File("/").getAbsolutePath());
		System.out.println(System.getProperty("user.dir"));
	}
	/**
	 * 创建索引的数据源
	 * @return
	 */
	public static File createSourceFile() {
		File file = null;
		return file;
	}
}
