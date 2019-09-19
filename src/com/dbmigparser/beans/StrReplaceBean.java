package com.dbmigparser.beans;

public class StrReplaceBean {

	private String name;

	private String search;
	
	private String source;

	private String target;

	public StrReplaceBean() {

	}

	public StrReplaceBean(String name, String source, String target) {
		this.name = name;
		this.source = source;
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String toString() {
		return name + ", " + source + ", " + target;
	}

}
