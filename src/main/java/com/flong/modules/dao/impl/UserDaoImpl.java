package com.flong.modules.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.flong.commons.persistence.dao.impl.EntityDaoSupport;
import com.flong.modules.dao.UserDao;
import com.flong.modules.pojo.User;

/**
 * @Author:liangjilong
 * @Date:2015年10月29日-下午3:45:04
 * @Email:jilonglinag@sina.com
 * @Version:1.0
 * @Description:
 */
@Repository
public class UserDaoImpl extends EntityDaoSupport<User> implements UserDao {

	public List<Map<String, Object>> getAll() {
		List<Map<String, Object>> search=null;
		 try {
			String sql = simpleSqlBuilder.getQueryAllSql();
			
			  search = search(sql, new Object[]{});
			//list = search(sql, UserDao.class, new Object[]{});
			//search(sql, mapRowMapper, params)
			return search;
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return search;
	}
}
