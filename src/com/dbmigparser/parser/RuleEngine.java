package com.dbmigparser.parser;

import java.util.List;

import com.dbmigparser.dbblockparser.MergeTranslator;
import com.dbmigparser.dbblockparser.PivotTranslator;
import com.dbmigparser.dbblockparser.SelfDmlTranslator;
import com.dbmigparser.dblineparser.AddNewLines;
import com.dbmigparser.dblineparser.CaseChange;
import com.dbmigparser.dblineparser.CodeIndentation;
import com.dbmigparser.dblineparser.EndStmtWithColon;
import com.dbmigparser.dblineparser.OpenBraceShift;
import com.dbmigparser.dblineparser.SelectStmtShift;
import com.dbmigparser.dblineparser.StringReplace;
import com.dbmigparser.dblineparser.TmpObjRename;
import com.dbmigparser.dblineparser.WordShiftLeftRight;
import com.dbmigparser.utils.ChangeLog;

public class RuleEngine {

	private String config = null;
	private ChangeLog changes = null;
	
	public RuleEngine(String config) {
		this.config = config;
		changes = ChangeLog.getInstance();
	}

	public List<String> executeLines(List<String> sqlCode) {
		String[] rules = config.split(";");
		RuleBase ruleTool = null;
		for (String rule : rules) {
			changes.logLine();
			changes.logChange("Rule Applied # " + rule);
			changes.logLine();
			ruleTool = getRuleTool(rule);
			sqlCode = ruleTool.applyRule(sqlCode);
		}
		return sqlCode;
	}
	
	public String executeBlock(String sqlCode) {
		String[] rules = config.split(";");
		RuleBase ruleTool = null;
		for (String rule : rules) {
			changes.logLine();
			changes.logChange("Rule Applied # " + rule);
			changes.logLine();
			ruleTool = getRuleTool(rule);
			sqlCode = ruleTool.applyRule(1, sqlCode);
		}
		return sqlCode;
	}

	private RuleBase getRuleTool(String rule) {
		RuleBase ruleTool = null;
		switch (rule) {
		// Line Parser Rules Started
		case "end-line-col":
			ruleTool = new EndStmtWithColon();
			break;
		case "code-indent":
			ruleTool = new CodeIndentation();
			break;
		case "keyword-shift":
			ruleTool = new WordShiftLeftRight();
			break;
		case "openbrace-shift":
			ruleTool = new OpenBraceShift();
			break;
		case "str-replace":
			ruleTool = new StringReplace();
			break;
		case "tmpobj-rename":
			ruleTool = new TmpObjRename();
			break;
		case "add-newlines":
			ruleTool = new AddNewLines();
			break;
		case "case-change":
			ruleTool = new CaseChange();
			break;
		case "select-shift":
			ruleTool = new SelectStmtShift();
			break;
		// Line Parser Rules Ended
			
		// Block Parser Rules Started
		case "pivot-trans":
			ruleTool = new PivotTranslator();
			break;
		case "self-dml":
			ruleTool = new SelfDmlTranslator();
			break;
		case "merge-trans":
			ruleTool = new MergeTranslator();
			break;
		// Block Parser Rules Ended
		default:
			break;
		}
		return ruleTool;
	}

}
