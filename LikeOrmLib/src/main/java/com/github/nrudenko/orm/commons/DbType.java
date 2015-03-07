package com.github.nrudenko.orm.commons;

public enum DbType {
	INT("INTEGER DEFAULT 0"),
	INT_DEF("INTEGER DEFAULT -1"),
	REAL("REAL"),
	TEXT("TEXT"), 
	NUMERIC("NUMERIC"), 
	TEXT_NOT_NULL("TEXT NOT NULL"),
    TEXT_DEFAULT_EMPTY("TEXT DEFAULT \"\""),
    BLOB("BLOB"),
    SERIALIZED("BLOB"),
    NO_TYPE(""); // just for default annotation value

	private String sqlRepresentation;

	DbType(String value) {
		this.sqlRepresentation = value;
	}

	public String getSqlRep() {
		return sqlRepresentation;
	}
}