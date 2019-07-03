package com.dbmigparser.parser;

import java.io.File;
import java.util.List;

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
			sqlCode = re.execute(sqlCode);
		}
		return sqlCode;
	}
}
