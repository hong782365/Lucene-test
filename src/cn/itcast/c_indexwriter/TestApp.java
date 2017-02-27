package cn.itcast.c_indexwriter;

import java.io.File;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import cn.itcast._util.LuceneUtils;

public class TestApp {

	@Test
	public void test() throws Exception {
		Directory directory  = FSDirectory.open(new File("./indexDir"));
		
		IndexWriter indexWriter1 = new IndexWriter(directory, LuceneUtils.getAnalyzer(), MaxFieldLength.LIMITED);
		IndexWriter indexWriter2 = new IndexWriter(directory, LuceneUtils.getAnalyzer(), MaxFieldLength.LIMITED);

	}
}
