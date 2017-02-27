package cn.itcast._util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.util.NumericUtils;

import cn.itcast._domain.Article;

public class ArticleDocumentUtils {

	/**
	 * 把Article转为Document
	 * 
	 * @param article
	 * @return
	 */
	public static Document articleToDocument(Article article) {
		Document doc = new Document();

		String idStr = NumericUtils.intToPrefixCoded(article.getId()); // 一定要使用Lucene的工具类把数字转为字符串！
		
		doc.add(new Field("id", idStr, Store.YES, Index.NOT_ANALYZED)); // 注意：唯一标示符一般选择Index.NOT_ANALYZED
		doc.add(new Field("title", article.getTitle(), Store.YES, Index.ANALYZED));
		doc.add(new Field("content", article.getContent(), Store.YES, Index.ANALYZED));

		return doc;
	}

	/**
	 * 把Document转为Article
	 * 
	 * @param doc
	 * @return
	 */
	public static Article documentToArticle(Document doc) {
		Article article = new Article();
		
		Integer id = NumericUtils.prefixCodedToInt(doc.get("id")); // 一定要使用Lucene的工具类把字符串转为数字！
		
		article.setId(id);
		article.setTitle(doc.get("title"));
		article.setContent(doc.get("content"));
		
		return article;
	}

}
