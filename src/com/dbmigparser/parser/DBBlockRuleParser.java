package com.dbmigparser.parser;


import static com.dbmigparser.utils.Constants.BLOCK_PARSER_CFG_RULES;
import static com.dbmigparser.utils.Constants.SPACE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.utils.ChangeLog;

public class DBBlockRuleParser extends DBRuleParser {
	
	public List<String> applyRules(File file) {
		// Read File Content
		List<String> sqlCode = readFile(file);
		
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int queryNum = 1;
		StringBuffer query = new StringBuffer();
		for (String currLine : sqlCode) {
			if (currLine.startsWith("=====")) {
				if (query.length() > 0)
					newSqlCode.addAll(applyRuleSet(query.toString()));
				changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
				query.setLength(0);
				queryNum++;
			} else if (currLine.endsWith(";")) {
				query.append(currLine.trim());
				newSqlCode.addAll(applyRuleSet(query.toString()));
				changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
				query.setLength(0);
				queryNum++;
			} else {
				query.append(currLine.trim());
				query.append(SPACE);
			}
		}
		if (query.length() > 0) {
			newSqlCode.addAll(applyRuleSet(query.toString()));
			changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
		}
		return newSqlCode;
	}

	private List<String> applyRuleSet(String sqlQuery) {
		String[] rules = BLOCK_PARSER_CFG_RULES.split(",");
		List<String> currSqlCode = new ArrayList<String>();
		currSqlCode.add(sqlQuery);
		List<String> newSqlCode = currSqlCode;
		RuleEngine re = null;
		for(String rl: rules) {
			re = new RuleEngine(rl);
			newSqlCode = re.execute(newSqlCode);
			if(newSqlCode == null)
				newSqlCode = currSqlCode;
			else
				currSqlCode = newSqlCode;
		}
		return currSqlCode;
	}
	
}
