package chapter1;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	public static void main(String[] args) throws IllegalArgumentException, IOException, ParseException {
		 String indexDir = "index";
		 String q = "Lucene";
		 search(indexDir, q);
	 }
	
	 public static void search(String indexDir, String q) throws IOException, ParseException {
		 Directory dir = FSDirectory.open(new File(indexDir).toPath());
		 DirectoryReader reader = DirectoryReader.open(dir);
		 IndexSearcher is = new IndexSearcher(reader);
		 QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
		 Query query = parser.parse(q);
		 
		 long start = System.currentTimeMillis();
		 TopDocs hits = is.search(query, 10);
		 long end = System.currentTimeMillis();
		 System.err.printf("Found %d document(s) (in %d milliseconds) that matched query '%s'", hits.totalHits ,end - start, q);
		 
		 for(ScoreDoc scoreDoc : hits.scoreDocs) {
			 Document doc = is.doc(scoreDoc.doc);
			 System.out.println(doc.get("fullpath"));
		 }
		 reader.close();
	 }
}
