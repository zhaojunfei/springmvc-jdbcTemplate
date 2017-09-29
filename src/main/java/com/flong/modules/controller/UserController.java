package com.flong.modules.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flong.commons.web.BaseController;
import com.flong.modules.service.UserService;

/**
 * @Author:liangjilong
 * @Date:2015年10月29日-下午3:37:48
 * @Email:jilonglinag@sina.com
 * @Version:1.0
 * @Description:
 */

@Controller
@RequestMapping("user")
public class UserController extends BaseController{

	@Autowired UserService userService;
	
	
	@RequestMapping(value="list")
	public String list(HttpServletRequest request ,HttpServletResponse response){
		List<Map<String,Object>> list = userService.getAll();
		
		request.setAttribute("list", list);
		
		return "modules/test/list";
	}
	
	
	
}
