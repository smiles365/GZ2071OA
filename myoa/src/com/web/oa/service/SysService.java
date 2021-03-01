package com.web.oa.service;

import java.util.List;

import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

public interface SysService {

	List<MenuTree> getMenuTree();

	List<SysPermission> getPermissionList(String username) throws Exception;
	
	List<SysRole> findRoleList();

	void addUserRole(Employee employee);
}
