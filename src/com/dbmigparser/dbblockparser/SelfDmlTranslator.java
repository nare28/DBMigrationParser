package com.dbmigparser.dbblockparser;

import static com.dbmigparser.utils.Constants.KEY_AS;
import static com.dbmigparser.utils.Constants.SPACE;
import static com.dbmigparser.utils.Constants.TAB_SPACE;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;

public class SelfDmlTranslator extends RuleBase {

	public static final String[] SELF_DML_PATTERNS = {
			"UPDATE | SET | FROM | INNER JOIN | LEFT JOIN | LEFT OUTER JOIN | RIGHT JOIN | RIGHT OUTER JOIN | ON | WHERE ",
			"DELETE FROM | USING | INNER JOIN | LEFT JOIN | LEFT OUTER JOIN | RIGHT JOIN | RIGHT OUTER JOIN | ON | WHERE " };

	@Override
	public String applyRule(int linePos, String sqlQuery) {
		int goodPtrnIndex = -1;
		if (sqlQuery.startsWith("UPDATE "))
			goodPtrnIndex = 0;
		else if (sqlQuery.startsWith("DELETE FROM "))
			goodPtrnIndex = 1;

		// Not matching with pattern
		if (goodPtrnIndex < 0)
			return null;

		changes.logChange("SelfDMLTranslator :: " + sqlQuery);
		StringBuffer newQuery = new StringBuffer();
		Pattern pattern = null;
		Matcher matcher = null;
		int start = 0;
		int end = 0;
		String currToken = null;
		String setCommand = null;
		
		pattern = Pattern.compile(SELF_DML_PATTERNS[goodPtrnIndex]);
		matcher = pattern.matcher(sqlQuery);
		String prevGroup = null;
		String tgtTable = null;
		String aliasName = "";
		String aliasNameDml = null;
		boolean selfJoin = false;
		String selfJoinFilter = null;
		
		while (matcher.find()) {
			start = matcher.start();
			if (start > 0)
				currToken = sqlQuery.substring(end, start).trim();
			end = matcher.end();

			if (prevGroup != null) {
				switch (prevGroup) {
				case "DELETE FROM":
					tgtTable = currToken;
					newQuery.append("DELETE FROM ");
					newQuery.append(currToken);
					newQuery.append(" AS <alias_name>");
					newQuery.append(System.lineSeparator());
					break;

				case "UPDATE":
					tgtTable = currToken;
					newQuery.append("UPDATE ");
					newQuery.append(currToken);
					newQuery.append(" AS <alias_name>");
					newQuery.append(System.lineSeparator());
					break;

				case "SET":
					newQuery.append("SET ");
					setCommand = currToken;
					newQuery.append("<set_cmd>");
					newQuery.append(System.lineSeparator());
					break;

				case "FROM":
				case "USING":
					String fromTable = null;
					if (currToken.contains(KEY_AS)) {
						fromTable = currToken.substring(0, currToken.indexOf(KEY_AS)).trim();
						aliasName = currToken.substring(currToken.indexOf(KEY_AS) + 4).trim();
					} else {
						String[] arrTbl = currToken.split(SPACE);
						fromTable = arrTbl[0].trim();
						if(arrTbl.length > 1)
							aliasName = arrTbl[1].trim();
					}

					if (fromTable.equals(tgtTable)) {
						selfJoin = true;
					} else {
						newQuery.append(prevGroup + SPACE);
						newQuery.append(currToken);
						newQuery.append(System.lineSeparator());
					}
					break;

				case "INNER JOIN":
					if (selfJoin) {
						newQuery.append("FROM ");
						newQuery.append(currToken);
					} else {
						newQuery.append(prevGroup + SPACE);
						newQuery.append(currToken);
					}
					newQuery.append(System.lineSeparator());
					break;

				case "LEFT JOIN":
				case "LEFT OUTER JOIN":
				case "RIGHT JOIN":
				case "RIGHT OUTER JOIN":
					if (selfJoin) {
						newQuery.append("FROM ");
						newQuery.append(tgtTable);
						newQuery.append(KEY_AS + aliasName);
						aliasNameDml = aliasName + "_dml";
						newQuery.append(System.lineSeparator());
						newQuery.append(prevGroup + SPACE);
						newQuery.append(currToken);
						
					} else {
						newQuery.append(prevGroup + SPACE);
						newQuery.append(currToken);
					}
					newQuery.append(System.lineSeparator());
					break;

				case "ON":
					if (selfJoin) {
						selfJoinFilter = currToken;
						if(aliasNameDml != null) {
							newQuery.append("ON ");
							newQuery.append(currToken);
							newQuery.append(System.lineSeparator());
							selfJoinFilter = extractSelfTableKeys(selfJoinFilter, aliasName, aliasNameDml);
							if(setCommand != null)
								setCommand = setCommand.replace(aliasName+".", aliasNameDml+".");
							aliasName = aliasNameDml;
							aliasNameDml = null;
						}
						selfJoin = false;
					} else {
						newQuery.append("ON ");
						newQuery.append(currToken);
						newQuery.append(System.lineSeparator());
					}
					break;
				default:
					break;
				}
			}
			prevGroup = matcher.group().trim();
		}
		
		currToken = sqlQuery.substring(end).trim();
		changes.logChange("PrevGroup="+prevGroup+", LastToken="+currToken);
		changes.logChange("SelfJoinFilter="+selfJoinFilter + ", SelfJoin="+selfJoin);

		if ("WHERE".equalsIgnoreCase(prevGroup) && selfJoinFilter != null) {
			boolean endsColon = currToken.endsWith(";");
			if(endsColon)
				newQuery.append("WHERE " + currToken.substring(0, currToken.length() - 1));
			else
				newQuery.append("WHERE " + currToken);
			
			newQuery.append(System.lineSeparator());
			newQuery.append(TAB_SPACE);
			newQuery.append("AND ");
			newQuery.append(selfJoinFilter);
			if(endsColon)
				newQuery.append(";");
		} else if ("ON".equalsIgnoreCase(prevGroup) && selfJoin) {
			newQuery.append("WHERE ");
			newQuery.append(currToken);
		} else {
			return null;
		}
		
		String sql = newQuery.toString().replace("<alias_name>", aliasName);
		if(setCommand != null) {
			sql = sql.replace("<set_cmd>", setCommand);
		}
		return sql;
	}

	private String extractSelfTableKeys(String selfJoinFilter, String aliasName, String aliasNameDml) {
		String[] filters = selfJoinFilter.split("(\\(|\\s|\\=|\\))");
		aliasName = aliasName + ".";
		aliasNameDml = aliasNameDml + ".";
		Set<String> keys = new HashSet<String>();
		for(String fl: filters) {
			if(fl.startsWith(aliasName))
				keys.add(fl);
		}
		StringBuffer newFilter = new StringBuffer();
		for(String k: keys) {
			if(newFilter.length() > 0)
				newFilter.append(" AND ");
			newFilter.append(k.replace(aliasName, aliasNameDml));
			newFilter.append(" = ");
			newFilter.append(k);
		}
		return newFilter.toString();
	}

}
