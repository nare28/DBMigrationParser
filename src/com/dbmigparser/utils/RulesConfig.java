package com.dbmigparser.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dbmigparser.beans.StrReplaceBean;

public class RulesConfig {

	private static RulesConfig p = null;

	List<StrReplaceBean> stringRulesList = null;
	
	List<StrReplaceBean> regexRulesList = null;

	private RulesConfig() {
		stringRulesList = readConfigs("./resources/sql2pg/strreplacerules.xml");
	}

	private List<StrReplaceBean> readConfigs(String configFile) {
		List<StrReplaceBean> rules = new ArrayList<StrReplaceBean>();
		String line = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(this.getClass().getClassLoader().getResource(configFile).getFile());
			br = new BufferedReader(fr);
			StrReplaceBean bean = null;
			while ((line = br.readLine()) != null) {
				bean = new StrReplaceBean();
				String[] parts = line.split(",");
				bean.setName(parts[0]);
				bean.setSource(parts[1]);
				bean.setTarget(parts[2]);
				rules.add(bean);
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Failed to open file # " + configFile);
		} catch (IOException ex) {
			System.out.println("Error while reading file # " + configFile);
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
		return rules;
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
