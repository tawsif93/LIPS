package com.spl3.lips.files;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by peacefrog on 9/22/17.
 * Time 11:17 PM
 */
public class Directory {

	private String pathName;
	private ArrayList<String> filePaths;

	public Directory(String pathName) {
		this.pathName = pathName;
		this.filePaths = new ArrayList<>();
	}

	public ArrayList<File> getFiles(){
		return filePaths.stream().map(File::new).collect(Collectors.toCollection(ArrayList::new));
	}

	public String getPathName() {
		return pathName;
	}

	public boolean isDirectoryEmpty(){
		return filePaths.isEmpty();
	}

	public void addFilePath(String pathName){
		filePaths.add(pathName);
	}

	@Override
	public String toString() {
		return "Directory{" +
				"pathName='" + pathName + '\'' +
				'}';
	}
}
