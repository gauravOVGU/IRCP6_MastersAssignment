/**
 * 
 */
package IR.MasterAssignment.Package;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;

import java.awt.Color;

import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.ScrollPaneConstants;
import java.awt.Checkbox;

/**
 * @author Prash
 *
 */
public class CrawlerGui {

	/**
	 * @param args
	 */

	private JFrame frame;
	private JTextField textField;
	private JTextField pathVar;
	private String orignalQuery;
	private JTextField queryStr;
	private JTable searchTable;
	private PrintStream sysOut ;
	private PrintStream sysErr;
	private PrintStream printStream;
	private JTextArea textArea;
	private JTextField newIndexName;
	private JSpinner spinner;
	private JTextField indexPathField;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CrawlerGui window = new CrawlerGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CrawlerGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(240, 240, 240));
		frame.setTitle("URL Crawler");
		frame.setBounds(100, 100, 1314, 783);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(33, 38, 1251, 685);
		frame.getContentPane().add(tabbedPane);
		/*Redirecting SOP outputs to JTextArea*/
		sysOut = System.out; //Backup of System.out
		sysErr = System.err; //Backup of System.err
		System.setOut(printStream);
		System.setErr(printStream);
		
		JPanel CrawlURL = new JPanel();
		CrawlURL.setBackground(Color.WHITE);
		tabbedPane.addTab("Create Index", null, CrawlURL, null);
		CrawlURL.setLayout(null);
		CrawlURL.setBounds(33, 38,  1204, 557);
		
		JLabel Path = new JLabel("URL:");
		Path.setBounds(12, 23, 83, 16);
		CrawlURL.add(Path);
		
		pathVar = new JTextField();
		pathVar.setToolTipText("Enter the URL to be indexed.");
		pathVar.setBounds(96, 19, 692, 25);
		CrawlURL.add(pathVar);
		pathVar.setColumns(10);
		final Checkbox checkbox = new Checkbox("Advance Image Search");
		checkbox.setBounds(738, 62, 152, 22);
		checkbox.setVisible(false);
		CrawlURL.add(checkbox);
		final JRadioButton rdbtnYes = new JRadioButton("Yes");
		
		rdbtnYes.setBounds(131, 62, 50, 25);
		CrawlURL.add(rdbtnYes);
		
		final JRadioButton rdbtnNo = new JRadioButton("No");
		rdbtnNo.setSelected(true);
		rdbtnNo.setBounds(183, 62, 67, 25);
		CrawlURL.add(rdbtnNo);
		
		ButtonGroup newIndexYesOrNo = new ButtonGroup();
		newIndexYesOrNo.add(rdbtnYes);
		newIndexYesOrNo.add(rdbtnNo);
		
		JButton btnStartIndexing = new JButton("Create Index");
		btnStartIndexing.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\Resources\\create.png"));
		btnStartIndexing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.setOut(printStream);
				System.setErr(printStream);
				
				if(rdbtnYes.isSelected() && newIndexName.getText().trim().equals("")){
					JOptionPane.showMessageDialog(null,"Please enter New Index name! (Note : The name should contain only alphabets and length should be between 4 to 8 characters)");
					return;
				}	
				
				if(!pathVar.getText().trim().equals("")){
					
					final String [] docsPath = new String [] {"-URLPath" ,pathVar.getText(),"-index",newIndexName.getText(),"-recursionDepth",spinner.getValue().toString()};
					(new Thread(){
						public void run(){
								Boolean success = false;
								boolean AvdScr = checkbox.getState();
								success=Crawler.crawl(docsPath,AvdScr);
								if(success)
								{   
									JOptionPane.showMessageDialog(null, "Index Successfully created");
								}
								
						}
					}).start();
				}
				else
					JOptionPane.showMessageDialog(null, "Please enter the URL!");
			}
		});
		btnStartIndexing.setBounds(809, 14, 140, 35);
		CrawlURL.add(btnStartIndexing);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(27, 144, 476, -6);
		CrawlURL.add(separator);
		
		JScrollPane scrollPaneForTextArea = new JScrollPane();
		scrollPaneForTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneForTextArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneForTextArea.setBounds(6, 91, 1240, 564);
		CrawlURL.add(scrollPaneForTextArea);
		
		JLabel lblCreateNewIndex = new JLabel("Create New Index?");
		lblCreateNewIndex.setBounds(12, 62, 118, 25);
		CrawlURL.add(lblCreateNewIndex);
		
		final JLabel lblNewIndexName = new JLabel("Index name");
		lblNewIndexName.setBounds(274, 63, 108, 25);
		lblNewIndexName.setVisible(false);
		CrawlURL.add(lblNewIndexName);
		
		newIndexName = new JTextField();
		newIndexName.setBounds(359, 65, 116, 22);
		newIndexName.setVisible(false);
		CrawlURL.add(newIndexName);
		newIndexName.setColumns(10);
		
		final JLabel lblRecursionDepth = new JLabel("Crawler Depth");
		lblRecursionDepth.setBounds(500, 62, 118, 25);
		lblRecursionDepth.setVisible(false);
		CrawlURL.add(lblRecursionDepth);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), new Integer(10), new Integer(1)));
		spinner.setBounds(617, 64, 80, 22);
		spinner.setVisible(false);
		CrawlURL.add(spinner);
		textArea = new JTextArea();
		textArea.setBounds(10, 93, 1236, 560);
		CrawlURL.add(textArea);
		textArea.setEditable(false);
		printStream = new PrintStream(new CustomOutputStream(textArea));
		
		
		
		
		rdbtnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.setOut(printStream);
				System.setErr(printStream);
				
				if(rdbtnYes.isSelected()){
					newIndexName.setVisible(true);
					lblRecursionDepth.setVisible(true);
					lblNewIndexName.setVisible(true);
					spinner.setVisible(true);
					checkbox.setVisible(true);
					return;
				}		
			}
		});
		rdbtnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.setOut(printStream);
				System.setErr(printStream);
				
				if(rdbtnNo.isSelected()){
					newIndexName.setVisible(false);
					lblRecursionDepth.setVisible(false);
					lblNewIndexName.setVisible(false);
					spinner.setVisible(false);
					checkbox.setVisible(true);
					return;
				}		
			}
		});
		JPanel SearchIndex = new JPanel();
		SearchIndex.setBackground(new Color(255, 255, 255));
		tabbedPane.addTab("Search Index", null, SearchIndex, null);
		SearchIndex.setLayout(null);
		
		JLabel lblQuery = new JLabel("Query :");
		lblQuery.setBounds(12, 29, 56, 16);
		SearchIndex.add(lblQuery);
		
		queryStr = new JTextField();
		queryStr.setBounds(65, 26, 438, 22);
		SearchIndex.add(queryStr);
		queryStr.setColumns(10);
		
		JButton btnStartSearch = new JButton("Start Search");
		btnStartSearch.setForeground(new Color(0, 128, 0));
		btnStartSearch.setBackground(new Color(0, 128, 0));
		btnStartSearch.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\Resources\\search.png"));
		btnStartSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				/*Remove the existing rows in the table*/
				DefaultTableModel tableModle = (DefaultTableModel) searchTable.getModel();
				if(tableModle.getRowCount() != 0){
						tableModle.setRowCount(0);
				}
				
				String [] searchArgs;
				System.setErr(sysErr);
				System.setOut(sysOut);
				orignalQuery=queryStr.getText();
				if(queryStr.getText().trim().isEmpty() || indexPathField.getText().trim().isEmpty()){
					JOptionPane.showMessageDialog(null, "Please enter query and index to be searched!");
				}
				if(!queryStr.getText().trim().isEmpty() && !indexPathField.getText().trim().isEmpty()){
					
					searchArgs = new String [] {"-index",indexPathField.getText(),"-query",queryStr.getText()};
											
							try {
								ScoreDoc[] scoreDocs = IR.MasterAssignment.Package.SearchFiles.searchIndex(searchArgs);
								if(scoreDocs.length > 0){
									System.out.println(scoreDocs.length);
							    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPathField.getText())));
							    IndexSearcher searcher = new IndexSearcher(reader);
							    System.out.println(scoreDocs.length);
							    MyAnalyzer analyser = new MyAnalyzer();
							    QueryParser parser = new QueryParser("contents", analyser);
							    Query query = parser.parse(queryStr.getText());
							    System.out.println(scoreDocs.length);
					           
					            int higherBound = (10 > scoreDocs.length) ?scoreDocs.length:10;
							     
							     for(int i = 0; i < higherBound ; i++){
							    	 Document doc = searcher.doc(scoreDocs[i].doc);
							         String pageUrl = doc.get("parentUrl");
							         String summary = doc.get("contents");
							         String relevance = String.valueOf(scoreDocs[i].score);
							         String title = doc.get("title");
							         String imageUrl = doc.get("imageUrl");
							         System.out.println("Image Found"+imageUrl);
							         System.out.println(scoreDocs.length);
									
							         tableModle.addRow(new Object[] {String.valueOf(i+1),title,pageUrl,summary,relevance,"Image:"+imageUrl});
							         searchTable.getColumnModel().getColumn(3).setCellRenderer(getRenderer());
							         searchTable.getColumnModel().getColumn(5).setCellRenderer(getRenderer());
								         
							     }
							
								}
								else
									JOptionPane.showMessageDialog(null, "No Relevant Documents found!");
							} catch (Exception exp) {
								exp.printStackTrace();
							}
					}
			}
		});
		btnStartSearch.setBounds(513, 20, 140, 35);
		SearchIndex.add(btnStartSearch);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 126, 1246, 529);
		SearchIndex.add(scrollPane);
		
		
        String[] columnNames = {"Rank",
        		"Title",
                "URL",
                "Summary",
                "Relevance",
                "Image"};
        Object[][] data = {        };
       searchTable = new JTable(new DefaultTableModel(data,columnNames)){
    	   
    	   public boolean isCellEditable(int row, int column){ 
    			return false;
    		}
       };
       
       searchTable.setForeground(Color.DARK_GRAY);
       searchTable.setCellSelectionEnabled(true);
      searchTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		/*Resizing the columns*/
		searchTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		searchTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		searchTable.getColumnModel().getColumn(2).setPreferredWidth(115);
		searchTable.getColumnModel().getColumn(3).setPreferredWidth(400);
		searchTable.getColumnModel().getColumn(4).setPreferredWidth(80);
		searchTable.getColumnModel().getColumn(5).setPreferredWidth(400);
		searchTable.setRowHeight(175);

		
		scrollPane.setViewportView(searchTable);
		
		JButton btnStop = new JButton("Cancel");
		btnStop.setForeground(new Color(255, 0, 0));
		btnStop.setBackground(new Color(255, 0, 0));
		btnStop.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\Resources\\cancel.png"));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnStop.setBounds(683, 20, 140, 35);
		SearchIndex.add(btnStop);
		
		JLabel lblIndexToBe = new JLabel("Index to be searched");
		lblIndexToBe.setBounds(12, 79, 127, 25);
		SearchIndex.add(lblIndexToBe);
		
		indexPathField = new JTextField();
		indexPathField.setBounds(142, 80, 495, 22);
		SearchIndex.add(indexPathField);
		indexPathField.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\Resources\\browse.png"));
		btnBrowse.setBackground(Color.WHITE);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String indexPath;
				try{
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("./index"));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(!indexPathField.getText().equals(null)){
					File pwd = new File(indexPathField.getText()); 
					fc.setCurrentDirectory(pwd);
				}
				int returnVal = fc.showOpenDialog(tabbedPane);
				File currDir = fc.getSelectedFile();
				
				indexPath = currDir.getPath();
				indexPathField.setText(indexPath);
				}
				catch(Exception exp)
				{
					exp.printStackTrace();
				}
			}
		});
		btnBrowse.setBounds(683, 74, 140, 35);
		SearchIndex.add(btnBrowse);
	}
	public String getSummary(String content, String Query)
	{
		content=content.replaceAll("<[^>]*>", "");
		content=content.replaceAll(",", ". ");
		content=content.replaceAll(":", ". ");
		content=content.replaceAll(";", ". ");
		content=content.toUpperCase();
		Query=Query.toUpperCase();
		StringBuffer summary = new StringBuffer();
		int counter = 0;
		String[] sentences = content.split(Pattern.quote("."));
		if(content.contains(Query))
		{
			for(int i=0;i<sentences.length;i++)
			{
				if(sentences[i].contains(Query))
				{
					summary.append(sentences[i]);
					summary.append("...");
					counter++;
					if(counter>=3)
						break;
				}
			}
		}
		else {
			return content;
		}
		return summary.toString();
	}
	private TableCellRenderer getRenderer() {
        return new TableCellRenderer() {
        	

            @Override
            public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
            	JLabel  lbl = new JLabel(new ImageIcon(System.getProperty("user.dir")+"\\Resources\\html.png"));
            	if(arg1.toString().contains("Image:"))
            	{
            		try {
            			if(arg1.toString().contains("http")){
            				BufferedImage img = ImageIO.read(new URL(arg1.toString().substring(arg1.toString().indexOf("http"))));
            				if(img!=null){
            				img=scaleImage(img, BufferedImage.TYPE_INT_ARGB, 400, 175);
            				lbl=new JLabel(new ImageIcon((Image)img));
            				}
						//lbl = new JLabel(new ImageIcon(new URL(arg1.toString().substring(arg1.toString().indexOf("http")))));}
					} }catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return lbl;
            	}
            	
            	JTextArea Summary=new JTextArea(getSummary(arg1.toString(),orignalQuery));
            	String textForSearch=orignalQuery;
            	if(arg1 != null){
            		
                	Summary.setWrapStyleWord(true);
                	Summary.setLineWrap(true);
                    String string = getSummary(arg1.toString(),orignalQuery).toUpperCase();
                    if(string.contains(textForSearch.toUpperCase())){
                    	System.out.println("Search for="+orignalQuery);

                 
                        
                        for (int i = -1; (i = string.indexOf(textForSearch.toUpperCase(), i + 1)) != -1; ) {
                        	try {
                        		Summary.getHighlighter().addHighlight(i,i+textForSearch.length(),new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Color.RED));
                                
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            
                        }
            
                    }
                }
            	JScrollPane pane = new JScrollPane(Summary);
    			return pane;
            }
        };
    }
	public static BufferedImage scaleImage(BufferedImage image, int imageType,
	        int newWidth, int newHeight) {
	        // Make sure the aspect ratio is maintained, so the image is not distorted
	        double thumbRatio = (double) newWidth / (double) newHeight;
	        int imageWidth = image.getWidth(null);
	        int imageHeight = image.getHeight(null);
	        double aspectRatio = (double) imageWidth / (double) imageHeight;

	        if (thumbRatio < aspectRatio) {
	            newHeight = (int) (newWidth / aspectRatio);
	        } else {
	            newWidth = (int) (newHeight * aspectRatio);
	        }

	        // Draw the scaled image
	        BufferedImage newImage = new BufferedImage(newWidth, newHeight,
	                imageType);
	        Graphics2D graphics2D = newImage.createGraphics();
	        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);

	        return newImage;
	    }
}
class MyTableModel extends DefaultTableModel{
	public boolean isCellEditable(int row, int column){ 
		return false;
	}

}
