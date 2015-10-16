package ph.globe.com;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class BonusFBFileConverter extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	JPanel panel1, panel2, panel3, panel4, panel5, panelMain;
	JLabel jlabel1, jlabel2, jlabel3, jlabel4, jlabelAck;
	JButton process;
    JFileChooser chooser;
    JComboBox operationComboBox;
    String choosertitle, operation = "add"; // default value
    String[] operationTypes = { "add", "remove" };
   
  public BonusFBFileConverter(JPanel main) {
	
	this.panelMain = main;
	panel1 = new JPanel(new FlowLayout());
	panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panel2.setSize(30, 30);
	panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panel5 = new JPanel(new FlowLayout(FlowLayout.CENTER));
	jlabel1 = new JLabel("Select Operation: ");
	jlabel2 = new JLabel("Started: ");
	jlabel3 = new JLabel("Finished: ");
	jlabel4 = new JLabel("Total: ");
	jlabelAck = new JLabel("");
    process = new JButton("Select & Process File");
    process.addActionListener(this);
	operationComboBox = new JComboBox(operationTypes);
	operationComboBox.setSelectedIndex(0); 
	operationComboBox.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			
			JComboBox jcmbType = (JComboBox) e.getSource();
			operation = (String) jcmbType.getSelectedItem();
			if(operation.trim().isEmpty()) {
				operation = operationTypes[0]; // "add" = default value
			}
		}
	});
	
	panel1.add(jlabel1);
    panel1.add(operationComboBox);
    panel1.add(process);
    panel2.add(jlabel2);
    panel3.add(jlabel3);
    panel4.add(jlabel4);
    panel5.add(jlabelAck);
    panelMain.add(panel1);
    panelMain.add(panel2);
    panelMain.add(panel3);
    panelMain.add(panel4);
    panelMain.add(panel5);
   }

  
  public void actionPerformed(ActionEvent e) {
	
	long start = 0, end = 0;  
    chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle(choosertitle);
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", new String[] {"txt"} );
    chooser.setFileFilter(filter);
    chooser.setAcceptAllFileFilterUsed(false);
    
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
      
    	start = System.currentTimeMillis();
    	jlabel2.setText("Started: " + start);
        String fileName = chooser.getSelectedFile().toString().substring(0, chooser.getSelectedFile().toString().indexOf(".txt"));
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName + ".csv", "UTF-8");
			BufferedReader br = new BufferedReader(new FileReader(new File(chooser.getSelectedFile().toString())));
	    	String line = "", append = "," + operation;
	    	while((line = br.readLine()) != null) {
	    		if(line.length() == 10) {
	    			writer.write("63" + line + append);
		    		writer.write("\n");
	    		} else {
	    			writer.write(line + append);
		    		writer.write("\n");
	    		}
	    		
	    	}	
	    	br.close();	
	    	JOptionPane.showMessageDialog(null, "Process File is now available at\n" + fileName + ".csv" , "Finished Processing!", JOptionPane.INFORMATION_MESSAGE);
	    	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
		}
		writer.close(); 
		
		end = System.currentTimeMillis();
		jlabel3.setText("Finished: " + end);
		jlabel4.setText("Total: " +  (end - start) + " seconds ");
		jlabelAck.setText("THANK YOU AND HAVE A GOOD DAY!!");
		
      } else {
    		JOptionPane.showMessageDialog(null, "No File Selected!", "Error!", JOptionPane.ERROR_MESSAGE);
      }
   }
   
  
  public Dimension getPreferredSize(){
	  return new Dimension(400, 300);
    }
    
  public static void main(String s[]) {
	  
    JFrame frame = new JFrame("Bonus FB Whitelist Conversion Utility");
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    BonusFBFileConverter bonusFBFileConverter = new BonusFBFileConverter(mainPanel);
    frame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
          }
        }
      );
    frame.getContentPane().add(mainPanel);
    frame.setSize(bonusFBFileConverter.getPreferredSize());
    frame.setLocationRelativeTo(null);
    
    URL url = frame.getClass().getResource("/ph/globe/com/icon/globe_icon.png");
    ImageIcon icon = new ImageIcon(url);
    frame.setIconImage(icon.getImage());
    
    frame.setVisible(true);
    frame.setResizable(false);
    //try {
		//frame.setIconImage(ImageIO.read(new File("src/ph/globe/com/icon/globe_icon.png")));
	//} catch (IOException e1) {
	//	e1.printStackTrace();
	//}
    
    }
}
