package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.STR_REPLACE;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public class StringReplace extends RuleBase {

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int linePos = 1;
		for (String currLine : sqlCode) {
			for (int i = 0; i < STR_REPLACE.length; i++) {
				
				
				if(currLine.contains(STR_REPLACE[i][0])) {
					currLine = currLine.replace(STR_REPLACE[i][0], STR_REPLACE[i][1]);
					changes.logChange("Replaced keyword '" + STR_REPLACE[i][0] 
							+ "' with '" + STR_REPLACE[i][1] + "' at line # " + linePos);
				}
			}
			newSqlCode.add(currLine);
			linePos++;
		}
		return newSqlCode;
	}

}
