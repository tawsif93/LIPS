package com.spl3.lips.operations;

import com.spl3.lips.files.DirectoryReader;
import com.spl3.lips.files.FileExtension;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by peacefrog on 10/5/17.
 * Time 12:44 PM
 */
public class SourceExecutor {

	final static Logger logger = Logger.getLogger(SourceExecutor.class);
	final static Logger testLogger = Logger.getLogger("testLogger");
	private static SourceExecutor sourceExecutorInstance;

	private SourceExecutor() {
	}

	public static SourceExecutor getInstance() {
		if(sourceExecutorInstance == null){
			sourceExecutorInstance = new SourceExecutor();
		}
		return sourceExecutorInstance;
	}

	public boolean compileJavaFile(File path){
		boolean success = false ;
		String command;
		try {
			removeClassFile(path);
			command = "javac " + path.getPath();
			success = !runProcess(command);
//			Runtime.getRuntime().exec(command).getOutputStream().flush();
//			executeJavaFile(path);
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
		return success;
	}

	public boolean compileCFile(File path){
		boolean success = false ;
		String command;
		try {
			command = "gcc -o " + path.getPath().split("\\.c")[0].concat(FileExtension.cExecutable.getValue()) + " " + path.getPath();
			success = !runProcess(command);
//			Runtime.getRuntime().exec(command).getOutputStream().flush();
//			executeJavaFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}


	private boolean executeJavaFile(File path, List<String> args)  {
		String command;
		boolean success = false ;
		try {
			command = "java -cp " + path.getParent() + " " + path.getName();
			command = mergeProgramArguments(command , args);
			System.out.println(command);
//			Runtime.getRuntime().exec(s).getOutputStream().flush();
			success = !runProcess(command);
		} catch (Exception e){
			e.printStackTrace();
		}

		return success;
	}

	private boolean executeCFile(File path, List<String> args)  {
		String command;
		boolean success = false ;
		try {
			command = "./" + path.getParent() + File.separator + path.getName() ;
			command = mergeProgramArguments(command , args);
			System.out.println(command);
			success = !runProcess(command);
		} catch (Exception e){
			e.printStackTrace();
		}

		return success;
	}

	private boolean executePythonFile(File path, List<String> args)  {
		String command;
		boolean success = false ;
		try {
			command = "LC_ALL=\"en_US\" python " + path.getParent() + File.separator + path.getName() ;
			command = mergeProgramArguments(command , args);
			System.out.println(command);
			success = !runProcess(command);
		} catch (Exception e){
			e.printStackTrace();
		}

		return success;
	}

	private String mergeProgramArguments( String command , List<String> args){
		final String[] tempCommand = {command};
		args.forEach(s -> tempCommand[0] = command.concat(" ").concat(s));

		return tempCommand[0];
	}

	private void printLines(String name, InputStream ins) throws Exception {
		String line = null;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			testLogger.debug(line);
			System.out.println(name + " " + line);
		}
	}

	private boolean runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		printLines(command + " stdout:", pro.getInputStream());
		pro.waitFor();
		System.out.println(command + " exitValue() " + pro.exitValue());
		boolean error = isError(pro.getErrorStream(), command);
		System.out.println("Error: " + error);
		return error;
	}

	private boolean isError(InputStream  errorStream , String command) throws Exception {
//		printLines(command + " stderr:", errorStream);
		String output = IOUtils.toString(errorStream, Charset.defaultCharset());
//		System.out.println(output);
		return !StringUtils.isEmpty(output) && StringUtils.contains(output, "error");
	}

	private void removeClassFile(File path){
		String classPath = path.getPath();

		classPath = classPath.split(".java")[0] + ".class";
//		System.out.println(classPath);
		try {
			if(new File(classPath).exists()){
				FileUtils.forceDelete(new File(classPath));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void removeCExecutableFile(File path){
		String classPath = path.getPath();

		classPath = classPath.split(".c")[0] + FileExtension.cExecutable.getValue();
//		System.out.println(classPath);
		try {
			FileUtils.forceDelete(new File(classPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean compileBatchFiles(File rootPath) throws Exception {
		if(!rootPath.isDirectory()){
			throw new Exception("Root path must be a directory");
		}
		else {
			ArrayList<File> listOfFiles = DirectoryReader.getInstance().listFilesForFolder(rootPath);

			for (File file : listOfFiles) {
				if (FilenameUtils.isExtension(file.getName(), getCompilableExtensions())) {
					boolean success = false;
					if (FilenameUtils.getExtension(file.getName()).equals(FileExtension.c.getValue())) {
						success = compileCFile(file);
					} else if (FilenameUtils.getExtension(file.getName()).equals(FileExtension.java.getValue())) {
						success = compileJavaFile(file);
					}
					else if(FilenameUtils.getExtension(file.getName()).equals(FileExtension.python.getValue())){
//						success =compileJavaFile(file);
						success = true;
					}
					if (!success) {
						return success;
					}
				}
			}
			return true;
		}
	}

	public boolean executeBatchFiles(File rootPath, Map<String, ArrayList<String>> rootFileArgs) throws Exception {
		if(!rootPath.isDirectory()){
			throw new Exception("Root path must be a directory");
		}
		else {
			Set<String> executableFiles = rootFileArgs.keySet();
//			ArrayList<File> listOfFiles = DirectoryReader.getInstance().listFilesForFolder(rootPath);
			ArrayList<File> listOfFiles = new ArrayList<>();

			executableFiles.forEach(s -> listOfFiles.add(new File(rootPath + File.separator + s)));

			for (File file : listOfFiles) {

				if (FilenameUtils.isExtension(file.getName(), getExecutableExtensions())) {

					boolean success = false;
					List<String > args = rootFileArgs.get(file.getName());

					if (FilenameUtils.getExtension(file.getName()).equals(FileExtension.c.getValue())) {
//						args.add("0");
						success = executeCFile(file, args);

					} else if (FilenameUtils.getExtension(file.getName()).equals(FileExtension.java.getValue())) {
//						args.add("10");

						success = executeJavaFile(file, args);
					}
					else if(FilenameUtils.getExtension(file.getName()).equals(FileExtension.python.getValue())){
//						args.add("1");
//						args.add("00");
						success =executePythonFile(file , args);
					}
					if (!success) {
						return success;
					}
				}
			}
			return true;
		}
	}

	public String[] getCompilableExtensions(){
		String[] extensions = new String[3];

		extensions[0] = FileExtension.c.getValue();
		extensions[1] = FileExtension.java.getValue();
		extensions[2] = FileExtension.python.getValue();

		return extensions;
	}

	public String[] getExecutableExtensions(){
		String[] extensions = new String[3];

		extensions[0] = FileExtension.cExecutable.getValue();
		extensions[1] = FileExtension.javaClass.getValue();
		extensions[2] = FileExtension.python.getValue();

		return extensions;
	}
}
