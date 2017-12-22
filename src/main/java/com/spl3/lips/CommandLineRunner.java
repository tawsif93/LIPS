package com.spl3.lips;

import com.spl3.lips.operations.ORBSLogger;
import com.spl3.lips.orbs.ORBS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peacefrog on 12/21/17.
 * Time 1:02 PM
 */
public class CommandLineRunner {

	public static void main(String[] args) {
		ORBSLogger.tieSystemOutAndErrToLog();

		ORBS orbs = new ORBS();
		boolean rootFound = false;

		String root = "example/orig";
		String output = "work";
		Map<String , ArrayList<String>> params = new HashMap<>();

		for (int i = 0; i < args.length; i++) {
			if(args[i].equals("-s")){
				if(i < args.length ) root = args[i+1];
			}
			if (args[i].equals("-o")){
				if (i < args.length) output = args[i+1];
			}
			if(args[i].equals("-a")){
				String file = args[i+1];
				ArrayList<String> listParam = new ArrayList<>();
				for (int j = i+2; j < args.length; j++) {
					listParam.add(args[j]);
				}
				params.put(file , listParam);

				rootFound = true;
			}
		}
		if(!rootFound){
			System.out.println("Specify root parameters");
			System.exit(1);
		}

		orbs.setCommandLineParams(params);
		orbs.setOutputPath(output);
		orbs.setSourcePath(root);
		orbs.runSlice();
	}

}
