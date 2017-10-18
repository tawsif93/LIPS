package com.spl3.lips.orbs;

import com.spl3.lips.files.DirectoryReader;
import com.spl3.lips.operations.SourceExecutor;

import java.io.File;

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

	private String [][] lines ;

	public static void main(String[] args) {
		DirectoryReader.getInstance().init("/home/peacefrog/SPL_LIPS");
		System.out.println(DirectoryReader.getInstance().getRepository().getAllFiles());
		SourceExecutor.getInstance().compileJavaFile(new File("/home/peacefrog/Dropbox/orbs/projects/example/work/checker.java"));
	}
}
