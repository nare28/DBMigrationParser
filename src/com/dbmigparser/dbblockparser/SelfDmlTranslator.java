package com.dbmigparser.dbblockparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;
import static com.dbmigparser.utils.Constants.TAB_SPACE;

public class SelfDmlTranslator extends RuleBase {

	public static final String[] SELF_DML_PATTERNS = {
			"UPDATE | SET | FROM | INNER JOIN | LEFT JOIN | LEFT OUTER JOIN | ON | WHERE ",
			"DELETE FROM | USING | INNER JOIN | LEFT JOIN | LEFT OUTER JOIN | ON | WHERE " };

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		String sqlQuery = sqlCode.get(0);
		int goodPtrnIndex = 0;

		if (sqlQuery.startsWith("UPDATE "))
			goodPtrnIndex = 0;
		else if (sqlQuery.startsWith("DELETE "))
			goodPtrnIndex = 1;

		// Not matching with pattern
		if (goodPtrnIndex < 0)
			return null;

		System.out.println("SelfDMLTranslator :: " + sqlQuery);
		Pattern pattern = null;
		Matcher matcher = null;
		int start = 0;
		int end = 0;
		String substr = null;

		pattern = Pattern.compile(SELF_DML_PATTERNS[goodPtrnIndex]);
		matcher = pattern.matcher(sqlQuery);
		String prevGroup = null;
		String tgtTable = null;
		String aliasName = null;
		boolean selfJoin = false;
		String selfJoinFilter = null;
		while (matcher.find()) {
			start = matcher.start();
			if (start > 0)
				substr = sqlQuery.substring(end, start).trim();
			end = matcher.end();

			if (prevGroup != null) {
				System.out.println(prevGroup + "::" + substr);
				switch (prevGroup) {

				case "DELETE FROM":
					tgtTable = substr;
					newSqlCode.add(prevGroup + " " + substr + " AS ");
					break;

				case "UPDATE":
					tgtTable = substr;
					newSqlCode.add(prevGroup + " " + substr + " AS ");
					break;

				case "SET":
					newSqlCode.add(prevGroup + " " + substr);
					break;

				case "FROM":
				case "USING":
					String fromTable = null;
					if(substr.contains(" AS ")) {
						fromTable = substr.substring(0, substr.indexOf(" AS ")).trim();
						aliasName = substr.substring(substr.indexOf(" AS ") + 4).trim();
					} else {
						String[] arrTbl = substr.split(" ");
						fromTable = arrTbl[0].trim();
						aliasName = arrTbl[1].trim();
					}
					
					if (fromTable.equals(tgtTable)) {
						newSqlCode.set(0, newSqlCode.get(0) + aliasName);
						selfJoin = true;
					} else {
						newSqlCode.add(prevGroup + " " + substr);
					}
					break;

				case "INNER JOIN":
					if (selfJoin) {
						newSqlCode.add("FROM " + substr);
					} else {
						newSqlCode.add(prevGroup + " " + substr);
					}
					break;

				case "LEFT JOIN":
				case "LEFT OUTER JOIN":
					if (selfJoin) {
						newSqlCode.add("FROM " + substr);
					} else {
						newSqlCode.add(prevGroup + " " + substr);
					}
					break;

				case "ON":
					if (selfJoin) {
						selfJoinFilter = substr;
					} else {
						newSqlCode.add(prevGroup + " " + substr);
					}
					selfJoin = false;
					break;
				default:
					break;
				}
			}
			prevGroup = matcher.group().trim();
		}
		substr = sqlQuery.substring(end).trim();
		System.out.println(prevGroup + "::" + substr);
		
		if("WHERE".equalsIgnoreCase(prevGroup)) {
			newSqlCode.add(prevGroup + " " + substr);
			if (selfJoinFilter != null)
				newSqlCode.add(TAB_SPACE + "AND " + selfJoinFilter);
		} else if("ON".equalsIgnoreCase(prevGroup)) {
				newSqlCode.add("WHERE " + substr);
		}
		
		

		return newSqlCode;
	}

}
