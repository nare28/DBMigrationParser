package com.dbmigparser.dbblockparser;

import static com.dbmigparser.utils.Constants.SPACE;
import static com.dbmigparser.utils.Constants.COMMENT_KEY;
import static com.dbmigparser.utils.Constants.BLANK;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbmigparser.parser.DBRuleParser;
import com.dbmigparser.parser.RuleEngine;
import com.dbmigparser.utils.ChangeLog;

public class DBBlockRuleParser2 extends DBRuleParser {

	public DBBlockRuleParser2(String rulesConfig) {
		super(rulesConfig);
	}

	private Map<String, String> commentsCache = null;

	public List<String> applyRules(File file) {
		// Read File Content
		List<String> sqlCode = readFile(file);

		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		StringBuffer query = new StringBuffer();
		int commentIndex = 1;
		commentsCache = new HashMap<String, String>();
		int commStartPos = 0;
		int commEndPos = 0;
		int stmtStartLine = 1;
		int stmtEndLine = 0;
		int currLineNum = 0;
		boolean commentFound = false;
		String newQuery = null;
		List<String> originalQuery = new ArrayList<String>();

		for (String currLine : sqlCode) {
			changes.logChange(currLine);
			currLineNum++;
			originalQuery.add(currLine);

			// Find commented code
			commStartPos = currLine.indexOf("--");
			if (commStartPos < 0) {
				commStartPos = currLine.indexOf("/*");
				commEndPos = currLine.indexOf("*/");
			} else {
				commEndPos = 0;
			}

			if (commStartPos > 0) {
				commentFound = true;
				commentIndex++;
				if (commEndPos > 0) {
					commentsCache.put(COMMENT_KEY + commentIndex, currLine.substring(commStartPos, commEndPos + 1));
					currLine = currLine.substring(0, commStartPos) + SPACE + COMMENT_KEY + commentIndex + SPACE
							+ currLine.substring(commEndPos + 2);
				} else {
					commentsCache.put(COMMENT_KEY + commentIndex, currLine.substring(commStartPos));
					currLine = currLine.substring(0, commStartPos) + SPACE + COMMENT_KEY + commentIndex;
				}
			} else {
				commentFound = false;
			}

			// Statement End
			if (currLine.trim().endsWith(";") || (commentFound && currLine.contains(";"))) {
				stmtEndLine = currLineNum;
				query.append(currLine);
				changes.logLine();
				changes.logChange("Translate Query from line " + stmtStartLine + " to " 
						+ stmtEndLine + " :: " + query.toString());
				
				newQuery = applyRuleSet(query.toString());
				if (newQuery == null)
					newSqlCode.addAll(originalQuery);
				else
					newSqlCode.add(newQuery);

				stmtStartLine = stmtEndLine + 1;
				query.setLength(0);
				originalQuery = new ArrayList<String>();
			} else if(currLine.trim().length()==0) { // Blank Line 
				newSqlCode.add(BLANK);
			} else {
				query.append(currLine.trim());
				query.append(SPACE);
			}
		}

		if (query.length() > 0) {
			changes.logLine();
			changes.logChange("Translate Query from line " + stmtStartLine + " to " 
					+ stmtEndLine + " :: " + query.toString());
			newQuery = applyRuleSet(query.toString());
			if (newQuery == null)
				newSqlCode.addAll(originalQuery);
			else
				newSqlCode.add(newQuery);
		}
		return newSqlCode;
	}

	private String applyRuleSet(String sqlQuery) {
		String[] rules = rulesConfig.split(",");
		String newSqlCode = null;
		String currSqlCode = sqlQuery;
		RuleEngine re = null;
		boolean changed = false;
		for (String rl : rules) {
			re = new RuleEngine(rl);
			newSqlCode = re.executeBlock(currSqlCode);
			if (newSqlCode != null) {
				changes.logChange("Changed Query :: " + newSqlCode);
				changes.logLine();
				currSqlCode = newSqlCode;
				changed = true;
			}
		}

		if(changed) {
			if (currSqlCode.contains(COMMENT_KEY)) {
				currSqlCode = currSqlCode.substring(0, currSqlCode.indexOf(COMMENT_KEY));
			}
			return currSqlCode;
		} else
			return null;
	}

}
