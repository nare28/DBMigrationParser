package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.DELIM_CHARS;
import static com.dbmigparser.utils.Constants.MAX_LINE_CHARS;
import static com.dbmigparser.utils.Constants.MAX_TOKEN_CHARS;
import static com.dbmigparser.utils.Constants.TAB_SPACE;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public class CodeIndentation extends RuleBase {

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int linePos = 1;
		for (String currLine : sqlCode) {
			if (currLine.length() > MAX_LINE_CHARS) {
				List<String> newLines = generateMultiLines(currLine);
				changes.logChange("Line split into "+newLines.size()+" lines at line # " + linePos);
				newSqlCode.addAll(newLines);
			} else {
				newSqlCode.add(currLine);
			}
			linePos++;
		}
		return newSqlCode;
	}

	private List<String> generateMultiLines(String currLine) {
		List<String> newLines = new ArrayList<String>();
		String spaceKey = null;
		for(String k: DELIM_CHARS) {
			if (currLine.contains(k) == false) {
				spaceKey = k;
				break;
			}
		}
		if (spaceKey == null)
			throw new Error("File has some key");

		String splitKey = ",";
		currLine = currLine.replace(" ", spaceKey);
		String[] tokens = currLine.split(splitKey);
		
		if(tokens.length < 2) {
			splitKey = "=";
			tokens = currLine.split(splitKey);
		}
		
		StringBuffer line = new StringBuffer();
		int i = 0;
		String blankText = generateBlankText(tokens[0], spaceKey);
		for (; i < tokens.length - 1; i++) {
			line.append(tokens[i]);
			line.append(splitKey);
			if (line.length() + tokens[i+1].length() > MAX_LINE_CHARS 
					|| tokens[i].length() > MAX_TOKEN_CHARS) {
				newLines.add(line.toString().replace(spaceKey, " "));
				line.setLength(0);
				line.append(TAB_SPACE);
				line.append(blankText);
			}
		}
		line.append(tokens[i]);
		newLines.add(line.toString().replace(spaceKey, " "));

		return newLines;
	}

	private String generateBlankText(String token, String key) {
		String[] spaces = token.split(key);
		int i = 1;
		StringBuffer buff = new StringBuffer();
		for (; i < spaces.length; i++) {
			if (spaces[i].length() > 0)
				break;
			buff.append(" ");
		}
		return buff.toString();
	}

}
