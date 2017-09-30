package com.spl3.lips.files;

import java.io.File;
import java.util.ArrayList;

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


}
