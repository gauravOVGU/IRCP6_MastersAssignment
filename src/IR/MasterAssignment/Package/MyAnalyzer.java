package IR.MasterAssignment.Package;
import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public class MyAnalyzer extends StopwordAnalyzerBase {
 
   private Version matchVersion;
   public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
   public MyAnalyzer(Version matchVersion) {
     this.matchVersion = matchVersion;
   }

   public MyAnalyzer() {
	   
	     this.matchVersion = matchVersion;
   }
   
   @Override
   protected TokenStreamComponents createComponents(String fieldName) {
    
     final Tokenizer src = new StandardTokenizer();;
     TokenStream tok = new StandardFilter(src); 
     tok = new LowerCaseFilter(tok);
     tok = new StopFilter(tok,STOP_WORDS_SET);
     return new TokenStreamComponents(src, new PorterStemFilter(tok));
   }
   
   
 }
