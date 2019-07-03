package com.dbmigparser.parser;

import static com.dbmigparser.utils.Constants.FILE_PREFIX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.utils.ChangeLog;

public abstract class DBRuleParser {

	private ChangeLog changes = null;
	
	protected String rulesConfig = null;
	
	public DBRuleParser(String rulesConfig) {
		this.rulesConfig = rulesConfig;
	}
	
	public void process(String srcDirPath, String outDirPath, String fileName) {
		changes = ChangeLog.getInstance();
		// If File Name not passed, consider it as directory
		if (fileName == null) {
			processDir(new File(srcDirPath), outDirPath);
		} else {
			processFile(new File(srcDirPath + fileName), outDirPath);
		}
	}

	private void processDir(File dir, String outDirPath) {
		for (File fl : dir.listFiles()) {
			// Check for Sub-directories
			if (fl.isDirectory())
				processDir(fl, outDirPath);
			else
				processFile(fl, outDirPath);
		}
	}

	private void processFile(File file, String outDirPath) {
		changes.logLine();
		changes.logChange("---- Start Processing ----");
		changes.logLine();
		changes.logChange("File # "+file.getAbsolutePath());
		if (file.exists() == false || file.getName().startsWith(FILE_PREFIX)) {
			changes.logChange("File not found or invalid");
			changes.logChange("---- Finished Processing ----");
			changes.printLog();
			return;
		}
		
		List<String> sqlCode = applyRules(file);
		
		changes.logLine();
		printNewFile(sqlCode, outDirPath + FILE_PREFIX + file.getName());
		changes.logLine();
		changes.logChange("---- Finished Processing ----");
		changes.logLine();
		printNewFile(changes.getChanges(), outDirPath + FILE_PREFIX + "change_log.txt");
//		changes.printLog();
	}

	public abstract List<String> applyRules(File file);
	
	public void printNewFile(List<String> changes, String outFile) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(outFile);
			bw = new BufferedWriter(fw);
			for(String msg: changes) {
//				System.out.println(msg);
				bw.write(msg);
				bw.write(System.lineSeparator());
			}
			
		} catch (FileNotFoundException ex) {
			logInfo("Failed to write file # " + outFile);
		} catch (IOException ex) {
			logInfo("Error while writing file # " + outFile);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected List<String> readFile(File sourceFile) {
		String line = null;
		FileReader fr = null;
		BufferedReader br = null;
		List<String> sqlCode = null;
		try {
			fr = new FileReader(sourceFile);
			br = new BufferedReader(fr);
			sqlCode = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				sqlCode.add(line);
			}
		} catch (FileNotFoundException ex) {
			logInfo("Failed to open file # " + sourceFile);
		} catch (IOException ex) {
			logInfo("Error while reading file # " + sourceFile);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sqlCode;
	}

	protected void logInfo(String text) {
		System.out.println(text);
	}

}
