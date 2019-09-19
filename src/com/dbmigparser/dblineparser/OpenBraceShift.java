package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.TAB_SPACE;

import com.dbmigparser.parser.RuleBase;

public class OpenBraceShift extends RuleBase {

	// Make sure blank space left at end of the keyword
	public static String[] KEYWORDS = { "CREATE ", "PERFORM ", "SELECT " };
	private String prevChangedLine = null;
	
	@Override
	public String applyRule(int linePos, String currLine) {
		String key = findKeyword(currLine, KEYWORDS);
		
		if (key != null && currLine.trim().endsWith("(") == false) {
			String nextLine = getNextLine(linePos);
			if (nextLine.trim().startsWith("(")) {
				currLine = currLine + "(";
				prevChangedLine = TAB_SPACE + nextLine.replaceFirst("\\(", "");
				changes.logChange("Added openbrace on keyword '" + key + "' at line # " + linePos);
				return currLine;
			}
		}
		
		if(prevChangedLine != null) {
			currLine = prevChangedLine;
			prevChangedLine = null;
		}
		
		return currLine;
	}


	private String findKeyword(String currLine, String[] keys) {
		for (String key : keys) {
			if (currLine.trim().startsWith(key))
				return key;
		}
		return null;
	}

}
