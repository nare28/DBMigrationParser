package com.dbmigparser.dblineparser;

import com.dbmigparser.parser.RuleBase;

public class DelBlankLines extends RuleBase {

	public DelBlankLines() {
		
	}
	
	@Override
	public String applyRule(int linePos, String currLine) {
		String newLine = null;
		String trimLine = currLine.trim();
		if (trimLine.length() == 0 && linePos > 1) {
			if (getNextLine(linePos - 2).length() == 0) 
				changes.logChange("Deleted blank line at # " + linePos);
			else
				newLine = currLine;
		} else {
			newLine = currLine;
		}
		return newLine;
	}

}
