package com.flong.codegenerator;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class CodeGenerator {
	public static final String ENTER = "\n";
	public static final String TAB = "    ";
	public static final String NAME = "NAME";
	public static final String TYPE = "TYPE";
	public static final String SIZE = "SIZE";
	public static final String CLASS = "CLASS";
	public static final String NOW_DATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	public static final String DB_NAME = PropertiesHelper.get("jdbc.url").substring(PropertiesHelper.get("jdbc.url").lastIndexOf("/")+1,PropertiesHelper.get("jdbc.url").indexOf("?") == -1? PropertiesHelper.get("jdbc.url").length():PropertiesHelper.get("jdbc.url").indexOf("?"));
	public static final String ROOT_PACKAGE = PropertiesHelper.get("rootPackage");
	public static final String AUTHOR = PropertiesHelper.get("author");
	public static final List<String> IGNORE_TABLE_PREFIX = new ArrayList<String>();

	static {
		String ignoreTablePrefix = PropertiesHelper.get("ignoreTablePrefix");
		if(ignoreTablePrefix.length() > 0) {
			String[] ignoreTablePrefixs = ignoreTablePrefix.split("\\s*\\,\\s*");
			for (String elem : ignoreTablePrefixs) {
				IGNORE_TABLE_PREFIX.add(elem);
			}
		}
	}

	/***
	 * 生成实体类的代码
	 * @param table
	 * @throws Exception
	 */
	public void createEntityClass(String table) throws Exception {
		String tableConstantName = getTableConstantName(table);
		String className = getClassName(tableConstantName);
		StringBuilder sb = new StringBuilder();
		sb.append("package " + ROOT_PACKAGE + ".entity;");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append("/**\n * \n *\n * @created：" + NOW_DATE + "\n * @author " + AUTHOR + "\n */");
		sb.append(ENTER);
		sb.append("@Relation(" + className + ".TABLE)");
		sb.append(ENTER);
		sb.append("public class " + className + " extends Entity {");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append(TAB);
		sb.append("/** 表名常量 */");
		sb.append(ENTER);
		sb.append(TAB);
		sb.append("public static final String TABLE = Table." + tableConstantName + ";");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append(TAB);
		sb.append("/**");
		sb.append(ENTER);
		sb.append(TAB);
		sb.append(" * 列名常量");
		sb.append(ENTER);
		sb.append(TAB);
		sb.append(" */");
		sb.append(ENTER);
		for (Map<String, Object> col : getCols(table)) {
			String colName = col.get(NAME).toString().toUpperCase();
			sb.append(TAB);
			sb.append("public static final String COL_" + colName + " = \"" + colName + "\";");
			sb.append(ENTER);
		}
		sb.append(ENTER);
		sb.append(TAB);
		sb.append("/**");
		sb.append(ENTER);
		sb.append(TAB);
		sb.append(" * 列属性");
		sb.append(ENTER);
		sb.append(TAB);
		sb.append(" */");
		sb.append(ENTER);
		for (Map<String, Object> col : getCols(table)) {
			sb.append(TAB);
			sb.append("/**  */");
			sb.append(ENTER);
			if(col.get(NAME).toString().equalsIgnoreCase("ID")) {
				sb.append(TAB);
				sb.append("@Id");
				sb.append(ENTER);
			}
			sb.append(TAB);
			sb.append("@Column(COL_" + col.get(NAME).toString().toUpperCase() + ")");
			sb.append(ENTER);
			sb.append(TAB);
			sb.append("private ");
			if(col.get(NAME).toString().equalsIgnoreCase("ID") || col.get(NAME).toString().toUpperCase().endsWith("_ID")) {
				sb.append("Long");
			} else if(Class.forName(col.get(CLASS).toString()).isAssignableFrom(Date.class) || 
					Class.forName(col.get(CLASS).toString()) == Timestamp.class) {
				sb.append("Date");
			} else if(getClassName(col.get(NAME).toString()).equals(Class.forName(col.get(CLASS).toString()).getSimpleName())) {
				sb.append(col.get(CLASS));
			} else {
				sb.append(Class.forName(col.get(CLASS).toString()).getSimpleName());
			}
//			sb.append(" " + getFieldName(col.get(NAME).toString()) + ";");
			sb.append(" " + col.get(NAME).toString() + ";");
			sb.append(ENTER);
		}
		sb.append(ENTER);
		for (Map<String, Object> col : getCols(table)){
			sb.append(TAB);
			sb.append("public ");
			if(col.get(NAME).toString().equalsIgnoreCase("ID") || col.get(NAME).toString().toUpperCase().endsWith("_ID")) {
				sb.append("Long");
			} else if(Class.forName(col.get(CLASS).toString()).isAssignableFrom(Date.class) || 
					Class.forName(col.get(CLASS).toString()) == Timestamp.class) {
				sb.append("Date");
			} else if(getClassName(col.get(NAME).toString()).equals(Class.forName(col.get(CLASS).toString()).getSimpleName())) {
				sb.append(col.get(CLASS));
			} else {
				sb.append(Class.forName(col.get(CLASS).toString()).getSimpleName());
			}
			sb.append(" ").append("get").append(col.get(NAME).toString().replaceFirst("\\b(\\w)|\\s(\\w)", col.get(NAME).toString().substring(0,1).toUpperCase()));
			sb.append("() {");
			sb.append(ENTER);
			sb.append(TAB);
			sb.append(TAB);
			sb.append("return ").append(col.get(NAME).toString()).append(";");
			sb.append(ENTER);
			sb.append(TAB);
			sb.append("}");
			sb.append(ENTER);
			sb.append(TAB);
			sb.append("public void ").append("set").append(col.get(NAME).toString().replaceFirst("\\b(\\w)|\\s(\\w)", col.get(NAME).toString().substring(0,1).toUpperCase()));
			sb.append("(");
			if(col.get(NAME).toString().equalsIgnoreCase("ID") || col.get(NAME).toString().toUpperCase().endsWith("_ID")) {
				sb.append("Long");
			} else if(Class.forName(col.get(CLASS).toString()).isAssignableFrom(Date.class) || 
					Class.forName(col.get(CLASS).toString()) == Timestamp.class) {
				sb.append("Date");
			} else if(getClassName(col.get(NAME).toString()).equals(Class.forName(col.get(CLASS).toString()).getSimpleName())) {
				sb.append(col.get(CLASS));
			} else {
				sb.append(Class.forName(col.get(CLASS).toString()).getSimpleName());
			}
			sb.append(" ").append(col.get(NAME).toString());
			sb.append(") {");
			sb.append(ENTER);
			sb.append(TAB);
			sb.append(TAB);
			sb.append("this.").append(col.get(NAME).toString()).append(" = ").append(col.get(NAME).toString()).append(";");
			sb.append(ENTER);
			sb.append(TAB);
			sb.append("}");
			sb.append(ENTER);
		}
		sb.append("}");
		sb.append(ENTER);
		FileUtils.save("output-code/" + ROOT_PACKAGE.replaceAll("\\.", "/") + "/entity/" + className + ".java", sb.toString());
	}
	
	/***
	 * 生成dao层java类的代码
	 * @param table
	 * @throws Exception
	 */
	public void createDaoClass(String table) throws Exception {
		String className = getClassName(getTableConstantName(table));
		StringBuilder sb = new StringBuilder();
		sb.append("package " + ROOT_PACKAGE + ".dao;");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append("/**\n * \n *\n * @created：" + NOW_DATE + "\n * @author " + AUTHOR + "\n */");
		sb.append(ENTER);
		sb.append("public interface " + className + "Dao extends EntityDao<" + className + "> {");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append("}");
		sb.append(ENTER);
		FileUtils.save("output-code/" + ROOT_PACKAGE.replaceAll("\\.", "/") + "/dao/" + className + "Dao.java", sb.toString());
	}
	
	/***
	 * 生成dao的实现类的代码
	 * @param table
	 * @throws Exception
	 */
	public void createDaoImplClass(String table) throws Exception {
		String className = getClassName(getTableConstantName(table));
		StringBuilder sb = new StringBuilder();
		sb.append("package " + ROOT_PACKAGE + ".dao.impl;");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append("/**\n * \n *\n * @created：" + NOW_DATE + "\n * @author " + AUTHOR + "\n */");
		sb.append(ENTER);
		sb.append("@Repository");
		sb.append(ENTER);
		sb.append("public class " + className + "DaoImpl extends EntityDaoSupport<" + 
				className + "> implements " + className + "Dao {");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append("}");
		sb.append(ENTER);
		FileUtils.save("output-code/" + ROOT_PACKAGE.replaceAll("\\.", "/") + "/dao/impl/" + className + "DaoImpl.java", sb.toString());
	}

	/***
	 * 创建表的类
	 * @param tables
	 */
	public void createTableClass(List<String> tables) {
		StringBuilder sb = new StringBuilder();
		sb.append("package " + ROOT_PACKAGE + ".domain;");
		sb.append(ENTER);
		sb.append(ENTER);
		sb.append("/**\n * \n *\n * @created：" + NOW_DATE + "\n * @author " + AUTHOR + "\n */");
		sb.append(ENTER);
		sb.append("public interface Table {");
		sb.append(ENTER);
		for (String table : tables) {
			sb.append(TAB);
			sb.append("String " + getTableConstantName(table) + " = \"" + table.toUpperCase() + "\";");
			sb.append(ENTER);
		}
		sb.append(ENTER);
		sb.append("}");
		sb.append(ENTER);
		FileUtils.save("output-code/" + ROOT_PACKAGE.replaceAll("\\.", "/") + "/domain/Table.java", sb.toString());
	}

	/***
	 * 获取数据库表名
	 * @return
	 * @throws Exception
	 */
	public List<String> getTables() throws Exception {
		List<Object> params = new ArrayList<Object>();
		System.out.println("==========="+DB_NAME);
		//params.add(DB_NAME);
		String dbname=DB_NAME;
		params.add(dbname);
		
		//ResultSet rs = JdbcUtils.query("select table_name from information_schema.tables where table_schema = ? order by table_name", params);
		ResultSet rs = DBHelperUtils.query("select table_name from information_schema.tables where table_schema = ? order by table_name", params);
		List<String> tables = new ArrayList<String>();
		while (rs.next()) {
			tables.add(rs.getString(1));		
		}
		return tables;
	}
	
	/***
	 * 获取表的列
	 * @param table
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> getCols(String table) throws Exception {
		List<Map<String, Object>> cols = new ArrayList<Map<String,Object>>();
		 ResultSetMetaData md = DBHelperUtils.query("select * from " + table + " where 1 = 2", null).getMetaData();
		 for (int i = 1; i <= md.getColumnCount(); i++) {
			 Map<String, Object> col = new HashMap<String, Object>();
			 cols.add(col);
			 col.put(NAME, md.getColumnName(i));
			 col.put(CLASS, md.getColumnClassName(i));
			 col.put(SIZE, md.getColumnDisplaySize(i));
			 String _type = null;
			 String type = md.getColumnTypeName(i);
			 if(type.equals("INT")) {
				 _type = "INTEGER";
			 } else if(type.equals("DATETIME")) {
				 _type = "TIMESTAMP";
			 } else {
				 _type = type;
			 }
			 col.put(TYPE, _type);
		}
		return cols;
	}
	
	/***
	 * 获取表的常量名，一般是在数据库建表的时候，写的注释..
	 * @param table
	 * @return
	 */
	private String getTableConstantName(String table) {
		String tableConstantName = table.toUpperCase();
		for (String item : IGNORE_TABLE_PREFIX) {
			tableConstantName = tableConstantName.replaceAll("^" + item.toUpperCase(), "");
		}
		return tableConstantName;
	}

	/***
	 * 获取类的名
	 * @param name
	 * @return
	 */
	private String getClassName(String name) {
		String[] names = name.split("_");
		StringBuilder sb = new StringBuilder();
		for (String n : names) {
			if(n.length() == 0) {
				sb.append("_");
			} else {
				sb.append(n.substring(0, 1).toUpperCase());
				if(n.length() > 1) {
					sb.append(n.substring(1).toLowerCase());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 获取字段名
	 * @param name
	 * @return
	 */
	private String getFieldName(String name) {
		String _name = getClassName(name);
		return _name.substring(0, 1).toLowerCase() + _name.substring(1);
	}

}
