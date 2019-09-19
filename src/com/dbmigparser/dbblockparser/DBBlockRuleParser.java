package com.dbmigparser.dbblockparser;

import static com.dbmigparser.utils.Constants.COMMENT_KEY;
import static com.dbmigparser.utils.Constants.SPACE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbmigparser.parser.DBRuleParser;
import com.dbmigparser.parser.RuleEngine;

public class DBBlockRuleParser extends DBRuleParser {

	public DBBlockRuleParser(String rulesConfig) {
		super(rulesConfig);
	}

	private Map<String, String> commentsCache = null;
	
	public List<String> applyRules(File file) {
		// Read File Content
		List<String> sqlCode = readFile(file);

		List<String> newSqlCode = new ArrayList<String>();
		int queryNum = 1;
		StringBuffer query = new StringBuffer();
		int commentIndex = 1;
		commentsCache = new HashMap<String, String>();
		int charPos = 0;

		int stmtStartLine = 0;
		int stmtEndLine = 0;
		
		for (String currLine : sqlCode) {
			if (currLine.trim().startsWith("--")) {
				commentIndex++;
				commentsCache.put(COMMENT_KEY + commentIndex, currLine);
			} else if (currLine.endsWith(";")) {
				charPos = currLine.indexOf("--");
				if (charPos > 0) {
					commentIndex++;
					currLine = currLine.substring(0, charPos) + COMMENT_KEY + commentIndex;
					commentsCache.put(COMMENT_KEY + commentIndex, currLine.substring(charPos));
				}
				query.append(currLine.trim());
				newSqlCode.addAll(applyRuleSet(query.toString()));
				changes.logChange("Translate Query # " + queryNum + " : " + query.toString());
				query.setLength(0);
				queryNum++;
			} else {
				charPos = currLine.indexOf("--");
				if (charPos > 0) {
					commentIndex++;
					currLine = currLine.substring(0, charPos) + COMMENT_KEY + commentIndex;
					commentsCache.put(COMMENT_KEY + commentIndex, currLine.substring(charPos));
				}
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
		String[] rules = rulesConfig.split(",");
		List<String> currSqlCode = new ArrayList<String>();
		currSqlCode.add(sqlQuery);
		List<String> newSqlCode = new ArrayList<String>();
		newSqlCode.addAll(currSqlCode);
		
		RuleEngine re = null;
		for (String rl : rules) {
			re = new RuleEngine(rl);
			newSqlCode = re.executeLines(newSqlCode);
			if (newSqlCode == null || newSqlCode.size() == 0) {
				newSqlCode = new ArrayList<String>();
				newSqlCode.addAll(currSqlCode);
			} else {
				newSqlCode.add("================================");
				currSqlCode = new ArrayList<String>();
				currSqlCode.addAll(newSqlCode);
			}
		}
		
		List<String> currSqlWithComm = new ArrayList<String>();
		for(String line: currSqlCode) {
			if(line.contains(COMMENT_KEY)) {
				line = line.substring(0, line.indexOf(COMMENT_KEY));
			}
			currSqlWithComm.add(line);
		}
		return currSqlWithComm;
	}

}
