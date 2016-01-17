package chapter1;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	private IndexWriter writer;

	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir).toPath());
		writer = new IndexWriter(dir, new IndexWriterConfig( new StandardAnalyzer()));
	}

	public static void main(String[] args) throws Exception {
		 String indexDir = "index";
		 String dataDir = "data";
		 long start = System.currentTimeMillis();
		 Indexer indexer = new Indexer(indexDir);
		 int numIndexed;
		 try {
			 numIndexed = indexer.index(dataDir, new TextFilesFilter());
		 } finally {
			 indexer.close();
		 }
		 long end = System.currentTimeMillis();
		 System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
	 }
	
	public void close() throws IOException {
		 writer.close();
	 }

	public int index(String dataDir, FileFilter filter) throws Exception {
		 File[] files = new File(dataDir).listFiles();
		 for (File f: files) {
			 System.out.println(f.toString());
			 if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f))) {
				 indexFile(f);
			 }
		 }
		 return writer.numDocs();
	 }
	
	private static class TextFilesFilter implements FileFilter {
		 public boolean accept(File path) {
			 return path.getName().toLowerCase().endsWith(".txt");
		 }
	 }
			 
	protected Document getDocument(File f) throws Exception {
		Document doc = new Document();
		doc.add(new TextField("contents", new FileReader(f)));
		doc.add(new TextField("filename", f.getName(), Field.Store.YES));
		doc.add(new TextField("fullpath", f.getCanonicalPath(), Field.Store.YES));
		return doc;
	 }
		
	private void indexFile(File f) throws Exception {
		System.out.println("Indexing " + f.getCanonicalPath());
		writer.addDocument(getDocument(f));
	}
}
