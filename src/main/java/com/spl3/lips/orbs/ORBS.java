package com.spl3.lips.orbs;

import com.spl3.lips.files.DirectoryReader;
import com.spl3.lips.operations.ORBSLogger;
import com.spl3.lips.operations.SourceExecutor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peacefrog on 9/30/17.
 * Time 10:17 PM
 */
public class ORBS {

	final static Logger logger = Logger.getLogger(ORBS.class);
	final static String outputPathName = "work";
	final static String FAIL = "FAIL";

//	 Size of the deletion window
	private int delta = 3;
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
	private Map<String, String> sliceCache;

	public static void main(String[] args) {

		ORBSLogger.tieSystemOutAndErrToLog();
		DirectoryReader.getInstance().init("/home/peacefrog/SPL_LIPS");
		System.out.println(DirectoryReader.getInstance().getRepository().getAllFiles());
//		SourceExecutor.getInstance().compileJavaFile(new File("/home/peacefrog/Dropbox/orbs/projects/example/work/checker.java"));
		SourceExecutor.getInstance().compileCFile(new File("/home/peacefrog/Dropbox/orbs/projects/example/work/reader.c"));
//		ORBS orbs = new ORBS();
//		orbs.setup();
//		orbs.createFiles();

//		logger.info(orbs.hash());

	}

	private void setup(){
		files = DirectoryReader.getInstance().getRepository().getAllFiles().toArray(new File[0]);
		int numberOfLines = DirectoryReader.getInstance().getRepository().getAllFilesLineCount(files);

		lines = new String[numberOfLines + 1][];
		deleted = new boolean[numberOfLines+1];
		startLines = new HashMap<>();
		sliceCache = new HashMap<>();

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
	}


	public void runORBS(){
		boolean reduced = true;
		int step = 1;
		int start = 0;

		while (reduced){
			reduced = false;
			this.numberOfIterations += 1;
			int line = start;

			while (line >= 0 && line < lines.length){
				String currentFile = lines[line][0];
				int currentLine = line+1-startLines.get(lines[line][0]);

//         skip over all deleted lines
				if (deleted[line]) {
					logger.trace(currentFile + ":" + currentLine + ":.\n");
					line += step;
					continue;
				}

				boolean [] attempted = Arrays.copyOf(deleted , deleted.length);


				String status = "";
				boolean cached = false;
				String rc = "";

				int j = 1;
				int ij = line;
				while (j <= delta){
					while (ij >= 0 && ij < deleted.length-1 && deleted[ij]){
						ij += step;
					}

					if (ij < 0 || ij >= deleted.length-1) {
						rc = "FAIL";
						break;
					}

					attempted[ij] = true;

					System.out.println("* " + numberOfIterations + " delete " + (line+1) + "-" + (ij+1)+ " in "+ currentFile + " at "+ currentLine);

					boolean cachedComputation = false;
					String checksum = hash(attempted);
					rc = "";

					if(sliceCache.containsKey(checksum)){
						cachedComputation = true;
						rc = sliceCache.get(checksum);
						numberOfCachedCompilation++ ;
						System.out.println( "comp cached " + numberOfCachedCompilation + ":");
					}
					else {
						cachedComputation = false;
//						create the files and compile them
						createFiles(attempted);
					}
				}
			}
		}
	}

	public void createFiles(boolean[] sliced){

		File directory = new File(outputPathName);
		if (! directory.exists()){
			directory.mkdir();
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
					FileUtils.write(new File(outputPathName + File.separator + fileName.getName()) ,
									fileContent + System.lineSeparator() ,
									Charset.defaultCharset(),
									true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String hash(boolean[] sliced){
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
}
