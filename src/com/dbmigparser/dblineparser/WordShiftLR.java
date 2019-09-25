package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.TAB_SPACE;

import com.dbmigparser.parser.RuleBase;

public class WordShiftLR extends RuleBase {

	// Make sure blank space left at end of the keyword
	public static String[] KEYWORDS_LEFT = { "FROM ", "WHERE ", "GROUP BY ", "ORDER BY ", "JOIN ", "INNER JOIN ",
			"LEFT OUTER JOIN ", "ON " };
	public static String[] KEYWORDS_RIGHT = { "SET " };

	public WordShiftLR() {
		super();	
	}

	@Override
	public String applyRule(int linePos, String currLine) {
		String newLine = null;
		boolean ignoreLeftShift = false;
		String key = null;
		if (currLine.trim().startsWith("UPDATE ")) {
			ignoreLeftShift = true;
		} else if (currLine.trim().startsWith("SELECT ")) {
			ignoreLeftShift = false;
		} else if (currLine.trim().startsWith("DELETE ")) {
			ignoreLeftShift = false;
		}
		newLine = currLine;
		if (ignoreLeftShift == false) {
			key = findKeyword(newLine, KEYWORDS_LEFT);
			if (key != null) {
				changes.logChange("Shifted '" + key + "' backward at line # " + linePos);
				if (newLine.startsWith(TAB_SPACE))
					newLine = newLine.substring(4);
			}
		}

		key = findKeyword(newLine, KEYWORDS_RIGHT);
		if (key != null) {
			changes.logChange("Shifted '" + key + "' forward at line # " + linePos);
			newLine = TAB_SPACE + newLine;
		}
		return newLine;
	}

	private String findKeyword(String currLine, String[] keys) {
		for (String key : keys) {
			if (currLine.trim().startsWith(key))
				return key;
		}
		return null;
	}

}
