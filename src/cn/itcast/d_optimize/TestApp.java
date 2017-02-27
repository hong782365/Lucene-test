package cn.itcast.d_optimize;

import org.junit.Test;

import cn.itcast._domain.Article;
import cn.itcast._util.LuceneUtils;
import cn.itcast.b_indexdao.ArticleIndexDao;

public class TestApp {

	// 优化索引库文件（合并多个小文件为一个大文件）
	@Test
	public void test() throws Exception {
		LuceneUtils.getIndexWriter().optimize();
	}

	// 自动合并文件
	@Test
	public void testAuto() throws Exception {
		// 配置当小文件的数量达到多少个后就自动合并为一个大文件，默认为10，最小为2.
		LuceneUtils.getIndexWriter().setMergeFactor(5);

		// 建立索引
		Article article = new Article();
		article.setId(1);
		article.setTitle("准备Lucene的开发环境");
		article.setContent("如果信息检索系统在用户发出了检索请求后再去互联网上找答案，根本无法在有限的时间内返回结果。");
		new ArticleIndexDao().save(article);
	}
}
