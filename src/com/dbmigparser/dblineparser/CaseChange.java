package com.dbmigparser.dblineparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;

public class CaseChange extends RuleBase {

	public static final String LOWER_PATTERN = "(?i)LOWER\\((((')?(\\%)?(\\w+\\.)?\\w+(\\%)?(')?)|'')\\)";
	public static final String UPPER_PATTERN = "(?i)UPPER\\((((')?(\\%)?(\\w+\\.)?\\w+(\\%)?(')?)|'')\\)";

	private boolean upper = false;
	private boolean lower = false;
	
	public CaseChange() {
		super();
		int caseType = Integer.parseInt(props.getProperty("line.parser.caseType"));
		lower = caseType > 1;
		upper = caseType > 0;
	}

	@Override
	public String applyRule(int linePos, String currLine) {
		if (currLine.contains("LOWER"))
			currLine = valueExtractor(currLine, LOWER_PATTERN, linePos, lower);
		if (currLine.contains("UPPER"))
			currLine = valueExtractor(currLine, UPPER_PATTERN, linePos, upper);
		return currLine;
	}

	private String valueExtractor(String currLine, String regEx, int linePos, boolean toUpper) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(currLine);
		String token = null;
		while (matcher.find()) {
			try {
				token = matcher.group(2);
				if (token.startsWith("'") && token.endsWith("'")) {
					if (toUpper)
						token = token.toUpperCase();
					else
						token = token.toLowerCase();
					currLine = currLine.replace(matcher.group(), token);
					changes.logChange("Replaced '" + matcher.group() + "' with '" + token + "' at line # " + linePos);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return currLine;
	}

}
