package com.dbmigparser.dblineparser;

import java.io.File;
import java.util.List;

import com.dbmigparser.parser.DBRuleParser;
import com.dbmigparser.parser.RuleEngine;

public class DBLineRuleParser extends DBRuleParser {

	public DBLineRuleParser(String rulesConfig) {
		super(rulesConfig);
	}

	public List<String> applyRules(File file) {
		// Read File Content
		List<String> sqlCode = readFile(file);
		String[] rules = rulesConfig.split(",");
		RuleEngine re = null;
		for(String rl: rules) {
			re = new RuleEngine(rl);
			sqlCode = re.executeLines(sqlCode);
		}
		return sqlCode;
	}
}
