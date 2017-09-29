package com.flong.modules.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flong.commons.persistence.dao.impl.BaseDaoSupport;
import com.flong.modules.dao.UserDao;
import com.flong.modules.service.UserService;

/**
 * @Author:liangjilong
 * @Date:2015年10月29日-下午3:45:04
 * @Email:jilonglinag@sina.com
 * @Version:1.0
 * @Description:
 */
@Service
@SuppressWarnings("all")
public class UserServiceImpl  extends BaseDaoSupport  implements UserService  {

	@Autowired UserDao userDao;
	
	public List<Map<String, Object>> getAll() {
		return userDao.getAll();
	}

}
