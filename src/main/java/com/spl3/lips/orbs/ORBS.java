package com.spl3.lips.orbs;

import com.spl3.lips.files.DirectoryReader;
import com.spl3.lips.operations.ORBSLogger;
import com.spl3.lips.operations.SourceExecutor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by peacefrog on 9/30/17.
 * Time 10:17 PM
 */
public class ORBS {

	final static Logger logger = Logger.getLogger(ORBS.class);
	final static Logger compileLogger = Logger.getLogger("compileLogger");
	final static Logger testLogger = Logger.getLogger("testLogger");
	final static Logger sliceLogger = Logger.getLogger("projectedLogger");
	final static Logger executionLogger = Logger.getLogger("executionLogger");
	final static Logger statisticsLogger = Logger.getLogger("statisticsLogger");
	final static String FAIL = "FAIL";
	private String outputPath = "work";
	private String sourcePath = "example/orig"/*"toh"*/;

	//	 Size of the deletion window
	private int delta = 3;
	//  Number of ignored lines
	private  int numberOfIgnoredChanges = 0;
	//	Number of compilations
	private int numberOfCompilation = 0;
	//  Number of cached compilations
	private int numberOfCachedCompilation= 0;
	//	Number of cached executions
	private int numberOfCachedExecutions = 0;
	//	Number of executions
	private  int numberOfExecutions = 0;
	//  Number of main loop iterations
	private int numberOfIterations= 0;

	private File [] files;
	private String [][] lines ;
	private boolean [] deleted ;
	private Map<String, Integer> startLines;
	private Map<String, String> sliceCache;
	private Map<String, String> resultCache;
	private String original;
	private Map<String, ArrayList<String>> commandLineParams;

	public static void main(String[] args) {

		ORBSLogger.tieSystemOutAndErrToLog();
//		System.out.println(DirectoryReader.getInstance().getRepository().getAllFiles());
//		SourceExecutor.getInstance().compileJavaFile(new File("/home/peacefrog/Dropbox/orbs/projects/example/work/checker.java"));
//		SourceExecutor.getInstance().compileCFile(new File("/home/peacefrog/Dropbox/orbs/projects/example/work/reader.c"));

//		ORBS orbs = new ORBS();
//		orbs.setup();
//		orbs.createFiles(orbs.deleted, outputPath);
//		orbs.runOriginal();
//		orbs.runORBS();
//		orbs.createFiles(orbs.deleted, outputPath);
//		orbs.success();
//		logger.info(orbs.hash());

//		try {
//			SourceExecutor.getInstance().compileBatchFiles(new File(outputPath));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		SourceExecutor.getInstance().executePythonFile(new File(outputPath + "/glue.py"), Collections.singletonList("10 00000"));

	}

	public void runSlice(){
		setup();
		createFiles(deleted, outputPath);
		runOriginal();
		runORBS();
		createFiles(deleted, outputPath);
		success();
	}

	private void setup(){
		DirectoryReader.getInstance().init(sourcePath);
		files = DirectoryReader.getInstance().getRepository().getAllFiles().toArray(new File[0]);
		int numberOfLines = DirectoryReader.getInstance().getRepository().getAllFilesLineCount(files);

		lines = new String[numberOfLines + 1][];
		deleted = new boolean[numberOfLines+1];
		startLines = new HashMap<>();
		sliceCache = new HashMap<>();
		resultCache = new HashMap<>();

		Arrays.fill(deleted, false);

		for (int i = 0; i < numberOfLines + 1; i++) {
			lines[i] = new String[2];
		}

		final int[] currentLine = {0};

		for(File file : files){
			startLines.put(file.getPath() , currentLine[0]);
			try {
				FileUtils.readLines(file, Charset.defaultCharset()).forEach(o ->{
					lines[currentLine[0]][0] = file.getPath();
					lines[currentLine[0]][1] = (String) o;
					currentLine[0]++;
				} );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		logger.info(Arrays.deepToString(lines));

		try {
			FileUtils.cleanDirectory(new File("store"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runOriginal() {
		String compileOutput = compile("Original");
		if (!compileOutput.equals(FAIL)){
			original = execute("Original");
			testLogger.info(original);
		}
		else {
			original = "";
		}
	}


	public void runORBS(){
		boolean reduced = true;
		int step = 1;
		int start = 0;

		while (reduced){
			reduced = false;
			this.numberOfIterations += 1;
			int line = start;

			while (line >= 0 && line < lines.length-1) {
				String currentFile = lines[line][0];
				int currentLine = line + 1 - startLines.get(lines[line][0]);

//         skip over all deleted lines
				if (deleted[line]) {
					logger.trace(currentFile + ":" + currentLine + ":.\n");
					line += step;
					continue;
				}

				boolean[] attempted = Arrays.copyOf(deleted, deleted.length);


				String status = "";
				boolean cached = false;
				String rc = "";

				int j = 1;
				int ij = line;
				while (j <= delta) {
					while (ij >= 0 && ij < deleted.length - 1 && deleted[ij]) {
						ij += step;
					}

					if (ij < 0 || ij >= deleted.length - 1) {
						rc = "FAIL";
						break;
					}
					if(checkConditional(lines[ij][1])){
						ij += step;
						j += 1;
						continue;
					}
					attempted[ij] = true;

					System.out.println("* " + numberOfIterations + " delete " + (line + 1) + "-" + (ij + 1) + " in " + currentFile + " at " + currentLine);

					boolean cachedComputation = false;
					String checksum = hash(attempted);
					rc = "";

					if (sliceCache.containsKey(checksum)) {
						cachedComputation = true;
						rc = sliceCache.get(checksum);
						numberOfCachedCompilation++;
						System.out.println("comp cached " + numberOfCachedCompilation + ":");
					} else {
						cachedComputation = false;
//						create the files and compile them
						createFiles(attempted, outputPath);
						rc = compile(Integer.toString(line + 1) + "-" + Integer.toString(ij + 1));
						System.out.print("compile " + numberOfCompilation + ":");
						sliceCache.put(checksum, rc);
					}

					if (rc.equals(FAIL)) {
						status = "F";
						System.out.println(FAIL);
						ij += step;
						j += 1;
					} else {
//                      compilation succeeded.
						System.out.print("OK");
						break;
					}
				}

			boolean passes = false;
			String projected;
			if (!rc.equals(FAIL)) {
//                     check if we cached the result for this program instance
				if (resultCache.containsKey(rc)) {
//                      executed this before
					cached = true;
					projected = resultCache.get(rc);
					numberOfCachedExecutions += 1;
					System.out.print("cached " + numberOfCachedExecutions + ":");
				} else {
//                      execute and capture the projected trajectory.
					cached = false;
					if(numberOfCompilation != 77)projected = execute(Integer.toString(line + 1) + "-" + Integer.toString(ij + 1));
					else projected = "";
					sliceLogger.info(projected);
					resultCache.put(rc, projected);
					System.out.print("execution " + numberOfExecutions + ":");
				}
//					if (projected.equals(original)) {
				if (Objects.equals(projected, original)) {
//                      the projected trajectory has not changed and the
//                      slice is valid.
					System.out.println("UNCHANGED");
					passes = true;
					createFiles(attempted, "store" + File.separator + "Slice" + numberOfExecutions);
				} else {
//                      the projected trajectory has changed, the slice
//                      is not valid.
					System.out.println("CHANGED");
					status = "C";
				}
			}

			if (passes) {
//                  The deletion has produced a valid slice.Continue
//                  deletion on this valid slice and try to deleted more.
				deleted = attempted;
				reduced = true;
				if (cached) {
					status = "Dc";
				} else {
					status = "D";
				}
				for (int i = 0; i < j; i++) {
					logger.info(currentFile + ":" + Integer.toString(currentLine + i) + ":" + status + "\n");
					line += j * step;
				}
			} else {
//                  It is not possible to delete the current line.Try the
//                  next one.
				line += step;
				if (cached) {
					logger.info(currentFile + ":" + currentLine + ":" + status + "c\n");
				} else {
					logger.info(currentFile + ":" + currentLine + ":" + status + "\n");
				}
			}
		}
	}
}

	private boolean checkConditional(String line) {
		boolean conditional= false;
		String temp  = line.toUpperCase();

		if(temp.contains("IF")
				|| temp.contains("ELSE IF")
				|| temp.contains("ELSE")
				|| temp.contains("ELSEIF")
				|| temp.contains("ELIF")
				|| temp.contains("END")
				|| temp.contains("BEGIN")
				|| temp.contains("EIF")
				|| temp.contains("FI")
				|| temp.contains("FI END")
				|| temp.contains("IF END")
				|| temp.contains("ELSE END")
				|| temp.contains("IF BEGIN")
				|| temp.contains("ELSE BEGIN")
				) conditional = true;

		return conditional;
	}

	private String compile(String log) {

		this.numberOfCompilation++;
		String output = "";
		boolean success = false;
		try {
			success = SourceExecutor.getInstance().compileBatchFiles(new File(outputPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(success){
			File[] files = new File(outputPath).listFiles();

			ArrayList<File>fileArrayList = new ArrayList<>();

			for (File file : files) {
				if(FilenameUtils.isExtension(file.getName() , SourceExecutor.getInstance().getCompilableExtensions())){
					fileArrayList.add(file);
				}
			}
			output= fileCheckSum(fileArrayList.toArray(new File[0]));
		}
		if(StringUtils.isEmpty(output)){
			output = FAIL;
		}

		compileLogger.debug("C " + log + " " + output);
		return output;
	}

	private String execute(String log)  {

		this.numberOfExecutions++;
		Map<String , ArrayList<String>> rootFileArgs = commandLineParams;
//		ArrayList<String> args = new ArrayList<>();
//		args.add("10 00");
//		rootFileArgs.put("glue.py" , args );

//		args.add("10");
//		rootFileArgs.put("TowerOfHanoi.class" , args );
//		args.add("1 1");
//		rootFileArgs.put("mbe.cout" , args );
		String output = "";
		try {
			output = SourceExecutor.getInstance().executeBatchFiles(new File(outputPath),rootFileArgs);
			output = output.trim();
			executionLogger.info("Execution: " + output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public void createFiles(boolean[] sliced, String output){

		File directory = new File(output);
		if (! directory.exists()){
			directory.mkdirs();
			// If you require it to make the entire directory path including parents,
			// use directory.mkdirs(); here instead.
		}
		try {
			FileUtils.cleanDirectory(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0 ; i < lines.length-1 ; i++) {
			File fileName = new File(lines[i][0]);
			String fileContent = lines[i][1];

			if(!sliced[i]){
				try {
					FileUtils.write(new File(output + File.separator + fileName.getName()) ,
							fileContent + System.lineSeparator() ,
							Charset.defaultCharset(),
							true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void success() {
		int count_lines = 0;
		int count_deletions = 0;
		int i = 0;
		while (i < deleted.length-1) {
			count_lines += 1;
			if (!deleted[ i]) {
				File fileName = new File(lines[i][0]);
				String fileContent = lines[i][1];

				System.out.println(Integer.toString(i +1) + " " + fileName.getName() + " "
						+ fileContent);
			}
			else {
				count_deletions += 1;
				System.out.println(Integer.toString( i +1) + " -----");
			}
			i += 1;
		}

//    # on screen
		statistics();
//    # to log file
		statisticsLogger.info(numberOfIgnoredChanges + " ignored");
		statisticsLogger.info(numberOfCompilation + " compilations");
		statisticsLogger.info(numberOfCachedCompilation + " cached compilations");
		statisticsLogger.info(numberOfExecutions + " executions");
		statisticsLogger.info(numberOfCachedExecutions + " reuses");
		statisticsLogger.info(numberOfIterations + " iterations");
		statisticsLogger.info(count_deletions + " deletions");
		statisticsLogger.info(count_lines + " lines");

	}

	private void statistics() {
		int count_lines = 0;
		int count_deletions = 0;
		int i = 0;
		while (i < deleted.length-1) {
			count_lines += 1;
			if (deleted[i]) {
				count_deletions += 1;
			}
			i += 1;
		}
		System.out.println("ORBS needed" );
		System.out.println (numberOfCompilation+ " compilations");
		System.out.println (numberOfCachedCompilation+ " cached compilations");
		System.out.println (numberOfExecutions+ " executions");
		System.out.println (numberOfCachedExecutions+ " cached executions in");
		System.out.println (numberOfIterations+ " iterations.");
		System.out.println ("ORBS deleted " + count_deletions+ " of " + count_lines + " lines");
		System.out.println (numberOfIgnoredChanges+ " ignored.");

	}

	private String parseOutputs(String original){
		String[] lines = original.split("\n");
		String output = "";
		for (int i = 0; i < lines.length; i++) {
			String [] parsed = lines[i].split(":");
			if(parsed.length > 0) {
				if(i == 0 ){
					output = output.concat(parsed[parsed.length-1]);
				}
				else {
					output = output.concat("\n").concat(parsed[parsed.length-1]);
				}
			}
		}

		return output;
	}

	private String hash(boolean[] sliced){
		MessageDigest md5 = DigestUtils.getMd5Digest();

		for (boolean b : sliced) {
			md5.update(Boolean.toString(b).getBytes());
		}
//		md5.update("pass".getBytes());
//		return  DigestUtils.md5Hex("pass");
		//convert the byte to hex format method 1
		byte[] mdbytes = md5.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

//		System.out.println("Digest(in hex format):: " + sb.toString());
		return ( sb.toString());

	}

	public String fileCheckSum(File[] filePaths){

		StringBuilder digestBuilder = new StringBuilder("");
		for (File filePath : filePaths) {

			try {
				digestBuilder.append( DigestUtils.md5Hex(FileUtils.readFileToByteArray(filePath)));
				digestBuilder.append(" ").append(filePath.getName());
				digestBuilder.append(System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return digestBuilder.toString();
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setCommandLineParams(Map<String, ArrayList<String>> commandLineParams) {
		this.commandLineParams = commandLineParams;
	}
}
