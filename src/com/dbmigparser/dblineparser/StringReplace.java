package com.dbmigparser.dblineparser;

import java.util.List;

import com.dbmigparser.beans.StrReplaceBean;
import com.dbmigparser.parser.RuleBase;
import com.dbmigparser.utils.RulesConfig;

public class StringReplace extends RuleBase {

	private List<StrReplaceBean> rules = null;

	public StringReplace() {
		super();
		RulesConfig conf = RulesConfig.getInstance();
		rules = conf.getStringRules();
	}

	@Override
	public String applyRule(int linePos, String currLine) {
		for (StrReplaceBean rl : rules) {
			if (currLine.contains(rl.getSearch())) {
				currLine = currLine.replace(rl.getSource(), rl.getTarget());
				changes.logChange("Replaced keyword '" + rl.getSource() + "' with '" 
						+ rl.getTarget() + "' at line # " + linePos);
			}
		}
		return currLine;
	}

}
