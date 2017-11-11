package com.spl3.lips.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by peacefrog on 9/22/17.
 * Time 11:15 PM
 */
public class DirectoryRepository {

	private ArrayList<String> directoryPaths;
	private ArrayList<Directory> directories;

	private String rootPath;

	public DirectoryRepository(String rootPath) {
		this.rootPath = rootPath;

		this.directoryPaths = new ArrayList<>();
		this.directories = new ArrayList<>();
	}

	public ArrayList<Directory> getDirectories() {
		return directories;
	}

	public void addDirectory(Directory directory){
		directories.add(directory);
		directoryPaths.add(directory.getPathName());
	}

	public ArrayList<File> getAllFiles(){

		ArrayList<File> files = new ArrayList<>();
		directories.forEach(directory -> files.addAll(directory.getFiles()));
		return files;
	}

	public Integer getFileLineCount(String filePath){
		Integer numOfLines = 0;

		try (Stream<String> lines = Files.lines(Paths.get(filePath),Charset.defaultCharset())) {
			numOfLines = Math.toIntExact(lines.count());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numOfLines;
	}

	public Integer getAllFilesLineCount(File[] files){
		int totalCount = 0;
		for (File file : files) {
			totalCount += getFileLineCount(file.getPath());
		}
		return totalCount;
	}

	protected String getRootPath(){
		return  rootPath;
	}
}
