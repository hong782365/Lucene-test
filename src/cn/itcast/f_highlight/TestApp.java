package cn.itcast.f_highlight;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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

	// 搜索
	@Test
	public void testSearch() throws Exception {
		// 准备查询条件
		String queryString = "互联网";

		// 执行搜索
		List<Article> list = new ArrayList<Article>();

		// 1，把查询字符串转为Query对象（从title和content中查询）
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, new String[] { "title", "content" }, LuceneUtils.getAnalyzer());
		Query query = queryParser.parse(queryString);

		// 2，执行查询，得到中间结果
		IndexSearcher indexSearcher = new IndexSearcher(LuceneUtils.getDirectory()); // 指定所用的索引库
		TopDocs topDocs = indexSearcher.search(query, 100); // 最多返回前n条结果

		// ========================================================================================== 【创建高亮器】
		Query myQuery = query; // 查询条件
		String preTag = "<span class='keyword'>"; // 前缀
		String postTag = "</span>"; // 后缀
		int size = 20; // 摘要大小

		Formatter formatter = new SimpleHTMLFormatter(preTag, postTag); // 前缀、后缀
		Scorer scorer = new QueryScorer(myQuery);
		Highlighter highlighter = new Highlighter(formatter, scorer);
		highlighter.setTextFragmenter(new SimpleFragmenter(size)); // 摘要大小（字数）
		// ==========================================================================================

		// 3，处理结果
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			// 根据编号拿到Document数据
			int docId = topDocs.scoreDocs[i].doc; // Document的内部编号
			Document doc = indexSearcher.doc(docId);

			// ======================================================================================== 【使用高亮器】
			// 一次高亮一个字段，返回高亮后的结果，如果要高亮的字段值中没有出现关键字，就会返回null
			String text = highlighter.getBestFragment(LuceneUtils.getAnalyzer(), "content", doc.get("content"));
			if (text != null) {
				doc.getField("content").setValue(text); // 使用高亮后的文本替换原始内容
			}
			// ========================================================================================

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
