package com.dbmigparser.utils;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {

	private List<String> changes = null;
	private static ChangeLog changeLog = null;

	private ChangeLog() {
		changes = new ArrayList<String>();
	}

	public static ChangeLog getInstance() {
		if (changeLog == null)
			changeLog = new ChangeLog();
		return changeLog;
	}

	public void logChange(String msg) {
		changes.add(msg);
	}
	
	public void logLine() {
		changes.add("--------------------------------------------------");
	}

	public void printLog() {
		for(String msg: changes) {
			System.out.println(msg);
		}
	}

	public List<String> getChanges() {
		return changes;
	}
}
