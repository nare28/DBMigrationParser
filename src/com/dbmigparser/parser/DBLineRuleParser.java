package com.dbmigparser.parser;

import java.io.File;
import java.util.List;
import static com.dbmigparser.utils.Constants.LINE_PARSER_CFG_RULES;

public class DBLineRuleParser extends DBRuleParser {

	public List<String> applyRules(File file) {
		// Read File Content
		List<String> sqlCode = readFile(file);
		String[] rules = LINE_PARSER_CFG_RULES.split(",");
		RuleEngine re = null;
		for(String rl: rules) {
			re = new RuleEngine(rl);
			sqlCode = re.execute(sqlCode);
		}
		return sqlCode;
	}
}
