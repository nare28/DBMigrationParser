package com.dbmigparser.dblineparser;

import static com.dbmigparser.utils.Constants.DELIM_CHARS;
import static com.dbmigparser.utils.Constants.MAX_LINE_CHARS;
import static com.dbmigparser.utils.Constants.MAX_TOKEN_CHARS;
import static com.dbmigparser.utils.Constants.SPACE;
import static com.dbmigparser.utils.Constants.TAB_SPACE;
import static com.dbmigparser.utils.Constants.COMMA;
import static com.dbmigparser.utils.Constants.EQUAL;

import com.dbmigparser.parser.RuleBase;

public class CodeIndentation extends RuleBase {

	private int newLinesCount;

	public CodeIndentation() {
		super();
	}

	@Override
	public String applyRule(int linePos, String currLine) {
		if (currLine.trim().startsWith("SELECT ") 
				&& currLine.length() > MAX_LINE_CHARS) {
			newLinesCount = 1;
			currLine = generateMultiLines(currLine);
			changes.logChange("Line split into " + newLinesCount 
					+ " lines at line # " + linePos);
		}
		return currLine;
	}

	private String generateMultiLines(String currLine) {
		StringBuffer newLines = new StringBuffer();
		String spaceKey = null;
		for (String k : DELIM_CHARS) {
			if (currLine.contains(k) == false) {
				spaceKey = k;
				break;
			}
		}
		if (spaceKey == null)
			throw new Error("File has some key");

		// Adding extra space to protect last delim char
		currLine = currLine.replace(SPACE, spaceKey) + spaceKey; 
		String splitKey = COMMA;
		String[] tokens = currLine.split(splitKey);
		int prevTokensCount = tokens.length;
		
		tokens = currLine.split("\\)");
		if (prevTokensCount > tokens.length) {
			tokens = currLine.split(splitKey);
		} else {
			splitKey = "\\)";
		}
		prevTokensCount = tokens.length;
		tokens = currLine.split(EQUAL);
		if (prevTokensCount > tokens.length) {
			tokens = currLine.split(splitKey);
			if(splitKey.equals(COMMA) == false)
				splitKey = ")";
		} else {
			splitKey = EQUAL;
		}
		
		StringBuffer line = new StringBuffer();
		int i = 0;
		String blankText = generateBlankText(tokens[0], spaceKey);
		for (; i < tokens.length - 1; i++) {
			line.append(tokens[i]);
			line.append(splitKey);
			if (line.length() + tokens[i + 1].length() > MAX_LINE_CHARS 
					|| tokens[i].length() > MAX_TOKEN_CHARS) {
				newLines.append(line.toString().replace(spaceKey, SPACE));
				newLines.append(System.lineSeparator());
				newLinesCount++;
				line.setLength(0);
				line.append(TAB_SPACE);
				line.append(blankText);
			}
		}
		line.append(tokens[i]);
		newLines.append(line.toString().replace(spaceKey, SPACE));

		return newLines.toString();
	}

	private String generateBlankText(String token, String key) {
		String[] spaces = token.split(key);
		StringBuffer buff = new StringBuffer();
		for (int i = 1; i < spaces.length; i++) {
			if (spaces[i].length() > 0)
				break;
			buff.append(SPACE);
		}
		return buff.toString();
	}

}
