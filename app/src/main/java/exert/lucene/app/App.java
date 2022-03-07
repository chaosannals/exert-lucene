package exert.lucene.app;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        try (Directory dir = FSDirectory.open(Paths.get("demodb"))) {
            SmartChineseAnalyzer cna = new SmartChineseAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(cna);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            iwc.setCommitOnClose(true);

            System.out.println("insert");
            try (IndexWriter writer = new IndexWriter(dir, iwc)) {
                Document doc = newDoc();
                writer.addDocument(doc);
            }

            System.out.println("search");
            try (DirectoryReader reader = DirectoryReader.open(dir)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                QueryParser parser = new QueryParser("contents", cna);
                Query query = parser.parse("测试");
                ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;
                for (int i = 0; i < hits.length; ++i) {
                    Document d = searcher.doc(hits[i].doc);
                    System.out.println(d.get("contents"));
                }
            }

            System.out.println("end");
        }
    }

    public static Document newDoc() {
        Document doc = new Document();
        doc.add(new Field("contents", "测试内容", TextField.TYPE_STORED));
        return doc;
    }
}
