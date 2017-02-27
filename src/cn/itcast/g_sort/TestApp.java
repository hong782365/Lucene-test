package cn.itcast.g_sort;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.junit.Test;

import cn.itcast._domain.Article;
import cn.itcast._util.ArticleDocumentUtils;
import cn.itcast._util.LuceneUtils;

public class TestApp {

	// 建立索引
	@Test
	public void testCreateIndex() throws Exception {
		// 准备数据
		Article article = new Article();
		article.setId(30);
		article.setTitle("准备Lucene的开发环境");
		article.setContent("如果信息检索系统在用户发出了检索请求后再去互联网上找答案，根本无法在有限的时间内返回结果。");

		// 放到索引库中
		// 1, 把Article转为Document
		Document doc = ArticleDocumentUtils.articleToDocument(article);

		doc.setBoost(0.5F); // 1F表示正常得分，大于1表示高分，小于1表示低分

		// 2, 把Document放到索引库中
		LuceneUtils.getIndexWriter().addDocument(doc);
		LuceneUtils.getIndexWriter().commit();
	}

	// 搜索
	@Test
	public void testSearch() throws Exception {
		// 准备查询条件
		String queryString = "lucene";

		// 执行搜索
		List<Article> list = new ArrayList<Article>();

		// 1，把查询字符串转为Query对象（从title和content中查询）
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, new String[] { "title", "content" }, LuceneUtils.getAnalyzer());
		Query query = queryParser.parse(queryString);

		// 2，执行查询，得到中间结果
		IndexSearcher indexSearcher = new IndexSearcher(LuceneUtils.getDirectory()); // 指定所用的索引库
		// TopDocs topDocs = indexSearcher.search(query, 100); // 最多返回前n条结果

		// ========================================================================================== 【创建高亮器】
		// indexSearcher.search(query, n);
		// indexSearcher.search(query, filter, n);
		// indexSearcher.search(query, filter, n, sort);

		// Sort sort = new Sort( new SortField("id", SortField.INT) ); // 按id升序排列
		Sort sort = new Sort(new SortField("id", SortField.INT, true)); // 按id降序排列（true表示降序，false表示升序）

		TopDocs topDocs = indexSearcher.search(query, null, 100, sort);

		// ==========================================================================================

		// 3，处理结果
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			float score = topDocs.scoreDocs[i].score;
			System.out.println("---> score : " + score);

			// 根据编号拿到Document数据
			int docId = topDocs.scoreDocs[i].doc; // Document的内部编号
			Document doc = indexSearcher.doc(docId);
			// 把Document转为Article
			Article article = ArticleDocumentUtils.documentToArticle(doc);
			list.add(article);
		}
		indexSearcher.close();

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
