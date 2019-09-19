package com.dbmigparser.dblineparser;

import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.ChangeLog;

public class EndStmtWithColon extends RuleBase {

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		ChangeLog changes = ChangeLog.getInstance();
		int commStrIndex = -1;
		int commEndIndex = -1;
		boolean exists = false;
		int commStarted = -1;
		int commEnded = -1;
		int linePos = 0;
		int lastStmtPos = -1;
		int inlineCommCnt = 0;
		for (String currLine : sqlCode) {

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
					if (currLine.length() == commEndIndex + 3)
						exists = (currLine.charAt(commEndIndex + 2) == ';');
				}
			}
			if(currLine.trim().startsWith("--") && commStarted == -1) {
				commStarted = linePos;
				commEnded = linePos;
			}

			if (inlineCommCnt == 0) {
				if (commStarted == -1 && commEnded == -1)
					lastStmtPos = linePos;
				else if (commEnded > -1) {
					commStarted = -1;
					commEnded = -1;
				}
			}

			if (exists) {
				exists = false;
				changes.logChange("Added colon at line # " + lastStmtPos + " and removed at line # " + linePos);
				newSqlCode.set(lastStmtPos, newSqlCode.get(lastStmtPos) + ";");
				newSqlCode.add(currLine.substring(0, commEndIndex + 2));
				commStarted = -1;
				commEnded = -1;
			} else {
				newSqlCode.add(currLine);
			}

			linePos++;
		}
		return newSqlCode;
	}

	@Override
	public String applyRule(int linePos, String currLine) {
		// TODO Auto-generated method stub
		return null;
	}

}
