package com.dbmigparser.parser;

import static com.dbmigparser.utils.Constants.DIR_SEP;
import static com.dbmigparser.utils.Constants.LINE_SEP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbmigparser.utils.ChangeLog;
import com.dbmigparser.utils.ParserProps;

public abstract class DBRuleParser {

	protected ChangeLog changes = null;

	protected String rulesConfig = null;

	protected ParserProps props = null;

	public DBRuleParser(String rulesConfig) {
		this.rulesConfig = rulesConfig;
		props = ParserProps.getInstance();
	}

	public abstract List<String> applyRules(File file);

	public void process(String srcDirPath, String outDirPath, String fileName) {
		changes = ChangeLog.getInstance();
		changes.logLine();
		changes.logChange("--------------  Start Processing   ---------------");
		changes.logChange("StartTime::" + new Date());
		File dir = new File(srcDirPath);
		if (dir.exists() == false) {
			System.err.println("Source directory does not exists in system. Given Path # " + srcDirPath);
			return;
		}
		// If File Name not passed, consider it as directory
		if (fileName == null) {
			processDir(dir, srcDirPath.length(), null, outDirPath);
		} else {
			processFile(new File(srcDirPath + fileName), null, outDirPath);
		}
		changes.logChange("EndTime::" + new Date());
		changes.logChange("------------- Finished Processing ----------------");
		writeFile(changes.getChanges(), outDirPath + changes.getLogFileName(), true);
		changes.clear();
	}

	private void processDir(File dir, int srcDirLength, String subDir, String outDirPath) {
		for (File fl : dir.listFiles()) {
			// Check for Sub-directories
			if (fl.isDirectory())
				processDir(fl, srcDirLength, fl.getAbsolutePath().substring(srcDirLength), outDirPath);
			else
				processFile(fl, subDir, outDirPath);
		}
	}

	private void processFile(File file, String subDir, String outDirPath) {
		changes.logLine();
		if (file.exists() == false || isValidSubDir(file.getAbsolutePath()) == false
				|| file.getName().endsWith(".sql") == false) {
			changes.logChange("File not found or invalid");
			changes.logLine();
			return;
		}
		
		changes.logChange("### SQLFile # " + file.getAbsolutePath());
		changes.logLine();
		List<String> sqlCode = applyRules(file);
		changes.logLine();
		
		if (subDir != null) {
			File newDir = new File(outDirPath + subDir);
			if(newDir.exists() == false)
				newDir.mkdirs();
			writeFile(sqlCode, outDirPath + subDir + DIR_SEP + file.getName(), false);
		} else {
			writeFile(sqlCode, outDirPath + file.getName(), false);
		}

		changes.logLine();
		writeFile(changes.getChanges(), outDirPath + changes.getLogFileName(), true);
		changes.clear();
	}

	private void writeFile(List<String> newCode, String outFile, boolean append) {
		System.out.println(outFile);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(outFile, append);
			bw = new BufferedWriter(fw);
			for (String msg : newCode) {
				bw.write(msg);
				bw.write(LINE_SEP);
			}

		} catch (FileNotFoundException ex) {
			changes.logChange("Failed to write file # " + outFile);
		} catch (IOException ex) {
			changes.logChange("Error while writing file # " + outFile);
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
			changes.logChange("Failed to open file # " + sourceFile);
		} catch (IOException ex) {
			changes.logChange("Error while reading file # " + sourceFile);
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

	private boolean isValidSubDir(String absolutePath) {
		String[] subDirsOnly = props.getSubDirs();
		if (subDirsOnly == null)
			return true;
		for (String subDir : subDirsOnly) {
			if (absolutePath.contains(subDir))
				return true;
		}
		changes.logChange("Not processing file # " + absolutePath);
		return false;
	}
}
