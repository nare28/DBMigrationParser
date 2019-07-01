package com.dbmigparser.dbblockparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;

public class MergeTranslator extends RuleBase {

	public static final String[] MERGE_PATTERNS = { "MERGE|AS|USING|ON|WHEN MATCHED|THEN|WHEN NOT MATCHED" };
	public static final String PG_PIVOT = "UPDATE|SET|FROM|INNER JOIN|LEFT OUTER JOIN|ON|WHERE";

	@Override
	public List<String> applyRule(List<String> sqlCode) {
		String sqlQuery = sqlCode.get(0);
		List<String> newSqlCode = new ArrayList<String>();
		if(sqlQuery.startsWith("MERGE ") == false)
			return null;
		System.out.println("MergeTranslator :: "+sqlQuery);
		int goodPtrnIndex = 0;
		Pattern pattern = null;
		Matcher matcher = null;
		int start = 0;
		int end = 0;
		String substr = null;

		for (int i = 0; i < MERGE_PATTERNS.length; i++) {
			try {
				pattern = Pattern.compile(MERGE_PATTERNS[goodPtrnIndex]);
				matcher = pattern.matcher(sqlQuery);
				int index = 0;
				while (matcher.find()) {
					start = matcher.start();
					if (start > 0)
						substr = sqlQuery.substring(end, start).trim();
					System.out.println(index + "::" + substr);
					end = matcher.end();
					index++;
				}
			} catch (Exception e) {
				System.out.println("Pattern Failed to parse the code");

			}
		}
		newSqlCode.add("SELECT ");
		return newSqlCode;
	}

}
