package com.web.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysRoleMapper;
import com.web.oa.mapper.SysUserRoleMapper;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;
import com.web.oa.pojo.SysUserRole;
import com.web.oa.service.SysService;
@Service
public class sysServiceImpl implements SysService {
	@Autowired
	private SysPermissionMapperCustom SysPermissionMapperCustom;
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	
	@Override
	public List<MenuTree> getMenuTree() {
		List<MenuTree> list = this.SysPermissionMapperCustom.getMenuTree();

		return list;
	}

	
	//查询该用户权限 用于授权操作
	@Override
	public List<SysPermission> getPermissionList(String username) throws Exception {
		List<SysPermission> list = this.SysPermissionMapperCustom.findPermissionListByUserId(username);
		return list;
	}
	
	@Override
	public List<SysRole> findRoleList() {
		
		 List<SysRole> list = this.sysRoleMapper.selectByExample(null);
		return list;
	}

	//添加sys_user_role表
	@Override
	public void addUserRole(Employee employee) {
		// 封装SysUserRole
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setSysUserId(employee.getName());
		sysUserRole.setSysRoleId(employee.getRole().toString());
		
		//开始添加
		this.sysUserRoleMapper.insert(sysUserRole);
		
	}

}
