package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.BLANK;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public class AddNewLines extends RuleBase {

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int linePos = 1;
		for (String currLine : sqlCode) {
			if ("END IF;".equals(currLine.trim())) {
				newSqlCode.add(currLine);
				if (sqlCode.get(linePos).length() > 1) {
					newSqlCode.add(BLANK);
					changes.logChange("Added new blank after 'END IF;' at line # " + linePos);
				}
			} else if ("UNION".equals(currLine.trim()) || "UNION ALL".equals(currLine.trim())) {
				if (sqlCode.get(linePos - 2).length() > 1) {
					newSqlCode.add(BLANK);
					changes.logChange("Added new blank before 'UNION' at line # " + linePos);
				}
				newSqlCode.add(currLine);
				if (sqlCode.get(linePos).length() > 1) {
					newSqlCode.add(BLANK);
					changes.logChange("Added new blank after 'UNION;' at line # " + linePos);
				}
			} else if (currLine.trim().startsWith("IF ")) {
				if (sqlCode.get(linePos - 2).length() > 1) {
					newSqlCode.add(BLANK);
					changes.logChange("Added new blank before 'IF' at line # " + linePos);
				}
				newSqlCode.add(currLine);
			} else {
				newSqlCode.add(currLine);
			}
			linePos++;
		}
		return newSqlCode;
	}

}
