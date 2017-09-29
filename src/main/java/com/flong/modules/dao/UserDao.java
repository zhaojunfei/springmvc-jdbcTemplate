package com.flong.modules.dao;

import java.util.List;
import java.util.Map;

import com.flong.commons.persistence.dao.EntityDao;
import com.flong.modules.pojo.User;

/**
 * @Author:liangjilong
 * @Date:2015年10月29日-下午3:45:04
 * @Email:jilonglinag@sina.com
 * @Version:1.0
 * @Description:
 */
public interface UserDao extends EntityDao<User> {

	
	public 	List<Map<String, Object>> getAll();
}
