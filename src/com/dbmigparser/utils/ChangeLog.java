package com.dbmigparser.utils;

import java.util.ArrayList;
import java.util.List;
import static com.dbmigparser.utils.Constants.LINE_DASHES;

public class ChangeLog {

	private List<String> changes = null;
	private static ChangeLog changeLog = null;
	private String logFileName = null;
	private ParserProps props = null;
	
	private ChangeLog() {
		changes = new ArrayList<String>();
		props = ParserProps.getInstance();
		if("Y".equalsIgnoreCase(props.getProperty("logOverride")))
			logFileName = "change_log.log";
		else
			logFileName = "change_log" + (System.currentTimeMillis() / 1000) + ".log";
	}

	public static ChangeLog getInstance() {
		if (changeLog == null)
			changeLog = new ChangeLog();
		return changeLog;
	}

	public void logChange(String msg) {
		System.out.println(msg);
		changes.add(msg);
	}

	public void logLine() {
		System.out.println(LINE_DASHES);
		changes.add(LINE_DASHES);
	}

//	public void printLog() {
//		for (String msg : changes) {
//			System.out.println(msg);
//		}
//	}

	public List<String> getChanges() {
		return changes;
	}

	public void clear() {
		changes.clear();
	}

	public String getLogFileName() {
		return logFileName;
	}
}
