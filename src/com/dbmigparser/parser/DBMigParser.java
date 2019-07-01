package com.dbmigparser.parser;

import static com.dbmigparser.utils.Constants.FILE_NAME;
import static com.dbmigparser.utils.Constants.OUT_DIR;
import static com.dbmigparser.utils.Constants.SOURCE_DIR;

public class DBMigParser {

	public static void main(String[] args) {
		DBRuleParser parser = null;
		
//		parser = new DBLineRuleParser();
//		parser.process(SOURCE_DIR, OUT_DIR, FILE_NAME);
		
		parser = new DBBlockRuleParser();
		parser.process(SOURCE_DIR, OUT_DIR, FILE_NAME);
	}

}
