package com.dbmigparser.utils;

public class Constants {
	
	public static final String LINE_PARSER_CFG_RULES = "end-line-col,str-replace,tmpobj-rename,"
			+ "keyword-shift,openbrace-shift,add-newlines,code-indent";
	
	public static final String BLOCK_PARSER_CFG_RULES = "pivot-trans";
	

	public static final String BLANK = "";
	public static final String SPACE = " ";
	public static final String TAB_SPACE = "    ";
	public static final int TAB_LENGTH = 4;
	public static final String[] DELIM_CHARS = {"#", "%", "^", "XZ"};
	public static final String FILE_PREFIX = "c_";
	
	public static final int MAX_LINE_CHARS = 100;
	public static final int MAX_TOKEN_CHARS = 60;
	
	public static final String SOURCE_DIR = "/Users/nanarah/SCT_Test/SQLServer/";
	public static final String OUT_DIR = "/Users/nanarah/SCT_Test/PG/";
	public static final String FILE_NAME = "untranslated.sql";
	
	public static final String STR_REPLACE[][] = {
			{" NUMERIC(10, 0)", " INTEGER"},
			{" NUMERIC(5, 0)", " SMALLINT"},
			{" NUMERIC(15, 0)", " BIGINT"},
			{" NUMERIC(20, 0)", " BIGINT"},
			{" NUMERIC(1, 0)", " SMALLINT"},
			{" BIT", " BOOLEAN"},
			{" COUNT(*)", " COUNT(1)"},
			{" SYSDATETIMEOFFSET()", " NOW()"},
			{"LOWER('')", "''"},
			{"aws_sqlserver_ext.patindex", "STRPOS"}
			
			
	};
}
