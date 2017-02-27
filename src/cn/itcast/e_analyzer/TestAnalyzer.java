package cn.itcast.e_analyzer;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestAnalyzer {

	@Test
	public void test() throws Exception {
		String enText = "An IndexWriter creates and maintains an index.";
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		testAnalyzer(analyzer, enText);
		
		String cnText = "传智播客准备Lucene的开发环境";
		testAnalyzer(analyzer, cnText); // 单字分词
		
		testAnalyzer(new ChineseAnalyzer(), cnText); // 单字分词
		testAnalyzer(new CJKAnalyzer(Version.LUCENE_30), cnText); // 二分法分词
		testAnalyzer(new IKAnalyzer(), cnText); // 词库分词（重点）
	}

	/**
	 * 使用指定的分词器对指定的文本进行分词，并打印出分出的词
	 * 
	 * @param analyzer
	 * @param text
	 * @throws Exception
	 */
	private void testAnalyzer(Analyzer analyzer, String text) throws Exception {
		System.out.println("当前使用的分词器：" + analyzer.getClass().getSimpleName());
		TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
		tokenStream.addAttribute(TermAttribute.class);
		while (tokenStream.incrementToken()) {
			TermAttribute termAttribute = tokenStream.getAttribute(TermAttribute.class);
			System.out.println(termAttribute.term());
		}
		System.out.println();
	}

}
