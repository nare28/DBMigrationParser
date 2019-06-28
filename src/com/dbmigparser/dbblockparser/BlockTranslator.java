package com.dbmigparser.dbblockparser;

import static com.dbmigparser.utils.Constants.SPACE;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public abstract class BlockTranslator extends RuleBase {

	public abstract List<String> translateQuery(String sqlCode);

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int queryNum = 1;
		StringBuffer query = new StringBuffer();
		for (String currLine : sqlCode) {
			if (currLine.startsWith("=====")) {
				if (query.length() > 0)
					newSqlCode.addAll(translateQuery(query.toString()));
				changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
				query.setLength(0);
				queryNum++;
			} else if (currLine.endsWith(";")) {
				query.append(currLine.trim());
				newSqlCode.addAll(translateQuery(query.toString()));
				changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
				query.setLength(0);
				queryNum++;
			} else {
				query.append(currLine.trim());
				query.append(SPACE);
			}
		}
		if (query.length() > 0) {
			newSqlCode.addAll(translateQuery(query.toString()));
			changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
		}
		return newSqlCode;
	}

}
