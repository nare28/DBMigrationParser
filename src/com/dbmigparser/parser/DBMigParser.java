package com.dbmigparser.parser;

import static com.dbmigparser.utils.Constants.FILE_NAME;
import static com.dbmigparser.utils.Constants.OUT_DIR;
import static com.dbmigparser.utils.Constants.SOURCE_DIR;
import static com.dbmigparser.utils.Constants.LINE_PARSER_CFG_RULES;
import static com.dbmigparser.utils.Constants.BLOCK_PARSER_CFG_RULES;

public class DBMigParser {

	public static void main(String[] args) {
		DBRuleParser parser = null;
		
		parser = new DBRuleParser(LINE_PARSER_CFG_RULES);
//		parser.process(SOURCE_DIR, OUT_DIR, FILE_NAME);
		
		parser = new DBRuleParser(BLOCK_PARSER_CFG_RULES);
		parser.process(SOURCE_DIR, OUT_DIR, FILE_NAME);
	}

}
