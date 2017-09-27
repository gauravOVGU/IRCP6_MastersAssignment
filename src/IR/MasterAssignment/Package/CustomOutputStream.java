package IR.MasterAssignment.Package;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class CustomOutputStream extends OutputStream{

	private JTextArea textArea;
	
	public CustomOutputStream(JTextArea textArea) {
		// TODO Auto-generated constructor stub
		this.textArea = textArea;
	}
		
	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		textArea.append(String.valueOf((char) b));
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
}