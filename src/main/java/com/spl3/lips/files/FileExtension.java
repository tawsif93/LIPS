package com.spl3.lips.files;

/**
 * Created by peacefrog on 9/30/17.
 * Time 9:13 PM
 */
public enum FileExtension {

	java("java"),
	javaClass("class"),
	c("c"),
	cExecutable(".cout"),
	python("py");


	private String value;

	FileExtension(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
