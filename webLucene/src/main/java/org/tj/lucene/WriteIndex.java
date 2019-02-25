package org.tj.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 创建索引
 * 
 * @author 唐靖
 *
 * @date 2015年12月19日下午9:39:55
 *
 */
public class WriteIndex {

	/**
	 * 索引
	 */
	private IndexWriter indexWriter;

	public WriteIndex() throws IOException {
		/**
		 * 弄个地方存放索引文件,也可以放到内存
		 */
		Directory directory = FSDirectory.open(Paths.get("E:/lucene"));
		// 使用简单的分词器
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		indexWriter = new IndexWriter(directory, conf);
		addDoc();
	}

	/**
	 * 加入文档，这个文档就可以理解为搜索的对象
	 * 
	 * @author 唐靖
	 * @throws IOException
	 * @date 2015年12月19日下午10:05:01
	 *
	 */
	private void addDoc() throws IOException {
		Document document = null;
		File f = new File("E:/data");
		for (File file : f.listFiles()) {
			System.out.println("filename:" + file.getName());
			document = new Document();
			document.add(new LongField("modified", f.lastModified(),
					Field.Store.NO));
			document.add(new TextField("contents", new FileReader(file)));
			document.add(new StringField("path", file.toString(),
					Field.Store.YES));
			indexWriter.addDocument(document);

		}

	}

	/**
	 * 关闭索引
	 * 
	 * @author 唐靖
	 * @throws IOException
	 *
	 * @date 2015年12月19日下午10:02:15
	 *
	 */
	public void closeWriter() throws IOException {
		indexWriter.close();
	}

}
