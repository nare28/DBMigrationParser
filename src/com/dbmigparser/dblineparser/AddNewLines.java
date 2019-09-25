package com.dbmigparser.dblineparser;

import com.dbmigparser.parser.RuleBase;

public class AddNewLines extends RuleBase {

	public AddNewLines() {
		
	}
	
	@Override
	public String applyRule(int linePos, String currLine) {
		String newLine = "";
		String trimLine = currLine.trim();
		
		if ("END IF;".equals(trimLine)) {
			if (getNextLine(linePos).length() > 1) {
				newLine = currLine + System.lineSeparator();
				changes.logChange("Added blank line after 'END IF;' at line # " + linePos);
			} else {
				newLine = currLine;
			}
		} else if ("UNION".equals(trimLine) || "UNION ALL".equals(trimLine)) {
			if (getNextLine(linePos - 2).length() > 1) {
				newLine = System.lineSeparator();
				changes.logChange("Added blank line before 'UNION' at line # " + linePos);
			}
			newLine = newLine + currLine;
			if (getNextLine(linePos).length() > 1) {
				newLine = newLine + System.lineSeparator();
				changes.logChange("Added blank line after 'UNION' at line # " + linePos);
			}
		} else if (trimLine.startsWith("IF ")) {
			if (getNextLine(linePos - 2).length() > 1) {
				newLine = System.lineSeparator();
				changes.logChange("Added blank line before 'IF' at line # " + linePos);
			}
			newLine = newLine + currLine;
		} else {
			newLine = currLine;
		}
		return newLine;
	}

}
