package com.dbmigparser.utils;

import java.io.File;

public class Constants {

	public static final String COMMENT_KEY = "tm__c#";
	public static final String EQUAL = "=";
	public static final String COMMA = ",";
	public static final String BLANK = "";
	public static final String SPACE = " ";
	public static final String TAB_SPACE = "    ";
	public static final String BLOCK_LINE = "/*=====================================================================*/";
	public static final int TAB_LENGTH = 4;
	public static final String[] DELIM_CHARS = { "#", "%", "^", "XZ" };
	public static final String KEY_AS = " AS ";

	public static final int MAX_LINE_CHARS = 100;
	public static final int MAX_TOKEN_CHARS = 60;
	public static final String DIR_SEP = File.separator;
	public static final String LINE_SEP = System.lineSeparator();
	public static final String LINE_DASHES = "--------------------------------------------------";

}
