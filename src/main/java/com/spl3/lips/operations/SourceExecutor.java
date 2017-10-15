package com.spl3.lips.operations;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

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
			removeClassFile(path);

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

	private void runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		printLines(command + " stdout:", pro.getInputStream());
		pro.waitFor();
		System.out.println(command + " exitValue() " + pro.exitValue());
		System.out.println(isError(pro.getErrorStream() , command));
	}

	private boolean isError(InputStream  errorStream , String command) throws Exception {
//		printLines(command + " stderr:", errorStream);
		String output = IOUtils.toString(errorStream);

		return !StringUtils.isEmpty(output);
	}

	private void removeClassFile(File path){
		String classPath = path.getPath();

		classPath = classPath.split(".java")[0] + ".class";
//		System.out.println(classPath);
		try {
			FileUtils.forceDelete(new File(classPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
