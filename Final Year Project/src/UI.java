import java.awt.EventQueue;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class UI implements ActionListener, DocumentListener{

	private JFrame frame;
	private JTextArea textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JLabel label_2,lblLetterOutput;

	private static final String COMMIT_ACTION = "commit";
	private static enum Mode { INSERT, COMPLETION };
	private final List<String> words;
	Set<String> letters = new HashSet<String>(10000);
	Set<String> finalRoutes = new HashSet<String>(10000);
	private Mode mode = Mode.INSERT;
	private JTextField textField_4;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.put("swing.boldMetal", Boolean.FALSE);
					UI window = new UI();
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
	public UI() {
		super();
		initialize();

		//**************** Code from site *******************
		textField.getDocument().addDocumentListener(this);

		InputMap im = textField.getInputMap();
		ActionMap am = textField.getActionMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());

		//***************************************************
		words = new ArrayList<String>(2700);

		try {
			BufferedReader letterreader = new BufferedReader(new FileReader("3letters.txt"));

			String station;

			while ((station = letterreader.readLine()) != null) {
				station = station.toLowerCase();
				letters.add(station);
				station = station.substring(4);

				words.add(station);				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 683, 442);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);


		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel lblEnterStartStation = new JLabel("Enter Start Station:");
		lblEnterStartStation.setBounds(28, 86, 126, 16);
		frame.getContentPane().add(lblEnterStartStation);

		JLabel lblEnterEndStation = new JLabel("Enter End Station:");
		lblEnterEndStation.setBounds(28, 122, 126, 16);
		frame.getContentPane().add(lblEnterEndStation);

		textField_3 = new JTextField();
		textField_3.setBounds(215, 80, 134, 28);
		frame.getContentPane().add(textField_3);
		textField_3.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(215, 116, 134, 28);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);

		JLabel lblRequiredToPass = new JLabel("Required To Pass Through:");
		lblRequiredToPass.setBounds(28, 158, 181, 16);
		frame.getContentPane().add(lblRequiredToPass);

		textField_2 = new JTextField();
		textField_2.setBounds(215, 152, 134, 28);
		frame.getContentPane().add(textField_2);
		textField_2.setColumns(10);

		JLabel lblBritishRailwayTicket = new JLabel("British Railway Ticket Validity Checker");
		lblBritishRailwayTicket.setHorizontalAlignment(SwingConstants.CENTER);
		lblBritishRailwayTicket.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		lblBritishRailwayTicket.setBounds(29, 6, 570, 44);
		frame.getContentPane().add(lblBritishRailwayTicket);

		JLabel lblValidRoutes = new JLabel("Valid Routes");
		lblValidRoutes.setBounds(303, 219, 78, 16);
		frame.getContentPane().add(lblValidRoutes);

		label_2 = new JLabel("");
		label_2.setBounds(6, 235, 671, 137);
		frame.getContentPane().add(label_2);

		JLabel lblFindLetter = new JLabel("Find 3 letter Code:");
		lblFindLetter.setBounds(371, 86, 126, 16);
		frame.getContentPane().add(lblFindLetter);

		textField = new JTextArea();
		textField.setBounds(506, 80, 134, 28);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		lblLetterOutput = new JLabel("\n");
		lblLetterOutput.setBounds(371, 128, 269, 16);
		frame.getContentPane().add(lblLetterOutput);

		JButton btnFindCode = new JButton("Find Code");
		btnFindCode.setBounds(28, 373, 117, 29);
		frame.getContentPane().add(btnFindCode);
		btnFindCode.addActionListener(this);

		JButton btnCheckRoute = new JButton("Check Route");
		btnCheckRoute.setBounds(506, 373, 117, 29);
		frame.getContentPane().add(btnCheckRoute);

		JLabel lblPriceOfThe = new JLabel("Enter Cost Of The Ticket:");
		lblPriceOfThe.setBounds(28, 192, 181, 16);
		frame.getContentPane().add(lblPriceOfThe);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(215, 186, 134, 28);
		frame.getContentPane().add(textField_4);
		btnCheckRoute.addActionListener(this);
	}

	/**
	 * Waits for input from a button pressed and then performs
	 * action based on user input.
	 * @param a
	 */
	public void actionPerformed(ActionEvent a){
		Algorithm al = new Algorithm();
		JButton getPressed = (JButton) a.getSource();
		String button = getPressed.getText();
		Set<String> routes = new HashSet<String>();
		Set<String> toremove = new HashSet<String>();
		String route_Output ="<HTML>",start = textField_3.getText().toUpperCase(),
				end = textField_1.getText().toUpperCase(), stops = textField_2.getText().toUpperCase(),
				cost = textField_4.getText();
		routes.clear();
		toremove.clear();
		finalRoutes.clear();

		if(button.equals("Find Code") && !textField.getText().equals("")){

			for(String line : letters){
				String three = line.substring(0, 3);
				String stat = line.substring(4);

				if(stat.equals(textField.getText())){
					lblLetterOutput.setText(three.toUpperCase());
				}
			}

		}else if(button.equals("Find Code")){
			lblLetterOutput.setText("Enter a station to find it's code");
		}

		if(button.equals("Check Route") && !start.equals("") && !end.equals("") && !cost.equals("")){
			try {
				routes = al.run(start,end ,stops,Double.parseDouble(cost));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String[] places = stops.split(" ");


			for(String check_Points : routes){
				for(int i = 0; i < places.length;i++){
					CharSequence current = places[i];
					if(!check_Points.contains(current)){
						toremove.add(check_Points);
					}
				}
			}	

			routes.removeAll(toremove);


			for(String removeDupes : routes){
				String[] remove = removeDupes.split(" ");
				String dup = "";
				for(int j = 0;j<remove.length-1;j++){
					dup = dup + " " + remove[j];	

				}
				finalRoutes.add(dup);
			}

			if(finalRoutes.size()>8){
				String errorMessage = " There Are Too Many Routes For This Journey: Please Enter More Stations To Pass Through";
				route_Output = route_Output + errorMessage+"<br>";	
			}

			if(finalRoutes.isEmpty()){
				label_2.setText("No valid routes");
			}else{
				for(String route : finalRoutes){

					route_Output = route_Output + " " + route+"<br>";

					if(finalRoutes.size()>8){
						break;
					}
				}

				route_Output = route_Output + "</HTML>";
				label_2.setText(route_Output);
			}
		}
		else if(button.equals("Check Route")){
			label_2.setText("Please Fill Out All Required Information");
		}
	}

	/*
	 * Code below is from the following site
	 * http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TextAreaDemoProject/src/components/TextAreaDemo.java
	 * 
	 * It is for the implementation of auto-completion for finding station codes.
	 * 
	 */

	private class CompletionTask implements Runnable {
		String completion;
		int position;
		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}
		public void run() {
			textField.insert(completion, position);
			textField.setCaretPosition(position + completion.length());
			textField.moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
	}

	@SuppressWarnings("serial")
	private class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = textField.getSelectionEnd();
				textField.insert(" ", pos);
				textField.setCaretPosition(pos + 1);
				mode = Mode.INSERT;
			} else {
				textField.replaceSelection("\n");
			}
		}
	}

	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1) {
			return;
		}

		int pos = ev.getOffset();
		String content = null;
		try {
			content = textField.getText(0, pos + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (! Character.isLetter(content.charAt(w))) {
				break;
			}
		}
		if (pos - w < 2) {
			// Too few chars
			return;
		}

		String prefix = content.substring(w + 1).toLowerCase();
		int n = Collections.binarySearch(words, prefix);
		if (n < 0 && -n <= words.size()) {
			String match = words.get(-n - 1);
			if (match.startsWith(prefix)) {
				// A completion is found
				String completion = match.substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(
						new CompletionTask(completion, pos + 1));
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;
		}
	}

	// Not Used....
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub

	}
}

