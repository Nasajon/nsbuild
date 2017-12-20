package br.com.nasajon.nsjbuild.util;

import java.io.InputStream;

public class Shell {
	
	public static boolean runCmd(String cmd, String errorMessage) throws Exception {
		boolean result = false;
		
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			
			result = true;
			
			if(process.waitFor() != 0) {
				result = false;
				
				System.out.println("");
				System.out.println("");

				System.out.println(errorMessage);

				InputStream error = process.getInputStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				System.out.println("");

				error = process.getErrorStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
			}
		} catch (Exception e) {
			result = false;
			
			System.out.println(errorMessage);
			throw new Exception(e.getMessage());
		};
		
		return result;
	}
}