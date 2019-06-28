package com.dbmigparser.dbblockparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PivotTranslator extends BlockTranslator {

	public static final String[] PIVOT_PATTERNS = {"SELECT|FROM|PIVOT|FOR|IN " };

	@Override
	public List<String> translateQuery(String sqlCode) {
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
		String[] actualCols = null;
		String[] pivotCols = null;
		String[] selectCols = null;
		String function = null;
		String fromTable = null;
		String colName = null;
		String prevKeyword = null;
		String substr = null;
		for (int i = 0; i < PIVOT_PATTERNS.length; i++) {
			System.out.println("Valid Pattern=" + PIVOT_PATTERNS[i]);
			pattern = Pattern.compile(PIVOT_PATTERNS[goodPtrnIndex]);
			matcher = pattern.matcher(sqlCode);
			int index = 0;
			while (matcher.find()) {
				start = matcher.start();
				if(start > 0) {
					substr = sqlCode.substring(end, start).trim();
//					System.out.println(index +"::"+substr);
					switch(index) {
						case 1:
							actualCols = substr.split(",");
						break;
						case 3:
							selectCols = substr.split(",");
							break;
						case 4:
							fromTable = substr;
							break;
						case 5:
							function = substr;
							break;
						case 6:
							colName = substr;
							break;
							default:
								break;
					}
				}
				end = matcher.end();
				prevKeyword = matcher.group();
				index++;
			}
		}
		substr = sqlCode.substring(end);
		substr = substr.substring(substr.indexOf("(") + 1, substr.indexOf(")"));
		pivotCols = substr.split(",");
		
		System.out.println("===========================================");
		System.out.println("actualCols="+toString(actualCols));
		System.out.println("selectCols="+toString(selectCols));
		System.out.println("pivotCols="+toString(pivotCols));
		function = function.substring(function.indexOf("(") + 1);
		System.out.println("function="+function);
		fromTable = fromTable.substring(0, fromTable.indexOf(")"));
		System.out.println("fromTable="+fromTable);
		System.out.println("colName="+colName);
		System.out.println("function="+function);
		System.out.println("===========================================");
		substr = minus(actualCols, pivotCols);
		System.out.println("Minus="+substr);
		List<String> newSqlCode = new ArrayList<String>();
		newSqlCode.add("SELECT " + minus(actualCols, pivotCols));
		String colExp = function.replace(selectCols[1].trim(), "CASE WHEN "+colName.toLowerCase()+" = '<new_col>' THEN "
				+selectCols[1].trim().toLowerCase()+" ELSE NULL END") + " AS ";
		
		boolean notfirst = false;
		for(String p: pivotCols) {
			substr = colExp.replace("<new_col>", p.trim()) + p.trim();
			if(notfirst)
				substr = substr + ", ";
			newSqlCode.add(substr);
			notfirst = true;
			
		}
		
		System.out.println(newSqlCode);
		return newSqlCode;
	}

	private String minus(String[] actualCols, String[] pivotCols) {
		StringBuffer query = new StringBuffer();
		boolean found = false;
		for(String a: actualCols) {
			found = false;
			for(String b: pivotCols) {
				if(a.trim().equalsIgnoreCase(b.trim())) {
					found = true;
					break;
				}
			}
			if(found == false) {
				if(query.length() > 0)
					query.append(", ");
				query.append(a.trim().toLowerCase());
			}
		}
		return query.toString();
	}

	private String toString(String[] eles) {
		StringBuffer query = new StringBuffer();
		for(String c: eles) {
			query.append(c);
			query.append(", ");
		}
		return query.toString();
	}

}
