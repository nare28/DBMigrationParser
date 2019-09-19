package com.dbmigparser.utils;

import static com.dbmigparser.utils.Constants.SPACE;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbmigparser.beans.StrReplaceBean;

public class RulesConfig {

	private static RulesConfig p = null;

	List<StrReplaceBean> stringRulesList = null;
	
	List<StrReplaceBean> regexRulesList = null;

	private RulesConfig() {
		stringRulesList = readConfigs("./resources/sql2pg/strreplacerules.json");
//		regexRulesList = readConfigs("./resources/sql2pg/dynareplacerules.json");
	}

	private List<StrReplaceBean> readConfigs(String configFile) {
		List<StrReplaceBean> rrr = new ArrayList<StrReplaceBean>();
		try {
			URL fileName = this.getClass().getClassLoader()
					.getResource(configFile);
			StringBuffer fileContent = readFile(fileName.getFile());
			JSONArray rules = new JSONArray(fileContent.toString());
			Iterator<Object> itr = rules.iterator();
			StrReplaceBean bean = null;
			while (itr.hasNext()) {
				JSONObject obj = (JSONObject) itr.next();
				bean = new StrReplaceBean(obj.getString("rulename"), 
						obj.getString("source"),
						obj.getString("target"));
				if(obj.has("search"))
					bean.setSearch(obj.getString("search"));
				else
					bean.setSearch(obj.getString("source"));
				rrr.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rrr;
	}

	protected StringBuffer readFile(String sourceFile) {
		String line = null;
		FileReader fr = null;
		BufferedReader br = null;
		StringBuffer fileContent = new StringBuffer();
		try {
			fr = new FileReader(sourceFile);
			br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				fileContent.append(line);
				fileContent.append(SPACE);
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Failed to open file # " + sourceFile);
		} catch (IOException ex) {
			System.out.println("Error while reading file # " + sourceFile);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContent;
	}

	public static RulesConfig getInstance() {
		if (p == null)
			p = new RulesConfig();
		return p;
	}

	public List<StrReplaceBean> getStringRules() {
		return stringRulesList;
	}
	
	public List<StrReplaceBean> getDynaRules() {
		return regexRulesList;
	}
}
