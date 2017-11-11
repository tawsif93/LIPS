package com.spl3.lips.orbs;

import com.spl3.lips.files.DirectoryReader;
import com.spl3.lips.operations.SourceExecutor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by peacefrog on 9/30/17.
 * Time 10:17 PM
 */
public class ORBS {
//  Number of ignored lines
	private  int numberOfIgnoredChanges = 0;
//	Number of compilations
	private int numberOfCompilation = 0;
//  Number of cached compilations
	private int numberOfCachedCompilation= 0;
//	Number of cached executions
	private int numberOfCahedExecutoins= 0;
//	Number of executions
	private  int numberOfExecutions = 0;
//  Number of main loop iterations
	private int numberOfIterations= 0;

	private File [] files;
	private String [][] lines ;
	private boolean [] deleted ;
	private Map<String, Integer> startLines;

	public static void main(String[] args) {
		DirectoryReader.getInstance().init("/home/peacefrog/SPL_LIPS");
		System.out.println(DirectoryReader.getInstance().getRepository().getAllFiles());
		SourceExecutor.getInstance().compileJavaFile(new File("/home/peacefrog/Dropbox/orbs/projects/example/work/checker.java"));
		ORBS orbs = new ORBS();
		orbs.setup();
	}

	private void setup(){
		files = DirectoryReader.getInstance().getRepository().getAllFiles().toArray(new File[0]);
		int numberOfLines = DirectoryReader.getInstance().getRepository().getAllFilesLineCount(files);

		lines = new String[numberOfLines + 1][];
		deleted = new boolean[numberOfLines+1];

		Arrays.fill(deleted, false);

		for (int i = 0; i < numberOfLines + 1; i++) {
			lines[i] = new String[2];
		}

		final int[] currentLine = {0};

		for(File file : files){
			try {
				FileUtils.readLines(file).forEach(o ->{
					lines[currentLine[0]][0] = file.getPath();
					lines[currentLine[0]][1] = (String) o;
					currentLine[0]++;
				} );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


//	public void runORBS(){
//		boolean reduced = true;
//
//		while (reduced){
//			reduced = false;
//			this.numberOfIterations += 1;
//			line = start
//		}
//	}
}
