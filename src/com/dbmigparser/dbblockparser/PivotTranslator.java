package com.dbmigparser.dbblockparser;

import static com.dbmigparser.utils.Constants.BLOCK_LINE;
import static com.dbmigparser.utils.Constants.TAB_SPACE;
import static com.dbmigparser.utils.Utility.isConstantVal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbmigparser.parser.RuleBase;

public class PivotTranslator extends RuleBase {

	public static final String[] PIVOT_PATTERNS = { "SELECT|FROM|PIVOT|FOR| IN" };
	public static final String PG_PIVOT = "(CASE WHEN <col_name> = '<new_col>' THEN <tgt_col> ELSE NULL END) AS ";
	
	@Override
	public List<String> applyRule(List<String> sqlCode) {
		String sqlQuery = sqlCode.get(0);
		List<String> newSqlCode = new ArrayList<String>();
		if(sqlQuery.contains("PIVOT") == false)
			return null;
		System.out.println("PivotTranslator :: "+sqlQuery);
		int goodPtrnIndex = 0;
		Pattern pattern = null;
		Matcher matcher = null;
		int start = 0;
		int end = 0;
		String[] actualCols = null;
		String[] pivotCols = null;
		String funName = null;
		String targetCol = null;
		String fromTable = null;
		String colName = null;
		String substr = null;

		for (int i = 0; i < PIVOT_PATTERNS.length; i++) {
			try {
				pattern = Pattern.compile(PIVOT_PATTERNS[goodPtrnIndex]);
				matcher = pattern.matcher(sqlQuery);
				int index = 0;
				while (matcher.find()) {
					start = matcher.start();
					if (start > 0) {
						substr = sqlQuery.substring(end, start).trim();
//					System.out.println(index +"::"+substr);
						switch (index) {
						case 1:
							actualCols = substr.split(",");
							break;
//					case 3:
//						selectCols = substr.split(",");
//						break;
						case 4:
							fromTable = substr;
							break;
						case 5:
							funName = substr;
							break;
						case 6:
							colName = substr;
							break;
						default:
							break;
						}
					}
					end = matcher.end();
					index++;
				}
				
				substr = sqlQuery.substring(end);
				substr = substr.substring(substr.indexOf("(") + 1, substr.indexOf(")"));
				pivotCols = substr.split(",");

//				System.out.println("actualCols=" + toString(actualCols));
//				System.out.println("selectCols=" + toString(selectCols));
//				System.out.println("pivotCols=" + toString(pivotCols));
//				System.out.println("function=" + funName);
				funName = funName.substring(funName.indexOf("(") + 1);
				targetCol = funName.substring(funName.indexOf("(") + 1, funName.indexOf(")")).toLowerCase();
				funName = TAB_SPACE + funName.substring(0, funName.indexOf("("));
//				System.out.println("function=" + funName);
//				System.out.println("funCol=" + funCol);
//				System.out.println("fromTable=" + fromTable);
				fromTable = fromTable.substring(0, fromTable.indexOf(")"));
//				System.out.println("fromTable=" + fromTable);
//				System.out.println("colName=" + colName);
				
			} catch (Exception e) {
				System.out.println("Pattern Failed to parse the code");

			}
		}

		String[] sg = findSelectAndGroupingCols(actualCols, pivotCols);
		newSqlCode.add("SELECT " + sg[0]);

		String colExp = funName + PG_PIVOT.replace("<col_name>", colName.toLowerCase()).replaceAll("<tgt_col>", targetCol);
		String pivotCol = null;
		for (int k = 0; k < pivotCols.length; k++) {
			pivotCol = pivotCols[k].trim();
			substr = colExp.replace("<new_col>", pivotCol) + pivotCol;
			if (k == pivotCols.length - 1)
				newSqlCode.add(substr);
			else
				newSqlCode.add(substr + ", ");
		}
		newSqlCode.add("FROM " + fromTable + " AS s");
		newSqlCode.add("GROUP BY " + sg[1] + ";");
		newSqlCode.add(BLOCK_LINE);
		return newSqlCode;
	}

	private String[] findSelectAndGroupingCols(String[] actualCols, String[] pivotCols) {
		StringBuffer queryCols = new StringBuffer();
		StringBuffer groupCols = new StringBuffer();

		boolean found = false;

		for (String a : actualCols) {
			found = false;
			for (String b : pivotCols) {
				if (a.trim().equalsIgnoreCase(b.trim())) {
					found = true;
					break;
				}
			}

			if (found == false) {
				String newCol = null;
				String val = null;
				if (a.contains("=")) {
					String[] colDef = a.split("=");
					val = colDef[1].trim();
					newCol = val + " AS " + colDef[0].trim().toLowerCase();
				} else if (a.contains(" AS ") || a.contains(" as ")) {
					String[] colDef = a.split(" AS ");
					if (colDef.length < 2)
						colDef = a.split(" as ");
					val = colDef[0].trim();
					newCol = val + " AS " + colDef[1].trim().toLowerCase();
				} else {
					newCol = a.trim().toLowerCase();
				}

				queryCols.append(newCol);
				queryCols.append(", ");

				if (val == null || isConstantVal(val) == false) {
					if (groupCols.length() > 0)
						groupCols.append(", ");
					groupCols.append(newCol);
				}
			}

		}
		return new String[] { queryCols.toString(), groupCols.toString() };
	}

}
