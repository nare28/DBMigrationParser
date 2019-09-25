package com.dbmigparser.parser;

import com.dbmigparser.dbblockparser.DBBlockRuleParser;
import com.dbmigparser.dblineparser.DBLineRuleParser;
import com.dbmigparser.utils.ParserProps;

public class DBMigParser {

	public static void main(String[] args) {
		DBRuleParser parser = null;
		ParserProps props = ParserProps.getInstance();

		System.out.println("Command Example :: java -jar -Dparser.sourceDir=/Users/naresh/SQLCode/Source/ "
				+ "-Dparser.targetDir=/Users/naresh/SQLCode/Target/ -Dparser.fileName=untranslated.sql "
				+ "-Dparser.type=block -Dparser.rulesSet=<rules> -Dparser.subDirsOnly=<subdir_names> DBMigrationParser.jar");

		if ("block".equalsIgnoreCase(props.getProperty("parser.type"))) {
			System.out.println("Block Parser Started...");
			System.out.println("Block Parser Rules Set ::" + props.getProperty("block.parser.config"));
			parser = new DBBlockRuleParser(props.getProperty("block.parser.config"));
			parser.process(props.getProperty("parser.sourceDir"), props.getProperty("parser.targetDir"),
					props.getProperty("parser.fileName"));
			System.out.println("Block Parser Ended !");
		}
		if ("Y".equalsIgnoreCase(props.getProperty("parser.ignoreLineParser")) == false) {
			System.out.println("Line Parser Started...");
			System.out.println("Line Parser Rules Set ::" + props.getProperty("line.parser.config"));
			parser = new DBLineRuleParser(props.getProperty("line.parser.config"));
			parser.process(props.getProperty("parser.sourceDir"), props.getProperty("parser.targetDir"),
					props.getProperty("parser.fileName"));
			System.out.println("Line Parser Ended !");
		}
	}

}
