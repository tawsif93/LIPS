package com.spl3.lips.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by peacefrog on 9/15/17.
 * Time 3:01 AM
 */
public class DirectoryReader {

	private static DirectoryReader reader;

	private static DirectoryRepository repository;

	private static ArrayList<File> tempFileList;
	private DirectoryReader() {
	}

	public static DirectoryReader getInstance(){
		tempFileList = new ArrayList<>();
		if(reader == null){
			return reader = new DirectoryReader();
		}
		return reader;
	}

	public void init(String projectPath){
		repository = new DirectoryRepository(projectPath);
		DirectoryReader.getInstance().listFilesForFolder(repository.getRootPath());
	}

	public ArrayList<File> listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("Directory : " + fileEntry.getName()+ " path : " + fileEntry.getPath());
				listFilesForFolder(fileEntry);
			} else {
//				System.out.println(fileEntry.getName());
				if(isValidExtension(fileEntry)){
					tempFileList.add(fileEntry);
				}
			}
		}
		return tempFileList;
	}

	private boolean isValidExtension(File fileEntry) {
		for (FileExtension extension : FileExtension.values()){

			if(FilenameUtils.getExtension(fileEntry.getName()).equals(extension.getValue())) {
				return true;
			}
		}
		return false;
	}


	public void listFilesForFolder(final String path){

		final File folder = new File(path);

		Directory directory = new Directory(path);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
//				System.out.println("Directory : " + fileEntry.getName()+ " path : " + fileEntry.getPath());
				listFilesForFolder(fileEntry.getPath());
			} else {
				if(fileEntry != null){
//					System.out.println(fileEntry.getName());
					if(isValidExtension(fileEntry)){
						directory.addFilePath(fileEntry.getPath());
					}
				}
			}
		}
		repository.addDirectory(directory);


		/*try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths
					.filter(Files::isRegularFile)
					.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public String getFileContent(File path){
		try {
			return FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public DirectoryRepository getRepository(){
		return repository;
	}

	public static void main(String[] args) {

//		final File folder = new File("/home/peacefrog/Desktop");
//		try {
//			DirectoryReader.getInstance().listFilesForFolder(folder);
//		}catch (NullPointerException e){
//			e.printStackTrace();
//		}

		System.out.println("\n\n\n");
		repository = new DirectoryRepository("/home/peacefrog/SPL_LIPS");

		DirectoryReader.getInstance().listFilesForFolder(repository.getRootPath());

		System.out.println(repository.getDirectories());
	}
}
