package com.flong.codegenerator;

import java.util.List;

/**
 * 运行生成的main
 * @author liangjilong
 *
 */
public class DoMain {

	public static void main(String[] args) {
		try {
			CodeGenerator entityCreater = new CodeGenerator();
			List<String> tables = entityCreater.getTables();
			for (String table : tables) {
				entityCreater.createEntityClass(table);
				entityCreater.createDaoClass(table);
				entityCreater.createDaoImplClass(table);
			}
			entityCreater.createTableClass(tables);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
