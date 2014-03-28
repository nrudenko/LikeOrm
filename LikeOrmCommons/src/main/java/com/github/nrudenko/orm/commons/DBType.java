package com.github.nrudenko.orm.commons;

public enum DBType {
	INT_PRIMARY("INTEGER PRIMARY KEY AUTOINCREMENT"),
	INT("INTEGER DEFAULT 0"),
	INT_DEF("INTEGER DEFAULT -1"),
	REAL("REAL"),
	TEXT("TEXT"), 
	NUMERIC("NUMERIC"), 
	TEXT_NOT_NULL("TEXT NOT NULL"),
    TEXT_DEFAULT_EMPTY("TEXT DEFAULT \"\""),
    BLOB("BLOB"),
    NO_TYPE(""); // just for default annotation value

	private String name;

	DBType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}