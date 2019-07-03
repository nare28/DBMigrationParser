package com.dbmigparser.parser;

import static com.dbmigparser.utils.Constants.BLOCK_PARSER_CFG_RULES;
import static com.dbmigparser.utils.Constants.LINE_PARSER_CFG_RULES;

import java.io.File;

public class DBMigParser {

	public static void main(String[] args) {
		DBRuleParser parser = null;
		String src = System.getProperty("sourceDir");
		String tgt = System.getProperty("targetDir");
		String fl = System.getProperty("fileName");
		String rules = System.getProperty("rulesSet");
		String ps = System.getProperty("parser");

		System.out.println("Command Example :: java -jar -DsourceDir=/Users/naresh/SQLCode/Source/ "
				+ "-DtargetDir=/Users/naresh/SQLCode/Target/ -DfileName=untranslated.sql "
				+ "-Dparser=line(or block) -DrulesSet=<rules> DBMigrationParser.jar");
		System.out.println("\nLine Parser Rules Set ::" + LINE_PARSER_CFG_RULES);
		System.out.println("\nBlock Parser Rules Set ::" + BLOCK_PARSER_CFG_RULES);
		String currentDir = System.getProperty("user.dir");
		System.out.println("\nWorking directory :: " + currentDir);

		if (src == null || tgt == null) {
			src = currentDir + File.separator + "Source" + File.separator;
			tgt = currentDir + File.separator + "Target" + File.separator;
			System.err.println("\nMissing source and target dir paths, considering defaults as "
					+src+" and "+ tgt);
		}

		if ("block".equalsIgnoreCase(ps)) {
			if (rules == null)
				rules = BLOCK_PARSER_CFG_RULES;
			parser = new DBBlockRuleParser(rules);
		} else {
			if (rules == null)
				rules = LINE_PARSER_CFG_RULES;
			parser = new DBLineRuleParser(rules);
		}
		parser.process(src, tgt, fl);
	}

}
