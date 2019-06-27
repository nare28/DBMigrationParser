package com.dbmigparser.parser;

import java.util.List;

public abstract class RuleBase {
	
	public abstract List<String> applyRule(List<String> sqlCode);
	
	public void printNewFile(List<String> changes) {
		for(String msg: changes) {
			System.out.println(msg);
		}
	}
	
}
