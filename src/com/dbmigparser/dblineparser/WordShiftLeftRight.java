package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.TAB_SPACE;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public class WordShiftLeftRight extends RuleBase {

	// Make sure blank space left at end of the keyword
	public static String[] KEYWORDS_LEFT = {"FROM ", "WHERE ", "GROUP BY ", "ORDER BY ", "JOIN ", "INNER JOIN ", "LEFT OUTER JOIN ", "ON "};
	public static String[] KEYWORDS_RIGHT = {"IN par_", "OUT par_", "OUT p_","in par_", "out par_"};
	
	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int linePos = 1;
		String newLine = null;
		boolean ignoreLeftShift = false;
		String key = null;
		for (String currLine : sqlCode) {
			if(currLine.trim().startsWith("UPDATE ")) {
				ignoreLeftShift = true;
			} else if(currLine.trim().startsWith("SELECT ")) {
				ignoreLeftShift = false;
			} else if(currLine.trim().startsWith("DELETE ")) {
				ignoreLeftShift = false;
			}
			newLine = currLine;
			if(ignoreLeftShift == false) {
				key = findKeyword(newLine, KEYWORDS_LEFT);
				if(key != null) {
					changes.logChange("Shifted '"+key+"' backward at line # "+linePos);
					if(newLine.startsWith(TAB_SPACE))
						newLine = newLine.substring(4);
				}
			}
			
			key = findKeyword(newLine, KEYWORDS_RIGHT);
			if(key != null) {
				changes.logChange("Shifted '"+key+"' forward at line # "+linePos);
				newLine = TAB_SPACE + newLine;
			}
			newSqlCode.add(newLine);
			
			linePos++;
		}
		return newSqlCode;
	}

	private String findKeyword(String currLine, String[] keys) {
		for (String key : keys) {
			if (currLine.trim().startsWith(key))
				return key;
		}
		return null;
	}

}
