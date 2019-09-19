package com.dbmigparser.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

	private static final String LOWER_PATTERN = "(?i)LOWER\\((((')?(\\w+\\.)?\\w+(')?)|'')\\)";

	public static boolean isConstantVal(String val) {
		boolean isConstantVal = false;
		// String Value
		if (val.startsWith("'") && val.endsWith("'")) {
			isConstantVal = true;
		} // Boolean
		else if (val.equalsIgnoreCase("FALSE") && val.equalsIgnoreCase("TRUE")) {
			isConstantVal = true;
		} // Numeric
		else {
			try {
				Double.parseDouble(val);
				isConstantVal = true;
			} catch (Exception e) {

			}
		}
		return isConstantVal;
	}

	public static String valueExtractor(String txt, String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(txt);
		int i = 0;
		System.out.println("***********    (" + regEx + ")  ****************");
		System.out.println("Text=" + txt);
		String token = null;
		while (matcher.find()) {
			try {
				token = matcher.group(2);
				System.out.print(i + ") " + token + " ");
				System.out.print(i + ") " + matcher.group() + " ");
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		System.out.println();
		return token;
	}

	private static String valueExtractor1(String currLine, String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(currLine);
		int i = 0;
		String token = null;
		System.out.println(currLine);
		while (matcher.find()) {
			try {
				token = matcher.group(2);
				System.out.print(i + ") " + token + " ");
				System.out.print(i + ") " + matcher.group() + " ");
				if (token.startsWith("'") && token.endsWith("'")) {
					token = token.toLowerCase();
					currLine = currLine.replace(matcher.group(), token);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		System.out.println();
		return currLine;
	}

	public static void main(String[] args) {
		String str = "naresh/sss/dd.txt";
		String fileName = str.substring(str.lastIndexOf("/") + 1);
		System.out.println(str);
		System.out.println(fileName);

//		String str = "########WHERE#LOWER(areadescription)#LIKE#LOWER('%SO%')#OR#LOWER(areadescription)#LIKE#LOWER('%South%');";
//		String[] ele = str.split("\\)");
//		System.out.println("Length="+ele.length);
//		ele = str.split(",");
//		System.out.println("Length="+ele.length);
//		ele = str.split("=");
//		System.out.println("Length="+ele.length);
//		System.out.println("File==="+File.separator);
//		System.out.println(valueExtractor1("AAAA=LOWER(col_name) , SSSSSS=sssss , rt=LOWER('Bhaashini') ", LOWER_PATTERN));
//		System.out.println(valueExtractor1("SSSSS = lower('ABCD') , rrrrr=RRRRR, rt=LOWER('Bhaashini'), AWS_LOWER('AWSSSSS') ", LOWER_PATTERN));
//		String spl = "                ON ((LOWER(es.level32id) = LOWER(vp.employeeid) AND vp.issvparea = 0) OR (es.level32id IS NULL AND LOWER(es.level34id) = LOWER(vp.employeeid) AND vp.issvparea = 1)) AND vp.currentflag = 1";
//		System.out.println(CodeIndentation.generateMultiLines(spl));
	}

	public void regExTests() {
		String txt = "Hello World 1234 - Stars _ 4567 on the sky";
		System.out.println();
		System.out.println(valueExtractor(txt, "^Hello"));
		System.out.println(valueExtractor(txt, "sky$"));
		System.out.println(valueExtractor(txt, "\\d"));
		System.out.println(valueExtractor(txt, "\\s"));
		System.out.println(valueExtractor(txt, "\\w"));
		System.out.println(valueExtractor(txt, "\\w+"));
		System.out.println(valueExtractor(txt, "\\d\\d"));
		System.out.println(valueExtractor(txt, "\\d\\d\\d\\d"));
		System.out.println(valueExtractor(txt, "\\d+"));
		System.out.println(valueExtractor(txt, "[1-5]")); // Range
		System.out.println(valueExtractor(txt, "[1-5]+")); // Range
		System.out.println(valueExtractor(txt, "[a-z]+")); // Range
		System.out.println(valueExtractor(txt, "[A-Z]+")); // Range
		System.out.println(valueExtractor(txt, "[a-zA-Z]+")); // Range

		String[] names = { "Mr. Naresh", "Mr suneel", "Mr Praveen", "Mrs padmaja", "Ms Banga" };
		for (String nm : names) {
			System.out.println(valueExtractor(nm, "Mr\\.?"));
		}

		for (String nm : names) {
			System.out.println(valueExtractor(nm, "Mr\\.?\\s[A-Z]"));
			System.out.println(valueExtractor(nm, "Mr\\.?\\s[A-Z]\\w+"));
		}

		String[] phones = { "Call 8121844028", "Call 812-184-4028", "Call 812.184.4028", "Call 812*184*4028",
				"Call 800.184.4028", "Call 900.184.4028" };
//		for(String ph: phones) {
//			System.out.println(valueExtractor(ph, "\\d\\d\\d.\\d\\d\\d.\\d\\d\\d\\d"));
//		}
		phones = new String[] { "naresh.n@gmail.com", "sssss#aaaaa.com", "naresh-n@gmail.com", "naresh28@gmail.com",
				"naresh28@lpu.edu", "ssss1111@dddd.net", "81111@test.com", "nnnk@ssss.moc", "n88888@test.com",
				"ssssss@8ssss.com", "a11111@b2222.com", "a11111@b2222.com1" };
		for (String ph : phones) {
			System.out.println(valueExtractor(ph, "[a-zA-Z][a-zA-Z0-9.-]+@[a-zA-Z][a-zA-Z0-9]+\\.(com|edu|net)"));
		}

		phones = new String[] { "http://www.gggg.com", "https://www.hggg.com", "https://www.gggg.gov",
				"http://www.gggg.net", "https://hggg.edu", "https://gggg.in", "https:www.hggg.com" };
		for (String ph : phones) {
			System.out.println(valueExtractor(ph, "https?://(www.)?[a-z0-9]+\\.(com|edu|net)"));
		}
//		for(String ph: phones) {
//			System.out.println(valueExtractor(ph, "\\d\\d\\d[.-]\\d\\d\\d[.-]\\d\\d\\d\\d"));
//		}
//		
//		for(String ph: phones) {
//			System.out.println(valueExtractor(ph, "[89]00[.-]\\d\\d\\d[.-]\\d\\d\\d\\d"));
//		}
//		for(String ph: phones) {
//			System.out.println(valueExtractor(ph, "\\d{3}.\\d{3}.\\d{4}"));
//		}

//		System.out.println(valueExtractor("LOWER('111111')", "\\W*LOWER[^\\(]*\\(\\W*(\\w+)"));
//		System.out.println(valueExtractor("LOWER(111111)", "LOWER[(]\\W*(\\w+[)]"));
		System.out.println(valueExtractor("LOWER('ABCD')", LOWER_PATTERN));
		System.out.println(valueExtractor("LOWER('ABCD')", LOWER_PATTERN));
		System.out.println(valueExtractor("LOWER(ABCD)", LOWER_PATTERN));
		System.out.println(valueExtractor("LOWER(11122)", "LOWER[(]'?[A-Za-z0-9]+'?\\)"));
		System.out.println(valueExtractor("LOWER(11122)", "LOWER|(|)"));
		System.out.println(valueExtractor("LOWER('ABCD')", "LOWER|"));

	}
}
