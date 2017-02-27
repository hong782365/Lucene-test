package cn.itcast._util;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneUtils {

	private static Directory directory; // 索引库目录
	private static Analyzer analyzer; // 分词器

	private static IndexWriter indexWriter;

	static {
		try {
			// 这里应是读取配置文件得到的索引库目录
			directory = FSDirectory.open(new File("./indexDir"));
			// analyzer = new StandardAnalyzer(Version.LUCENE_30);
			analyzer = new IKAnalyzer(); // 应使用词库分词
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取全局唯一的IndexWriter对象
	 * 
	 * @return
	 */
	public static IndexWriter getIndexWriter() {
		// 在第一次使用IndexWriter是进行初始化
		if (indexWriter == null) {
			synchronized (LuceneUtils.class) { // 注意线程安全问题
				if (indexWriter == null) {
					try {
						indexWriter = new IndexWriter(directory, analyzer, MaxFieldLength.LIMITED);
						System.out.println("=== 已经初始化 IndexWriter ===");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

			// 指定一段代码，会在JVM退出之前执行。
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						indexWriter.close();
						System.out.println("=== 已经关闭 IndexWriter ===");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

		return indexWriter;
	}

	public static Directory getDirectory() {
		return directory;
	}

	public static Analyzer getAnalyzer() {
		return analyzer;
	}

}
