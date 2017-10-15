package com.spl3.lips.operations;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by peacefrog on 10/5/17.
 * Time 12:44 PM
 */
public class SourceExecutor {

	private static SourceExecutor sourceExecutorInstance;

	private SourceExecutor() {
	}

	public static SourceExecutor getInstance() {
		if(sourceExecutorInstance == null){
			sourceExecutorInstance = new SourceExecutor();
		}
		return sourceExecutorInstance;
	}

	public void compileJavaFile(File path){

		try {
			String command = "javac " + path.getPath();
			runProcess(command);
//			Runtime.getRuntime().exec(command).getOutputStream().flush();
			executeJavaFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		ProcessBuilder pb = new ProcessBuilder("javac " + path, "myArg1", "myArg2");
//		Map<String, String> env = pb.environment();
//		env.put("VAR1", "myValue");
//		env.remove("OTHERVAR");
//		env.put("VAR2", env.get("VAR1") + "suffix");
//		pb.directory(new File("myDir"));
//		try {
//			Process p = pb.start();
//			p.getOutputStream().flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		env.forEach((s, s2) -> System.out.println(s + " " + s2));
	}

	private void executeJavaFile(File path) throws Exception {
		String command;
		command = "java -cp " + path.getParent() + " " + path.getName().replace(".java" , "") + " 10.0";
		System.out.println(command);
//			Runtime.getRuntime().exec(s).getOutputStream().flush();
		runProcess(command);
	}

	private void printLines(String name, InputStream ins) throws Exception {
		String line = null;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(name + " " + line);
		}
	}

	private boolean runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		printLines(command + " stdout:", pro.getInputStream());
		pro.waitFor();
		System.out.println(command + " exitValue() " + pro.exitValue());
	}

	private boolean isError(Process pro , String command) throws Exception {
		String output = IOUtils.toString(pro.getErrorStream());
		printLines(command + " stderr:", pro.getErrorStream());

		return StringUtils.isEmpty(output);
	}
}
