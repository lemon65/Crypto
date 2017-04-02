import java.awt.EventQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import java.awt.Button;
import java.awt.Choice;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.awt.event.ActionEvent;
import java.awt.TextArea;
import java.awt.Toolkit;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.EmptyStackException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.awt.TextField;

@SuppressWarnings("serial")
public class Crypto extends JFrame{

	private JPanel contentPane;
	public String passwd = null;
	public String user_data = null;
	public String encrypted_data = null;
	public String tmp_buff = null;
	byte[] encrypted = null;
	byte[] decrypted = null;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Crypto frame = new Crypto();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Crypto() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Crypto.class.getResource("/Images/safe-backup-icon.png")));
		setResizable(false);
		setTitle("Crypto");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 538, 566);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(128, 128, 128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//create the debugging area
		TextArea debug = new TextArea();
		debug.setFont(new Font("Monospaced", Font.PLAIN, 9));
		debug.setBackground(Color.WHITE);
		debug.setEditable(false);
		debug.setBounds(10, 402, 512, 125);
		contentPane.add(debug);
		
		// Create the choice options
		Choice choice = new Choice();
		choice.setBounds(25, 345, 193, 31);
		contentPane.add(choice);
		choice.add("Encrypt");
		choice.add("Decrypt");
		
		// Create and a key for the user on startup!
		TextField passwordField = new TextField();
		passwordField.setBounds(235, 345, 193, 22);
		passwordField.setEditable(true);
		contentPane.add(passwordField);
		KeyGenerator gen = null;
		try {
			gen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e1) {e1.printStackTrace();
		}
		gen.init(128); /* 128-bit AES */
		SecretKey secret = gen.generateKey();
		byte[] binary = secret.getEncoded();
		String key = String.format("%032X", new BigInteger(+1, binary));
		key = key.substring(0, Math.min(key.length(), 16));
		passwordField.setText(key);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 512, 300);
		contentPane.add(scrollPane);
		
		//area for the user to add text to be encrypted
		JTextArea user_input = new JTextArea();
		user_input.setFont(new Font("Tahoma", Font.PLAIN, 14));
		user_input.setText("Welcome To Crypto, Update the Text Here!");
		user_input.setToolTipText("Please Input Text HERE");
		scrollPane.setViewportView(user_input);
		
		JLabel lblNewLabel = new JLabel("Save OR Enter a Key");
		lblNewLabel.setBounds(235, 325, 150, 14);
		contentPane.add(lblNewLabel);

		JLabel lblDebugoutput = new JLabel("Debug_Output");
		lblDebugoutput.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDebugoutput.setBounds(25, 382, 88, 14);
		contentPane.add(lblDebugoutput);
		
		JLabel lblMode = new JLabel("Mode");
		lblMode.setBounds(29, 325, 150, 14);
		contentPane.add(lblMode);
		
		Button button = new Button("GO");
		button.setBackground(new Color(50, 205, 50));
		button.setFont(new Font("Tahoma", Font.PLAIN, 12));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				String current_mode = choice.getSelectedItem();// picking the current mode
				String user_data = user_input.getText();
				//byte[] input = user_data.getBytes();
				String passwd = passwordField.getText();
				if(user_data==null || user_data.isEmpty()){user_data = null;}
				if(passwd==null || passwd.isEmpty()){passwd = null;}
				// Debugging data out to the user!
				debug.append("Running using Mode: " + current_mode + "\n");// debug out to the user..
				if(user_data == null || passwd == null){// Check that the user has given a password and Text data!
					debug.append("### ERROR, Please make sure you have entered a Private Key and Encryptable Text ### \n");
					JOptionPane.showMessageDialog(null,"Please make sure you have entered a Private Key and Encryptable Text");
					throw new EmptyStackException();
				}
				debug.append("ingesting your Key...\n");
	            Key aesKey = new SecretKeySpec(passwd.getBytes(), "AES");
	            try {Cipher cipher = Cipher.getInstance("AES");
	            if (current_mode == "Encrypt"){
	            	debug.append("Working on Encrypting data\n");
	            	cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	            	byte[] encrypted = cipher.doFinal(user_data.getBytes());
	            	tmp_buff = new String(Base64.getEncoder().encodeToString(encrypted));
	            	user_input.setText(tmp_buff);
	            }
				if(current_mode == "Decrypt"){
					debug.append("Working on Decrypting data\n");
					byte[] encrypted = Base64.getDecoder().decode(user_data);
					cipher.init(Cipher.DECRYPT_MODE, aesKey);
					String decrypted = new String(cipher.doFinal(encrypted));
					user_input.setText(decrypted);
				} 
	            debug.append("##################### END OF CODE #####################\n");
	            } catch (Exception e) {debug.append(e.toString() + "\n");} 
			}});
		button.setBounds(434, 374, 88, 22);
		contentPane.add(button);	
		
		Button button_1 = new Button("Save Key");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String save_pass = passwordField.getText();
				try {
					DateFormat df = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
					Calendar calobj = Calendar.getInstance();
					String file_name = df.format(calobj.getTime()) + ".txt";
					PrintWriter writer = new PrintWriter(file_name, "UTF-8");
					writer.println(save_pass);
					debug.append("Saved Key to File: " + file_name + "\n");
					writer.close();
				} catch (FileNotFoundException e1) {debug.append("### Saving Error: FileNotFoundException\n");
				} catch (UnsupportedEncodingException e1) {debug.append("### Saving Error: UnsupportedEncodingException\n");
				}
			}
		});
		button_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		button_1.setBackground(new Color(0, 191, 255));
		button_1.setBounds(232, 374, 88, 22);
		contentPane.add(button_1);
		
		Button button_2 = new Button("Gen Key");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debug.append("Created a new Key!\n");
				KeyGenerator gen = null;
				try {gen = KeyGenerator.getInstance("AES");
				} catch (NoSuchAlgorithmException e1) {e1.printStackTrace();}
				gen.init(128); /* 128-bit AES */
				SecretKey secret = gen.generateKey();
				byte[] binary = secret.getEncoded();
				String key = String.format("%032X", new BigInteger(+1, binary));
				key = key.substring(0, Math.min(key.length(), 16));
				passwordField.setText(key);
			}
		});
		button_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		button_2.setBackground(Color.YELLOW);
		button_2.setBounds(434, 345, 88, 22);
		contentPane.add(button_2);
		
		Button button_3 = new Button("Load Key");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = fileChooser.getSelectedFile();
		          try (BufferedReader buffer = new BufferedReader(new FileReader(selectedFile))) {
		        		String firstLine = buffer.readLine();
		        		passwordField.setText(firstLine);
		        	} catch (IOException ex) {
		        		debug.append("### Loading Error: IOException\n");
		        	}
		        }
			}
		});
		button_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		button_3.setBackground(new Color(30, 144, 255));
		button_3.setBounds(326, 373, 88, 22);
		contentPane.add(button_3);
		
		Button button_4 = new Button("Clear Text Box");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user_input.setText("");
				debug.append("Cleared the Text Box!\n");
			}
		});
		button_4.setBackground(new Color(255, 0, 0));
		button_4.setBounds(434, 317, 88, 22);
		contentPane.add(button_4);
	}
}
