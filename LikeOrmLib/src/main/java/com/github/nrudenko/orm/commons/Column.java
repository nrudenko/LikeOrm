package com.github.nrudenko.orm.commons;

public class Column {
    String name;
    DbType type;
    String customAdditional;

    public Column(String name) {
        this.name = name;
        this.type = DbType.NO_TYPE;
    }

    public Column(String name, DbType type) {
        this.name = name;
        this.type = type;
    }

    public Column(String name, DbType type, String customAdditional) {
        this.name = name;
        this.type = type;
        this.customAdditional = customAdditional;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DbType getType() {
        return type;
    }

    public void setType(DbType type) {
        this.type = type;
    }

    public String getCustomAdditional() {
        return customAdditional;
    }

    public void setCustomAdditional(String customAdditional) {
        this.customAdditional = customAdditional;
    }

    public String getColumnsSql() {

        StringBuilder columnsSql = new StringBuilder(getName());
        columnsSql
            .append(" ")
            .append(type.getSqlRep());

        if (customAdditional != null) {
            columnsSql
                .append(" ")
                .append(customAdditional);
        }
        return columnsSql.toString();
    }

    public Column as(Column column) {
        return new Column(getName() + " AS " + column.getName());
    }

    public boolean isCorrect() {
        return name != null && name.length() > 0 && type != null && type != DbType.NO_TYPE;
    }

    @Override
    public String toString() {
        return "Column{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", customAdditional='" + customAdditional + '\'' +
            '}';
    }
}
