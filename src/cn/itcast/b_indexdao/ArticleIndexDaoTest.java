package cn.itcast.b_indexdao;

import java.util.List;

import org.junit.Test;

import cn.itcast._domain.Article;
import cn.itcast._domain.QueryResult;

public class ArticleIndexDaoTest {

	private ArticleIndexDao indexDao = new ArticleIndexDao();

	@Test
	public void testSave() {
		// 准备数据
		Article article = new Article();
		article.setId(1);
		article.setTitle("准备Lucene的开发环境");
		article.setContent("如果信息检索系统在用户发出了检索请求后再去互联网上找答案，根本无法在有限的时间内返回结果。");

		// 放到索引库中
		indexDao.save(article);
	}

	@Test
	public void testSave_25() {
		for (int i = 1; i <= 25; i++) {
			// 准备数据
			Article article = new Article();
			article.setId(i);
			article.setTitle("准备Lucene的开发环境");
			article.setContent("如果信息检索系统在用户发出了检索请求后再去互联网上找答案，根本无法在有限的时间内返回结果。");

			// 放到索引库中
			indexDao.save(article);
		}
	}

	@Test
	public void testDelete() {
		indexDao.delete(1);
	}

	@Test
	public void testUpdate() {
		// 准备数据
		Article article = new Article();
		article.setId(1);
		article.setTitle("准备Lucene的开发环境");
		article.setContent("这是更新后的内容");

		// 更新到索引库中
		indexDao.update(article);
	}

	@Test
	public void testSearch() {
		// 准备查询条件
		String queryString = "lucene";
		// String queryString = "hibernate";

		// 执行搜索
		// QueryResult qr = indexDao.search(queryString, 0, 10000);

		// QueryResult qr = indexDao.search(queryString, 0, 10); // 第1页，每页10条
		// QueryResult qr = indexDao.search(queryString, 10, 10); // 第2页，每页10条
		QueryResult qr = indexDao.search(queryString, 20, 10); // 第3页，每页10条

		// 显示结果
		System.out.println("总结果数：" + qr.getCount());
		for (Article a : (List<Article>) qr.getList()) {
			System.out.println("------------------------------");
			System.out.println("id = " + a.getId());
			System.out.println("title = " + a.getTitle());
			System.out.println("content = " + a.getContent());
		}
	}

}
