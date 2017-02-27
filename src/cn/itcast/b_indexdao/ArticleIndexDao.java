package cn.itcast.b_indexdao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;

import cn.itcast._domain.Article;
import cn.itcast._domain.QueryResult;
import cn.itcast._util.ArticleDocumentUtils;
import cn.itcast._util.LuceneUtils;

public class ArticleIndexDao {

	/**
	 * 保存到索引库（建立索引）
	 * 
	 * @param article
	 */
	public void save(Article article) {
		// 1，把Article转为Document
		Document doc = ArticleDocumentUtils.articleToDocument(article);

		// 2，添加到索引库中
		try {
			LuceneUtils.getIndexWriter().addDocument(doc); // 添加
			LuceneUtils.getIndexWriter().commit(); // 提交更改
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除索引
	 * 
	 * Term ：某字段中出现的某一个关键词（在索引库的目录中）
	 * 
	 * @param id
	 */
	public void delete(Integer id) {
		try {
			String idStr = NumericUtils.intToPrefixCoded(id); // 一定要使用Lucene的工具类把数字转为字符串！
			Term term = new Term("id", idStr);

			LuceneUtils.getIndexWriter().deleteDocuments(term); // 删除所有含有这个Term的Document
			LuceneUtils.getIndexWriter().commit(); // 提交更改
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 更新索引
	 * 
	 * @param article
	 */
	public void update(Article article) {
		try {
			Term term = new Term("id", NumericUtils.intToPrefixCoded(article.getId())); // 一定要使用Lucene的工具类把数字转为字符串！
			Document doc = ArticleDocumentUtils.articleToDocument(article);

			LuceneUtils.getIndexWriter().updateDocument(term, doc); // 更新就是先删除再添加
			LuceneUtils.getIndexWriter().commit(); // 提交更改

			// indexWriter.deleteDocuments(term);
			// indexWriter.addDocument(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * * 搜索
	 * 
	 * @param queryString
	 *            查询条件
	 * @param first
	 *            从结果列表的哪个索引开始获取数据
	 * @param max
	 *            最多获取多少条数据（如果没有这么多，就把剩余的都返回）
	 * 
	 * @return 一段数据列表 + 符合条件的总记录数
	 */
	public QueryResult search(String queryString, int first, int max) {
		IndexSearcher indexSearcher = null;
		try {
			// 1，把查询字符串转为Query对象（在title与content中查询）
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, new String[] { "title", "content" }, LuceneUtils.getAnalyzer());
			Query query = queryParser.parse(queryString);

			// 2，执行查询，得到中间结果
			indexSearcher = new IndexSearcher(LuceneUtils.getDirectory());
			TopDocs topDocs = indexSearcher.search(query, first + max); // 最多返回前n条数据，这里要计算好，要返回足够数量的数据
			int count = topDocs.totalHits; // 符合条件的总记录数

			// 3，处理数据
			List<Article> list = new ArrayList<Article>();
			int endIndex = Math.min(first + max, topDocs.scoreDocs.length); // 计算结束的边界

			for (int i = first; i < endIndex; i++) { // 应只取一段数据
				// 根据内部编号获取真正的Document数据
				int docId = topDocs.scoreDocs[i].doc;
				Document doc = indexSearcher.doc(docId);
				// 把Document转换为Article
				Article article = ArticleDocumentUtils.documentToArticle(doc);
				list.add(article);
			}

			// 4，封装结果并返回
			return new QueryResult(list, count);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭IndexSearcher
			if (indexSearcher != null) {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
