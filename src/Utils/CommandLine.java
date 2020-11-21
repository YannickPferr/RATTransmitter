package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLine {

	Runtime r;
	public CommandLine(){
		r = Runtime.getRuntime();
	}
	
	public String executeCommand(String cmd){
		
		Process p;
		try {
			p = r.exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder output = new StringBuilder();
			String line;
			while((line = br.readLine()) != null)
				output.append(line);
			
			return output.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
}
