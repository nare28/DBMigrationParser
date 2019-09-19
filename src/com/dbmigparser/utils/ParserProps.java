package com.dbmigparser.utils;

import static com.dbmigparser.utils.Constants.DIR_SEP;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParserProps {

	private final Properties props = new Properties();

	private static ParserProps p = null;
	
	private String[] subDirs = null;

	private ParserProps() {
		InputStream in = null;
		try {
			in = this.getClass().getClassLoader().getResourceAsStream("./resources/parser.properties");
			props.load(in);
			loadSystemProps();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(in);
		}
	}

	private void loadSystemProps() {
		String currentDir = System.getProperty("user.dir");
		System.out.println("Working directory :: " + currentDir);
		props.put("currentDir", currentDir);

		// Check for Source and Target Directories
		String prop1 = System.getProperty("sourceDir");
		String prop2 = System.getProperty("targetDir");

		if (prop1 == null || prop2 == null) {
			prop1 = currentDir + DIR_SEP + "Source" + DIR_SEP;
			prop2 = currentDir + DIR_SEP + "Target" + DIR_SEP;
			System.err.println("\nMissing source and target dir paths, considering defaults as " 
					+ prop1 + " and " + prop2);
		} else {
			props.put("sourceDir", prop1);
			props.put("targetDir", prop2);
		}

		prop1 = System.getProperty("fileName");
		if (prop1 != null) {
			props.put("fileName", prop1);
		}

		// Check for Source and Target DB Types
		prop1 = System.getProperty("sourceDB");
		prop2 = System.getProperty("targetDB");
		if (prop1 != null)
			props.put("sourceDB", prop1);
		if (prop2 != null)
			props.put("targetDB", prop2);

		// Check for Type of Parser to use
		prop1 = System.getProperty("parser");

		if ("block".equals(prop1))
			props.setProperty("parser", "block");
		else if ("line".equals(prop1))
			props.setProperty("parser", "line");

		prop2 = System.getProperty("rulesSet");
		if (prop2 != null) {
			if ("block".equals(props.getProperty("parser")))
				props.setProperty("block.parser.config", prop2);
			else if ("line".equals(props.getProperty("parser")))
				props.setProperty("line.parser.config", prop2);
		}
		prop2 = System.getProperty("subDirsOnly");
		if (prop2 != null) {
			props.setProperty("subDirsOnly", prop2);
		}
		subDirs = props.getProperty("subDirsOnly").split(",");

	}

	private void close(InputStream in) {
		try {
			if (in != null)
				in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ParserProps getInstance() {
		if (p == null)
			p = new ParserProps();
		return p;
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public String[] getSubDirs() {
		return subDirs;
	}
}
