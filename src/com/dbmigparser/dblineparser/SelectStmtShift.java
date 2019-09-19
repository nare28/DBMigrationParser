package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.SPACE;

import com.dbmigparser.parser.RuleBase;

public class SelectStmtShift extends RuleBase {

	public static String[] KEYWORDS = { "SELECT", "(SELECT ", "SELECT " };
	
	private String prevChangedLine = null;
	
	public SelectStmtShift() {
		
	}
	
	@Override
	public String applyRule(int linePos, String currLine) {
		String trimLine = currLine.trim();
		if (trimLine.equalsIgnoreCase("SELECT") || trimLine.endsWith("(SELECT")) {
			String nextLine = getNextLine(linePos);
			prevChangedLine = currLine + SPACE + nextLine.trim();
			changes.logChange("Shifted select stmt line at line # " + linePos);
			return null;
		}
		
		if(prevChangedLine != null) {
			currLine = prevChangedLine;
			prevChangedLine = null;
		}
		
		return currLine;
	}

}
