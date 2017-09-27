/**
 * 
 */
package IR.MasterAssignment.Package;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.SystemException;










import java.io.InputStreamReader;

public class Crawler{
	  
	  private Crawler() {}
	 

	  /** Index all text files under a directory. */
	  public static boolean crawl(String[] indexArgs, Boolean AvdSch) {
		  
		String indexPath = System.getProperty("user.dir")+"\\index\\";
		
		String URLPath = null;
	    boolean create = true;
	    int recursionDepth=0,counter=0;
	    
	    for(int i=0;i<indexArgs.length;i++) {
	      if ("-index".equals(indexArgs[i])) {
	        if(!indexArgs[i+1].isEmpty())
	        	indexPath = indexPath + indexArgs[i+1];
	        i++;
	      } else if ("-URLPath".equals(indexArgs[i])) {
	    	  URLPath = indexArgs[i+1];
	        i++;
	      } else if ("-recursionDepth".equals(indexArgs[i])) {
	        recursionDepth = Integer.parseInt(indexArgs[i+1]);
	        i++;
	      }
	    }


	   	    
	    Date start = new Date();
	    try {
	      System.out.println("Indexing to directory '" + indexPath + "'...");

	      Directory dir = FSDirectory.open(Paths.get(indexPath));
	      Analyzer analyzer = new MyAnalyzer();
	      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

	      if (create) {
	        // Create a new index in the directory, removing any
	        // previously indexed documents:
	        iwc.setOpenMode(OpenMode.CREATE);
	      } else {
	        // Add new documents to an existing index:
	        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	      }
	      IndexWriter writer = new IndexWriter(dir, iwc);
	      URL parentUrl = new URL(URLPath);
	      /* Calling recursive function to crawl */
	      crawlRecursive(parentUrl,recursionDepth , counter,writer, AvdSch);
	      writer.close();
	      Date end = new Date();
	     System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	     return true;
	    } catch (IOException e) {
	    	return false;
	    }
	  }

	  

		public static void crawlRecursive(URL parentUrl, int recursionDepth, int count,IndexWriter writer, boolean AvdSch)
				throws IOException, MalformedURLException {

			String parentBaseUrl = parentUrl.getHost();
			String toBeCrawled;
			String toBeCrawledBaseURL;
			try {
				org.jsoup.nodes.Document doc = Jsoup.connect(parentUrl.toString())
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
							
						.referrer("http://www.google.com")//.timeout(1000 * 3)
						.get();
				Elements htmlBody = doc.getElementsByTag("body");
				Elements htmlTitle = doc.getElementsByTag("title");
				Elements linksInPage = doc.select("a[href]");
				/* Get all the links in the page */
				Elements links = doc.getElementsByTag("a");
				/* Call function to find representative image of the parentURL */
				Elements images = doc.select("img[src]");
				System.out.println(images.size()+" images found");
				String urlToRepresentativeImgOnPage = findRepresentativeImageByAnalyzingImageOnPage(parentUrl, images,AvdSch);
				System.out.println("Image Selected"+urlToRepresentativeImgOnPage);
				/* Index this URL */

				indexURL(htmlBody, htmlTitle,urlToRepresentativeImgOnPage, parentUrl.toString(),writer);

				
				  for(int x = 0 ; x < count ; x++){ System.out.print("\t"); }
				  System.out.println(parentUrl);
				 

				/*
				 * Check whether the base url of the link is same as base url
				 * provided by user if the base urls are same then skip the links
				 * and proceed crawling with others
				 */

				for (org.jsoup.nodes.Element linkEle : linksInPage) {
					toBeCrawled = linkEle.attr("href");
					try {
						toBeCrawledBaseURL = new URL(toBeCrawled).getHost();

						if (!toBeCrawledBaseURL.equals(parentBaseUrl)) {
							/* Stop crawling if recursion depth is zero */
							if (recursionDepth != 0) {
								crawlRecursive(new URL(toBeCrawled), recursionDepth - 1, count + 1,writer, AvdSch);
							}
						}
					} catch (MalformedURLException e) {
						e.getMessage();
					}

				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (HttpStatusException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

		}
		
		public static void indexURL(Elements htmlBody, Elements htmlTitle,String urlToRepresentativeImg, String parentURL,IndexWriter writer) {
			//Create a document for indexing webpages8/
			org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
			docLucene.add(new StringField("parentUrl", parentURL, Field.Store.YES));
			docLucene.add(new StringField("imageUrl", urlToRepresentativeImg, Field.Store.YES));
			docLucene.add(new TextField("contents", htmlBody.text() + htmlTitle.text(),Field.Store.YES));
		    docLucene.add(new TextField("title", htmlTitle.text(),Field.Store.YES));
		
			
			if(!htmlBody.text().trim().equals("")){
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				try{
					writer.updateDocument(new Term("parentUrl",parentURL), docLucene);
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			}
			
		}
		
		public static String findRepresentativeImageByAnalyzingImageOnPage(URL parentURL, Elements images, Boolean AvdSch) throws MalformedURLException{
			int resolution=0;
			String AvdSchUrl = null;
			String imgURL, extensionStr, parentNodeTag, parentHrefValue;
			URL imageURL;
			boolean isImage = false;
			int lastIndexOfPoint;
			/*
			 * looping through all image elements to find the most representative
			 * image of the site
			 */
			for (org.jsoup.nodes.Element link : images) {
				
				imgURL = link.attr("src");
				// Check if the image tag scr attribute ends with .png .jpg .jpeg*/
				System.out.println(imgURL);
				
				imageURL = new URL(parentURL,imgURL);
				lastIndexOfPoint = imgURL.lastIndexOf(".");
				 
				if(lastIndexOfPoint > 0){
				extensionStr = imgURL.substring(lastIndexOfPoint);
				switch (extensionStr) {
				case ".jpg":
					isImage = true;
					break;
				case ".png":
					isImage = true;
					break;
				case ".jpeg":
					isImage = true;
					break;
				case ".gif":
					isImage = true;
					break;
				}
				if(isImage && AvdSch)
				{	
				ImageIcon image = new ImageIcon(new URL(imageURL.toString()));
				System.out.println("Downloading image:"+imageURL.toString());
				if(resolution<=image.getIconHeight()*image.getIconWidth() )
				{
					AvdSchUrl=imageURL.toString();
					resolution=image.getIconHeight()*image.getIconWidth();
				}
				}				
				/*
				 * Check if the parent tag of IMG tag is a(link) and its href
				 * attribute value points to base URL of the link
				 */
				parentNodeTag = link.parent().tagName();
				if (parentNodeTag.equalsIgnoreCase("a")) {
					
					parentHrefValue = link.parentNode().attr("href");
					for (PossibleLogoStrToIgnore strno : PossibleLogoStrToIgnore.values()) {
						for (PossibleLogoSubStr str : PossibleLogoSubStr.values()) {
							if (imgURL.contains(str.toString()) && (!imgURL.contains(strno.toString()))) {
								/*
								 * found representative image return to the calling
								 * function
								 */
								return imageURL.toString();
							}
						}
				}
				}
			}
			}
			if(AvdSch)
			{
			return AvdSchUrl;
			}
			else
			{
				return "";
			}
		}
		enum PossibleLogoSubStr{
			logo,LOGO,index,header_logo,header,main
		}
		enum PossibleLogoStrToIgnore{
			footer,lang,en_logo,logo_en
		}
}