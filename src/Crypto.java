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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class Crypto extends JFrame{

	private JPanel contentPane;
	public String passwd = null;
	public String user_data = null;
	public String encrypted_data = null;
	public String tmp_buff = null;
	byte[] encrypted = null;
	byte[] decrypted = null;
	private JTextField target_image_textField;
	
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
				
				JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
				tabbedPane_1.setBounds(10, 11, 512, 515);
				contentPane.add(tabbedPane_1);
				
				// ##################################### Starting Pane for Text Ecryption #####################################
				JPanel text_encryption_panel = new JPanel();
				tabbedPane_1.addTab("Text Encryption", null, text_encryption_panel, null);
				text_encryption_panel.setLayout(null);
				
				//create the debugging area
				TextArea debug = new TextArea();
				debug.setBounds(10, 378, 477, 99);
				text_encryption_panel.add(debug);
				debug.setFont(new Font("Monospaced", Font.PLAIN, 9));
				debug.setBackground(Color.WHITE);
				debug.setEditable(false);
				
				// Create the choice options
				Choice choice = new Choice();
				choice.setBounds(10, 313, 193, 20);
				text_encryption_panel.add(choice);
				
				// Create and a key for the user on startup!
				TextField passwordField = new TextField();
				passwordField.setBounds(227, 311, 166, 22);
				text_encryption_panel.add(passwordField);
				passwordField.setEditable(true);
				
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(10, 11, 477, 266);
				text_encryption_panel.add(scrollPane);
				
				//area for the user to add text to be encrypted
				JTextArea user_input = new JTextArea();
				user_input.setFont(new Font("Tahoma", Font.PLAIN, 14));
				user_input.setText("Welcome To Crypto, Update the Text Here!");
				user_input.setToolTipText("Please Input Text HERE");
				scrollPane.setViewportView(user_input);
				
				JLabel lblNewLabel = new JLabel("Save OR Enter a Key");
				lblNewLabel.setBounds(227, 291, 150, 14);
				text_encryption_panel.add(lblNewLabel);
				
						JLabel lblDebugoutput = new JLabel("Debug_Output");
						lblDebugoutput.setBounds(10, 358, 88, 14);
						text_encryption_panel.add(lblDebugoutput);
						lblDebugoutput.setFont(new Font("Tahoma", Font.PLAIN, 9));
						
						JLabel lblMode = new JLabel("Mode");
						lblMode.setBounds(10, 293, 150, 14);
						text_encryption_panel.add(lblMode);
						
						Button GO_button = new Button("GO");
						GO_button.setBounds(399, 339, 88, 22);
						text_encryption_panel.add(GO_button);
						GO_button.setBackground(new Color(50, 205, 50));
						GO_button.setFont(new Font("Tahoma", Font.PLAIN, 12));
						
						Button Save_button = new Button("Save Key");
						Save_button.setBounds(212, 339, 88, 22);
						text_encryption_panel.add(Save_button);
						Save_button.addActionListener(new ActionListener() {
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
						Save_button.setFont(new Font("Tahoma", Font.PLAIN, 12));
						Save_button.setBackground(new Color(0, 191, 255));
						
						Button Gen_button = new Button("Gen Key");
						Gen_button.setBounds(399, 311, 88, 22);
						text_encryption_panel.add(Gen_button);
						Gen_button.addActionListener(new ActionListener() {
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
						Gen_button.setFont(new Font("Tahoma", Font.PLAIN, 12));
						Gen_button.setBackground(Color.YELLOW);
						
						Button Load_button = new Button("Load Key");
						Load_button.setBounds(305, 339, 88, 22);
						text_encryption_panel.add(Load_button);
						Load_button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File workingDirectory = new File(System.getProperty("user.dir"));
						        JFileChooser fileChooser = new JFileChooser();
						        fileChooser.setCurrentDirectory(workingDirectory);
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
						Load_button.setFont(new Font("Tahoma", Font.PLAIN, 12));
						Load_button.setBackground(new Color(30, 144, 255));
						
						Button Clear_button = new Button("Clear Text Box");
						Clear_button.setForeground(Color.BLACK);
						Clear_button.setBounds(399, 283, 88, 22);
						text_encryption_panel.add(Clear_button);
						Clear_button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								user_input.setText("");
								debug.append("Cleared the Text Box!\n");
							}
						});
						Clear_button.setBackground(new Color(255, 0, 0));
						
						GO_button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0){
						String current_mode = choice.getSelectedItem();// picking the current mode
						String user_data = user_input.getText();
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
		choice.add("Encrypt");
		choice.add("Decrypt");
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
		
		
		// ##################################### Starting Pane for steganography #####################################
		JPanel image_steganography = new JPanel();
		tabbedPane_1.addTab("Image Steganography", null, image_steganography, null);
		image_steganography.setLayout(null);
		
		Choice stg_set_mode = new Choice();
		stg_set_mode.setBounds(10, 342, 182, 36);
		stg_set_mode.add("Insert");
		stg_set_mode.add("Extract");
		
		TextArea stg_debug_area = new TextArea();
		stg_debug_area.setFont(new Font("Monospaced", Font.PLAIN, 9));
		stg_debug_area.setBounds(10, 381, 487, 96);
		image_steganography.add(stg_debug_area);
		image_steganography.add(stg_set_mode);
		
		TextArea steg_textArea = new TextArea();
		steg_textArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		steg_textArea.setText("Hide Text within images, with this simple tool!");
		steg_textArea.setBounds(10, 10, 487, 275);
		image_steganography.add(steg_textArea);
		
		target_image_textField = new JTextField();
		target_image_textField.setToolTipText("Path to an image");
		target_image_textField.setEditable(false);
		target_image_textField.setBounds(120, 291, 377, 20);
		image_steganography.add(target_image_textField);
		target_image_textField.setColumns(10);
		
		Button target_image_button = new Button("Target Image");
		target_image_button.setForeground(new Color(0, 0, 0));
		target_image_button.setBackground(new Color(0, 191, 255));
		target_image_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File workingDirectory = new File(System.getProperty("user.dir"));
		        JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setCurrentDirectory(workingDirectory);
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File targetimageFile = fileChooser.getSelectedFile();
		          	stg_debug_area.append("Using File: " + targetimageFile.toString() + "\n");
		          	target_image_textField.setText(targetimageFile.toString());
		        }
			}
		});
		target_image_button.setBounds(10, 289, 94, 22);
		image_steganography.add(target_image_button);
		
		Button stg_go_button = new Button("GO");
		stg_go_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String image_path = target_image_textField.getText();
				if(!image_path.contains(".jpg") && !image_path.contains(".png")){// Check that the user has given an image path
					JOptionPane.showMessageDialog(null,"Please make sure you have given the path to an image!");
					target_image_textField.setText("");
					throw new EmptyStackException();
				}
				Steganography steg = new Steganography();
				String srg_current_mode = stg_set_mode.getSelectedItem();// picking the current mode
	            if (srg_current_mode == "Insert"){
	            	stg_debug_area.append("Working on Inserting Text into your image.\n");
					String message_text = steg_textArea.getText();
					steg.encode(image_path, "Testing_File", message_text);
					stg_debug_area.append("Done Inserting the Text, into your image.\n");
	            }
				if(srg_current_mode == "Extract"){
					stg_debug_area.append("Working on Extracting Text from your image.\n");
					String decoded_message = steg.decode(image_path);
					steg_textArea.setText("############### Found Data ###############\n" + decoded_message);
					stg_debug_area.append("Done Extracting the Text, from your image.\n");
				} 
			}
		});
		stg_go_button.setBackground(new Color(50, 205, 50));
		stg_go_button.setBounds(298, 326, 125, 36);
		image_steganography.add(stg_go_button);
		
		Button help_button = new Button("Help");
		help_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stg_debug_area.append("Getting help information.\n");
				String help_text = "                                           Need Some Help?\n"
						+ "1. Type out the Text you want to hide in the image\n"
						+ "2. Select a Target image using the 'Taget Image Button', This has to be a (.jpg or .png)\n"
						+ "3. Pick your mode (Insert / Extract)\n"
						+ "4. Then simply Hit 'Go' and your text will be hidden or shown\n";
				JOptionPane.showMessageDialog(null, help_text);
			}
		});
		help_button.setBackground(new Color(95, 158, 160));
		help_button.setBounds(429, 326, 57, 36);
		image_steganography.add(help_button);
		
		Button clear_text_box = new Button("Clear Text Box");
		clear_text_box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stg_debug_area.append("Clearing the Text Box!\n");
				steg_textArea.setText("");
			}
		});
		clear_text_box.setBackground(new Color(255, 0, 0));
		clear_text_box.setBounds(198, 326, 94, 36);
		image_steganography.add(clear_text_box);
		
		JLabel mode_lab = new JLabel("Mode");
		mode_lab.setBounds(10, 322, 150, 14);
		image_steganography.add(mode_lab);
		
		
		
		
	}
}
