package org.fiteagle.adapters.epc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Helper methods
 * 
 * @author robynloughnane
 *
 */
public class CommonMethods {

	final static Logger LOGGER = Logger.getLogger(CommonMethods.class
			.toString());

	public static String executeCommand(String command) {

		LOGGER.info("Execute command: " + command);
		
		String separator = System.getProperty("line.separator");
		StringBuilder lines = new StringBuilder("Executed command: " + command);
		lines.append(separator);
		lines.append("Reply:");
		lines.append(separator);

		try {
			String line;
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				lines.append(line);
				lines.append(separator);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String output = lines.toString();
		return output;
	}
}