package com.dbmigparser.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryConverted {

	public QueryConverted() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		String[] queries = {
				"UPDATE table_1_a SET schedulecount = COALESCE(b.schedulecount, 0) FROM table_1_a AS a "
						+ "INNER JOIN table_1_b AS b ON a.empid = b.employeeid WHERE b.salart > 999 AND b.dep=10",

				"UPDATE table_2_a SET schedulecount = COALESCE(b.schedulecount, 0) FROM table_2_a AS a "
						+ "INNER JOIN table_2_b AS b ON a.empid = b.employeeid",

				"UPDATE table_3_a SET schedulecount = COALESCE(b.schedulecount, 0), dep = 20 FROM table_3_a AS a "
						+ "INNER JOIN table_3_b AS b ON a.empid = b.employeeid "
						+ "INNER JOIN table_3_c AS c ON a.empid = c.employeeid AND c.employeeid IS NOT NULL "
						+ "WHERE b.salart > 999 AND b.dep=10",

				"UPDATE table_4_a SET schedulecount = COALESCE(b.schedulecount, 0), dep = 20 FROM table_4_a AS a "
						+ "INNER JOIN table_4_b AS b ON a.empid = b.employeeid "
						+ "INNER JOIN table_4_c AS c ON a.empid = c.employeeid AND c.employeeid IS NOT NULL", 
						
				"UPDATE table_5_a SET schedulecount = b.schedulecount, dep = 20 FROM table_5_a AS a "
						+ "INNER JOIN table_5_b AS b ON a.empid = b.employeeid "
						+ "LEFT OUTER JOIN table_5_c AS c ON a.empid = c.employeeid AND c.employeeid IS NOT NULL "
						+ "WHERE c.empid IS NOT NULL AND a.sal > 10000", 
						
				"UPDATE table_5_a SET schedulecount = b.schedulecount, dep = 20 FROM table_5_a AS a "
						+ "INNER JOIN table_5_b AS b ON a.empid = b.employeeid "
						+ "LEFT OUTER JOIN table_5_c AS c ON a.empid = c.employeeid AND c.employeeid IS NOT NULL "
						+ "RIGHT OUTER JOIN table_5_d AS d ON d.empid = c.employeeid AND d.employeeid IS NOT NULL "
						+ "WHERE c.empid IS NOT NULL AND a.sal > 10000", 
				
		};

		String joinTypes = "( INNER JOIN - LEFT JOIN - LEFT OUTER JOIN - RIGHT JOIN - RIGHT OUTER JOIN )";

		String sourcePattern = "UPDATE $#UpdateTable| SET $#SetCmd|( FROM - USING )$##FromTable| AS $#FromAlias|"
				+ joinTypes + "$#JoinTable| AS $#JoinAlias| ON $#JoinFilter|" + joinTypes
				+ "$##OtherJoins| WHERE $#WhereFilter";

		String[] migKeySet = {
				"##FromTable,##OtherJoins,#FromAlias,#JoinAlias,#JoinFilter,#JoinTable,#SetCmd,#UpdateTable,#WhereFilter,",
				"##FromTable,#FromAlias,#JoinAlias,#JoinFilter,#JoinTable,#SetCmd,#UpdateTable,#WhereFilter,",
				"##FromTable,##OtherJoins,#FromAlias,#JoinAlias,#JoinFilter,#JoinTable,#SetCmd,#UpdateTable,",
				"##FromTable,#FromAlias,#JoinAlias,#JoinFilter,#JoinTable,#SetCmd,#UpdateTable,",
				
				};

		String[] migrPatterns = {
				"UPDATE |#UpdateTable| AS |#FromAlias|\n\t|SET |#SetCmd|\n|FROM |#JoinTable|"
						+ " AS |#JoinAlias|\n|##OtherJoins|\n|WHERE |#WhereFilter|\n\t|AND |#JoinFilter",

				"UPDATE |#UpdateTable| AS |#FromAlias|\n\t|SET |#SetCmd|\n|FROM |#JoinTable|"
						+ " AS |#JoinAlias|\n|WHERE |#WhereFilter|\n\t|AND |#JoinFilter",

				"UPDATE |#UpdateTable| AS |#FromAlias|\n\t|SET |#SetCmd|\n|FROM |#JoinTable|"
						+ " AS |#JoinAlias|\n|##OtherJoins|\n|WHERE |#JoinFilter",

				"UPDATE |#UpdateTable| AS |#FromAlias|\n\t|SET |#SetCmd|\n|FROM |#JoinTable|"
						+ " AS |#JoinAlias|\n|WHERE |#JoinFilter",

		};

		Map<String, String> tokensMap = null;
		QueryConverted conv = new QueryConverted();
		for (String query : queries) {
			tokensMap = conv.parseQuery(query, sourcePattern);
			System.out.println(tokensMap);
			conv.findPatternAndMigrate(migKeySet, migrPatterns, tokensMap);
			System.out.println("-----------------------------------------------------------");
		}

//		String str = "Hi my machine IP is 10.20.30.40 and i would like "
//				+ "to access port 80 from the host 23.12.56.34, which internally"
//				+ "connects to 3.90.23.65. Please process the request";
//		Pattern ptn = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
//		System.out.println(captureValues(ptn, str));

	}

	private Map<String, String> parseQuery(String query, String fromPattern) {
		System.out.println("Actual Query ::" + query);
		String[] queryTokens = fromPattern.split("\\|");
		Map<String, String> tokensMap = new HashMap<String, String>();
		String subText = query;
		String currKeyword = null;
		int tokenIndex = -1;
		String prevToken = null;
		String prevKeyword = null;
		for (int i = 0; i < queryTokens.length; i++) {
			String[] oneToken = queryTokens[i].split("\\$");
			currKeyword = oneToken[0];
			if (currKeyword.startsWith("(")) { // Find matching keyword from multiple options
				currKeyword = currKeyword.substring(1, currKeyword.length() - 1);
				String[] keys = currKeyword.split("-");
				currKeyword = null;// reset back to null
				int currIndex = -1;
				int index = 100;
				// Find the best keyword match which occurred first
				for (String k : keys) {
					currIndex = subText.indexOf(k);
					if (currIndex > -1 && index > currIndex) {
						index = currIndex;
						currKeyword = k;
					}
				}
			}
			// If Key is not found (i.e null) in Query, do not look for it and value
			// available in next token.
			if (currKeyword != null) {
				tokenIndex = subText.indexOf(currKeyword);
			} else {
				tokenIndex = -1;
			}

			if (tokenIndex == -1) {
				continue;
			}

			if (prevToken != null) {
				String val = subText.substring(0, tokenIndex).trim();
				if (prevToken.startsWith("##"))
					val = prevKeyword + " " + val;
				tokensMap.put(prevToken, val);
			}

			subText = subText.substring(tokenIndex + currKeyword.length());
			prevToken = oneToken[1];
			prevKeyword = currKeyword.trim();
		}

		if (prevToken.startsWith("##"))
			subText = prevKeyword + " " + subText;

		tokensMap.put(prevToken, subText);

		return tokensMap;
	}

	private Map<String, String> parseQuery2(String query, String fromPattern) {
		String[] tokens = fromPattern.split("\\|");
		Map<String, String> tokensMap = new HashMap<String, String>();
		System.out.println("Actual Query ::" + query);
		System.out.println();

		String subText = query;
		String[] keys = null;
		int currExtractorLoc = 0;
		int prevTokenLoc = 0;

		String token = tokens[0];
		int tokenIndex = subText.indexOf(token);
		subText = subText.substring(tokenIndex + token.length());
		String lastToken = null;
		boolean prevTokenFound = true;
		int i = 1;
		for (; i < tokens.length; i++) {
			token = tokens[i];

			if (token.startsWith("#") && prevTokenFound) { // Token key to store extracted token
				currExtractorLoc = i;
				continue;
			} else if (token.startsWith("(")) { // Find matching keyword from multiple options
				token = token.substring(1, token.length() - 1);
				keys = token.split(" - ");
				int currIndex = -1;
				int index = 100;
				token = null;
				// Find the best keyword match which occurred first
				for (String k : keys) {
					currIndex = subText.indexOf(k);
					if (currIndex > -1 && index > currIndex) {
						index = currIndex;
						token = k;
						tokens[i] = token;
					}
				}
				keys = null;
			}

			// If Key is not found (i.e null) in Query, do not look for it and value
			// available in next token.
			if (token != null)
				tokenIndex = subText.indexOf(token);
			else {
				tokenIndex = -1;
				i++; // skipping next token to process
			}

			if (tokenIndex > -1) {
				// Exact Keyword
//				System.out.println(i + ") Current Token :: " + token);
//				System.out.println(i + ") Key Token :: " + tokens[currExtractorLoc]);
//				System.out.println(i + ") SubText :: " + subText);
//				System.out.println();

				if (tokens[currExtractorLoc].startsWith("##")) // Read prev token value
					tokensMap.put(tokens[currExtractorLoc],
							tokens[prevTokenLoc].trim() + " " + subText.substring(0, tokenIndex).trim());
				else
					tokensMap.put(tokens[currExtractorLoc], subText.substring(0, tokenIndex).trim());
				prevTokenLoc = i;

				subText = subText.substring(tokenIndex + token.length());
				prevTokenFound = true;
			} else {
				prevTokenFound = false;
			}
			lastToken = subText;
		}
		// Exact Keyword
//		System.out.println(i + ") Key Token :: " + tokens[currExtractorLoc]);
//		System.out.println(i + ") SubText :: " + lastToken.trim());
//		System.out.println();
		if (tokens[currExtractorLoc].startsWith("##")) // Read prev token value
			tokensMap.put(tokens[currExtractorLoc], tokens[prevTokenLoc].trim() + " " + lastToken.trim());
		else
			tokensMap.put(tokens[currExtractorLoc], lastToken.trim());
		return tokensMap;
	}

	private String findPatternAndMigrate(String[] migKeySetMatch, String[] migPatterns, Map<String, String> tokensMap) {
		Set<String> keySet = tokensMap.keySet();
		SortedSet<String> sortSet = new TreeSet<String>(keySet);
		Iterator<String> itr = sortSet.iterator();
		String k = null;

		String keyText = "";
		while (itr.hasNext()) {
			keyText = keyText + itr.next() + ",";
		}
		System.out.println("keyText :: " + keyText);
		String migrPattern = null;
		for (int i = 0; i < migKeySetMatch.length; i++) {
			if (migKeySetMatch[i].equals(keyText)) {
				migrPattern = migPatterns[i];
				System.out.println("Matched Pattern :: " + i);
				break;
			}
		}

		if (migrPattern != null) {
			itr = keySet.iterator();
			while (itr.hasNext()) {
				k = itr.next();
				migrPattern = migrPattern.replace(k, tokensMap.get(k));
			}
			migrPattern = migrPattern.replace("|", "");
			System.out.println("Migrated Query :: \n" + migrPattern);
			return migrPattern;
		} else {
			System.out.println("No matched pattern found, unable to migrate query");
			return null;
		}
	}

	public static List<String> captureValues(Pattern ptn, String largeText) {
		Matcher mtch = ptn.matcher(largeText);
		List<String> ips = new ArrayList<String>();
		while (mtch.find()) {
			ips.add(mtch.group());
		}
		return ips;
	}
}
