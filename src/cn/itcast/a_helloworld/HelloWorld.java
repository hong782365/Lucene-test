package cn.itcast.a_helloworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import cn.itcast._domain.Article;

public class HelloWorld {

	private static Directory directory; // 索引库目录
	private static Analyzer analyzer; // 分词器

	static {
		try {
			directory = FSDirectory.open(new File("./indexDir"));
			analyzer = new StandardAnalyzer(Version.LUCENE_30);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// 建立索引
	@Test
	public void testCreateIndex() throws Exception {
		// 准备数据
		Article article = new Article();
		article.setId(2);
		article.setTitle("准备Lucene的开发环境");
		article.setContent("如果信息检索系统在用户发出了检索请求后再去互联网上找答案，根本无法在有限的时间内返回结果。");

		// 放到索引库中
		// 1, 把Article转为Document
		Document doc = new Document();
		String idStr = article.getId().toString();
		doc.add(new Field("id", idStr, Store.YES, Index.ANALYZED));
		doc.add(new Field("title", article.getTitle(), Store.YES, Index.ANALYZED));
		doc.add(new Field("content", article.getContent(), Store.NO, Index.ANALYZED));

		// 2, 把Document放到索引库中
		IndexWriter indexWriter = new IndexWriter(directory, analyzer,  MaxFieldLength.UNLIMITED);
		indexWriter.addDocument(doc);
		indexWriter.close();
	}

	// 搜索
	@Test
	public void testSearch() throws Exception {
		// 准备查询条件
		String queryString = "lucene";
		// String queryString = "hibernate";

		// 执行搜索
		List<Article> list = new ArrayList<Article>();

		// ==========================================================================================

		// 1，把查询字符串转为Query对象（默认只从title中查询）
		QueryParser queryParser = new QueryParser(Version.LUCENE_30, "title", analyzer);
		Query query = queryParser.parse(queryString);

		// 2，执行查询，得到中间结果
		IndexSearcher indexSearcher = new IndexSearcher(directory); // 指定所用的索引库
		TopDocs topDocs = indexSearcher.search(query, 100); // 最多返回前n条结果

		int count = topDocs.totalHits;
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;

		// 3，处理结果
		for (int i = 0; i < scoreDocs.length; i++) {
			ScoreDoc scoreDoc = scoreDocs[i];
			float score = scoreDoc.score; // 相关度得分
			int docId = scoreDoc.doc; // Document的内部编号

			// 根据编号拿到Document数据
			Document doc = indexSearcher.doc(docId);

			// 把Document转为Article
			String idStr = doc.get("id"); //
			String title = doc.get("title");
			String content = doc.get("content"); // 等价于 doc.getField("content").stringValue();

			Article article = new Article();
			article.setId(Integer.parseInt(idStr));
			article.setTitle(title);
			article.setContent(content);

			list.add(article);
		}
		indexSearcher.close();

		// ==========================================================================================

		// 显示结果
		System.out.println("总结果数：" + list.size());
		for (Article a : list) {
			System.out.println("------------------------------");
			System.out.println("id = " + a.getId());
			System.out.println("title = " + a.getTitle());
			System.out.println("content = " + a.getContent());
		}
	}
}
