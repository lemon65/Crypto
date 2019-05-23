import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.EmptyStackException;
import org.apache.commons.io.FilenameUtils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class AES_Helpers
{
        public String create_aes_key(TextArea debug)
        {
			KeyGenerator gen = null;
			try {gen = KeyGenerator.getInstance("AES");
			} catch (NoSuchAlgorithmException e1) {e1.printStackTrace();}
			gen.init(128); /* 128-bit AES */
			SecretKey secret = gen.generateKey();
			byte[] binary = secret.getEncoded();
			String key = String.format("%032X", new BigInteger(+1, binary));
			key = key.substring(0, Math.min(key.length(), 16));
			debug.append("Created a new Key: " + key + "\n");
			return key;
        }
               
        public void save_key_to_file(TextArea debug, String pass_key)
        {
			try {
				if(pass_key==null || pass_key.isEmpty()) {
					debug.append("Key is Null, did you generate one?\n");
					return;
				}
				DateFormat df = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
				Calendar calobj = Calendar.getInstance();
				String file_name = df.format(calobj.getTime()) + ".txt";
				PrintWriter writer = new PrintWriter(file_name, "UTF-8");
				writer.println(pass_key);
				debug.append("Saved Key to File: " + file_name + "\n");
				writer.close();
			} catch (FileNotFoundException e1) {debug.append("### Saving Error: FileNotFoundException\n");
			} catch (UnsupportedEncodingException e1) {debug.append("### Saving Error: UnsupportedEncodingException\n");
			}
        }
        
        public String load_key_from_file(TextArea debug)
        {
			File workingDirectory = new File(System.getProperty("user.dir"));
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setCurrentDirectory(workingDirectory);
	        int returnValue = fileChooser.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File selectedFile = fileChooser.getSelectedFile();
	          try (BufferedReader buffer = new BufferedReader(new FileReader(selectedFile))) {
	        		String firstLine = buffer.readLine();
	            	return firstLine;
	        	} catch (IOException ex) {
	        		debug.append("### Loading Error: IOException\n");
	        	}
	        }
			String error_str = "No Key File, was picked";
			return error_str;
        }
        
        public void processString(String input_string, String passwd, String current_mode, TextArea debug, JTextArea user_input)
        {
			if(input_string==null || input_string.isEmpty()){input_string = null;}
			if(passwd==null || passwd.isEmpty()){passwd = null;}
			debug.append("Running using Mode: " + current_mode + "\n");// debug out to the user..
			if(input_string == null || passwd == null){// Check that the user has given a password and Text data!
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
            	byte[] encrypted = cipher.doFinal(input_string.getBytes());
            	String encrypted_buffer = new String(Base64.getEncoder().encodeToString(encrypted));
            	user_input.setText(encrypted_buffer);
            }
			if(current_mode == "Decrypt"){
				debug.append("Working on Decrypting data\n");
				byte[] encrypted = Base64.getDecoder().decode(input_string);
				cipher.init(Cipher.DECRYPT_MODE, aesKey);
				String decrypted = new String(cipher.doFinal(encrypted));
				user_input.setText(decrypted);
			}
		    debug.append("##################### END OF CODE #####################\n");
		    } catch (Exception e) {debug.append(e.toString() + "\n");} 
        }
        
        public void processFile(String passwd, String current_mode, String inFile, TextArea debug)
        	    throws javax.crypto.IllegalBlockSizeException,
        	           javax.crypto.BadPaddingException,
        	           java.io.IOException, NoSuchPaddingException,
        	           NoSuchAlgorithmException, InvalidKeyException
        	    	{
        				File outfile_name = new File(inFile);
        				String extension = FilenameUtils.getExtension(inFile); // returns "txt"
        				String outFile = inFile.replace('.' + extension, ".tmp");
        				File out_file = new File(outFile);
        				out_file.createNewFile();
						debug.append("ingesting your Key...\n");
			            Key aesKey = new SecretKeySpec(passwd.getBytes(), "AES");
			            Cipher cipher = null;
						cipher = Cipher.getInstance("AES");
			            if (current_mode == "Encrypt"){
			            	debug.append("Working on Encrypting data\n");
							cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			            	}
			            if(current_mode == "Decrypt"){
			            	debug.append("Working on Decrypting data\n");
							cipher.init(Cipher.DECRYPT_MODE, aesKey);
			            	}
			            
	        	        try (FileInputStream in = new FileInputStream(inFile);
	        	             FileOutputStream out = new FileOutputStream(outFile)) {
	        	            byte[] ibuf = new byte[1024];
	        	            int len;
	        	            debug.append("Running in " + current_mode +" Mode, on file bytes...\n");
	        	            while ((len = in.read(ibuf)) != -1) {
	        	                byte[] obuf = cipher.update(ibuf, 0, len);
	        	                if ( obuf != null ) out.write(obuf);
	        	            }
	        	            debug.append("Finalizing File...\n");
	        	            byte[] obuf = cipher.doFinal();
	        	            if ( obuf != null ) out.write(obuf);
	        	        }
	        	        debug.append("Cleaning Up Files...\n");
        				File in_file = new File(inFile);
        				in_file.delete(); // Remove the inFile
        				out_file.renameTo(outfile_name);
        		debug.append("##################### END OF CODE #####################\n");
        	    }
        
}