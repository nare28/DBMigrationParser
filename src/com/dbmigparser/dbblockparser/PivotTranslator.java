package com.dbmigparser.dbblockparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PivotTranslator extends BlockTranslator {

	public static final String[] PIVOT_PATTERNS = {"SELECT|FROM|PIVOT|FOR|IN " };

	@Override
	public String translateQuery(String sqlCode) {
		int groupCount = 0;
		int goodPtrnIndex = 0;
		System.out.println(sqlCode);
		Pattern pattern = null;
		Matcher matcher = null;

//		for (int i = 0; i < PIVOT_PATTERNS.length; i++) {
//			pattern = Pattern.compile(PIVOT_PATTERNS[i]);
//			matcher = pattern.matcher(sqlCode);
//			System.out.println("matcher.groupCount()="+matcher.groupCount());
//			if (matcher. > groupCount) {
//				groupCount = matcher.groupCount();
//				goodPtrnIndex = i;
//			}
//		}
		int start = 0;
		int end = 0;
		for (int i = 0; i < PIVOT_PATTERNS.length; i++) {
			System.out.println("Valid Pattern=" + PIVOT_PATTERNS[i]);
			pattern = Pattern.compile(PIVOT_PATTERNS[goodPtrnIndex]);
			matcher = pattern.matcher(sqlCode);
			while (matcher.find()) {
				start = matcher.start();
				if(start > 0)
					System.out.println(sqlCode.substring(end, start));
				end = matcher.end();
				System.out.println(matcher.group());
			}
		}
		System.out.println(sqlCode.substring(end));
		
		return sqlCode;
	}

}
