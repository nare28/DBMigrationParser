package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.TAB_SPACE;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public class OpenBraceShift extends RuleBase {

	// Make sure blank space left at end of the keyword
	public static String[] KEYWORDS = { "CREATE ", "PERFORM ", "SELECT " };

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		String line = null;
		
		for (int i = 0; i < sqlCode.size(); i++) {
			line = sqlCode.get(i);
			String key = findKeyword(line, KEYWORDS);
			if (key == null) {
				newSqlCode.add(line);
			} else if (line.trim().endsWith("(")) {
				newSqlCode.add(line);
			} else {
				String nextLine = sqlCode.get(i + 1);
				if (nextLine.trim().startsWith("(")) {
					newSqlCode.add(line + "(");
					newSqlCode.add(TAB_SPACE + nextLine.replaceFirst("\\(", ""));
					changes.logChange("Added openbrace on keyword '" + key 
							+ "' at line # " + (i + 1));
					i++;
				} else {
					newSqlCode.add(line);
				}
			}
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
