package com.dbmigparser.parser;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.utils.ChangeLog;
import com.dbmigparser.utils.ParserProps;

public abstract class RuleBase {

	public abstract String applyRule(int linePos, String currLine);

	private int commStrIndex = -1;
	private int commEndIndex = -1;
	private int commStarted = -1;
	private int commEnded = -1;
	int inlineCommCnt = 0;
	protected ChangeLog changes = null;
	protected ParserProps props = null;
	private List<String> curSqlCode = null;

	protected RuleBase() {
		changes = ChangeLog.getInstance();
		props = ParserProps.getInstance();
	}

	public List<String> applyRule(List<String> curSqlCode) {
		int linePos = 1;
		this.curSqlCode = curSqlCode;
		List<String> newSqlCode = new ArrayList<String>();
		for (String curLine : curSqlCode) {
			if (isCommentedLine(linePos, curLine)) {
				newSqlCode.add(curLine);
			} else {
				curLine = applyRule(linePos, curLine);
				// If the line is ignored, ex: moved to prev or next line
				if (curLine != null)
					newSqlCode.add(curLine);
			}
			linePos++;
		}
		return newSqlCode;
	}

	protected String getNextLine(int linePos) {
		return curSqlCode.get(linePos);
	}

	private boolean isCommentedLine(int linePos, String currLine) {
		commStrIndex = currLine.indexOf("/*");
		commEndIndex = currLine.indexOf("*/");

		if (commStrIndex > -1) {
			if (commStarted > -1)
				inlineCommCnt++;
			else
				commStarted = linePos;
		}
		if (commEndIndex > -1) {
			if (inlineCommCnt > 0) {
				inlineCommCnt--;
			} else {
				commEnded = linePos;
			}
		}
		
		if(currLine.trim().startsWith("--") && commStarted == -1) {
			commStarted = linePos;
			commEnded = linePos;
		}

		if (inlineCommCnt == 0) {
			if (commEnded > -1) {
				commStarted = -1;
				commEnded = -1;
				return true;
			} else if (commStarted > -1) {
				return true;
			}
		}

		return false;
	}

}
