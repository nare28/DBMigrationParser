package com.dbmigparser.dbblockparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;

public class MergeTranslator extends RuleBase {

	public static final String[] MERGE_PATTERNS = { "MERGE|AS|USING|ON|WHEN MATCHED|THEN|WHEN NOT MATCHED" };

	@Override
	public String applyRule(int linePos, String sqlQuery) {
		if(sqlQuery.startsWith("MERGE ") == false)
			return null;
		
		System.out.println("MergeTranslator :: "+sqlQuery);
		StringBuffer newQuery = new StringBuffer();
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
		newQuery.append("SELECT ");
		return newQuery.toString();
	}

}
