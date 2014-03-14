package com.github.nrudenko.orm;

public enum DBType {
    INT_PRIMARY("INTEGER PRIMARY KEY AUTOINCREMENT"),
    INT("INTEGER DEFAULT 0"),
    INT_DEF_NEGATIVE("INTEGER DEFAULT -1"),
    FLOAT("FLOAT DEFAULT 0"),
    REAL("REAL DEFAULT 0"),
    TEXT("TEXT"),
    NUMERIC("NUMERIC"),
    TEXT_NOT_NULL("TEXT NOT NULL"),
    TEXT_DEFAULT_EMPTY("TEXT DEFAULT \"\""),
    BLOB("BLOB");

    private String name;

    DBType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}