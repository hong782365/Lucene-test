package cn.itcast.h_filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.junit.Test;

import cn.itcast._domain.Article;
import cn.itcast._util.ArticleDocumentUtils;
import cn.itcast._util.LuceneUtils;

public class TestApp {

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

		Filter filter = NumericRangeFilter.newIntRange("id", 5, 15, false, true); // null表示没有过滤条件
		TopDocs topDocs = indexSearcher.search(query, filter, 100);

		// ==========================================================================================

		// 3，处理结果
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
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
