package com.dbmigparser.parser;

import com.dbmigparser.dbblockparser.DBBlockRuleParser2;
import com.dbmigparser.dblineparser.DBLineRuleParser;
import com.dbmigparser.utils.ParserProps;

public class DBMigParser {

	public static void main(String[] args) {
		DBRuleParser parser = null;
		ParserProps props = ParserProps.getInstance();

		System.out.println("Command Example :: java -jar -DsourceDir=/Users/naresh/SQLCode/Source/ "
				+ "-DtargetDir=/Users/naresh/SQLCode/Target/ -DfileName=untranslated.sql "
				+ "-Dparser=block -DrulesSet=<rules> -DsubDirsOnly=<subdir_names> DBMigrationParser.jar");

		if ("block".equalsIgnoreCase(props.getProperty("parser"))) {
			System.out.println("Block Parser Started...");
			System.out.println("Block Parser Rules Set ::" + props.getProperty("block.parser.config"));
			parser = new DBBlockRuleParser2(props.getProperty("block.parser.config"));
			parser.process(props.getProperty("sourceDir"), props.getProperty("targetDir"),
					props.getProperty("fileName"));
		}
		if ("Y".equalsIgnoreCase(props.getProperty("ignoreLineParser")) == false) {
			System.out.println("Line Parser Started...");
			System.out.println("Line Parser Rules Set ::" + props.getProperty("line.parser.config"));
			parser = new DBLineRuleParser(props.getProperty("line.parser.config"));
			parser.process(props.getProperty("sourceDir"), props.getProperty("targetDir"),
					props.getProperty("fileName"));
		}
	}

}
