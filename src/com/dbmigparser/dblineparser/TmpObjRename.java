package com.dbmigparser.dblineparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.dbmigparser.parser.RuleBase;

public class TmpObjRename extends RuleBase {

	private Map<String, String> tmpObjNames = null;
	
	public TmpObjRename() {
		super();
		tmpObjNames = new HashMap<String, String>();
	}

	@Override
	public String applyRule(int linePos, String currLine) {
		String newLine = currLine.trim();
		if (newLine.startsWith("CREATE TEMPORARY ")) {
			String[] tokens = newLine.split(" ");
			String newName = tokens[3];
			if (newName.endsWith("(")) {
				newName = newName.substring(0, newName.length() - 1);
				tokens[3] = newName;
			}

			if (newName.startsWith("\"")) {
				newName = newName.substring(1, newName.length() - 1);
			}
			if (newName.startsWith("#")) {
				newName = newName.substring(1);
			}
			if (newName.contains("@")) {
				newName = newName.substring(0, newName.indexOf("@"));
			}
			if (newName.contains("$")) {
				newName = newName.substring(0, newName.indexOf("$"));
			}
			newName = newName.toLowerCase();
			if (newName.contains("tmp") == false && newName.contains("temp") == false) {
				newName = "temp_" + newName;
			}
			if (tokens[2].equalsIgnoreCase("TABLE") && newName.contains("tbl") == false
					&& newName.contains("table") == false) {
				newName = newName + "_table";
			}
			if (tokens[2].equalsIgnoreCase("SEQUENCE") && newName.contains("seq") == false) {
				newName = newName + "_seq";
			}
			if (newName.equalsIgnoreCase(tokens[3]) == false) {
				tmpObjNames.put(tokens[3], newName);
				tmpObjNames.put("\"" + tokens[3] + "\"", newName);
			}

			currLine = currLine.replace(tokens[3], newName);
			changes.logChange("Temp obj name '" + tokens[3] + "' changed to '" + newName + "' at line # " + linePos);
			
		} else {
			
			if (tmpObjNames.size() > 0) {
				Set<String> keys = tmpObjNames.keySet();
				Iterator<String> itr = keys.iterator();
				String k = null;
				while (itr.hasNext()) {
					k = itr.next();
					if (currLine.contains(" " + k)) {
						currLine = currLine.replace(" " + k, " " + tmpObjNames.get(k));
						changes.logChange("Temp obj name '" + k + "' changed to '" + tmpObjNames.get(k) + "' at line # "
								+ linePos);
					} else if (currLine.contains("'" + k)) {
						currLine = currLine.replace(k, tmpObjNames.get(k));
						changes.logChange("Temp obj name '" + k + "' changed to '" + tmpObjNames.get(k) + "' at line # "
								+ linePos);
					}
				}
			}
		}
		return currLine;
	}

}
