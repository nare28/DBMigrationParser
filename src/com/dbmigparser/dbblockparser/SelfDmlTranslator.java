package com.dbmigparser.dbblockparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;
import static com.dbmigparser.utils.Constants.TAB_SPACE;

public class SelfDmlTranslator extends RuleBase {

	public static final String[] SELF_DML_PATTERNS = {
			"UPDATE | SET | FROM | INNER JOIN | LEFT JOIN | LEFT OUTER JOIN | ON | WHERE " };

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		List<String> newSqlCode = new ArrayList<String>();
		String sqlQuery = sqlCode.get(0);
		if (sqlQuery.startsWith("UPDATE ") == false && sqlQuery.startsWith("DELETE ") == false)
			return null;

		System.out.println("SelfDMLTranslator :: " + sqlQuery);
		int goodPtrnIndex = 0;
		Pattern pattern = null;
		Matcher matcher = null;
		int start = 0;
		int end = 0;
		String substr = null;

		for (int i = 0; i < SELF_DML_PATTERNS.length; i++) {
			try {
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

						case "DELETE":
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
							String fromTable = substr.substring(0, substr.indexOf(" AS ")).trim();
							if (fromTable.equals(tgtTable)) {
								aliasName = substr.substring(substr.indexOf(" AS ") + 4).trim();
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

						case "WHERE":
							break;
						default:
							break;
						}
					}
					prevGroup = matcher.group().trim();
				}
				substr = sqlQuery.substring(end).trim();
				System.out.println(prevGroup + "::" + substr);
				newSqlCode.add(prevGroup + " " + substr);
				newSqlCode.add(TAB_SPACE + "AND " + selfJoinFilter);
			} catch (Exception e) {
				System.out.println("Pattern Failed to parse the code");

			}
		}
		return newSqlCode;
	}

}
