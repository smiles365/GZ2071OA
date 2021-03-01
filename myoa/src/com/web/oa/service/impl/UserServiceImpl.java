package com.web.oa.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.oa.mapper.EmployeeMapper;
import com.web.oa.mapper.SysPermissionMapper;
import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysRoleMapper;
import com.web.oa.mapper.SysRolePermissionMapper;
import com.web.oa.mapper.SysUserRoleMapper;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeCustom;
import com.web.oa.pojo.EmployeeExample;
import com.web.oa.pojo.EmployeeExample.Criteria;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysPermissionExample;
import com.web.oa.pojo.SysRole;
import com.web.oa.pojo.SysRolePermission;
import com.web.oa.pojo.SysRolePermissionExample;
import com.web.oa.pojo.SysUserRole;
import com.web.oa.pojo.SysUserRoleExample;
import com.web.oa.service.UserService;
@Service(value = "employeeService")
public class UserServiceImpl implements UserService{
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private SysPermissionMapperCustom sysPermissionMapperCustom;
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private SysRolePermissionMapper sysRolePermissionMapper;
	@Autowired
	private SysPermissionMapper sysPermissionMapper;
	
	
	@Override
	public Employee getUser(String username) {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(username);
		
		List<Employee> list = this.employeeMapper.selectByExample(example);
		return list.get(0);
	}
	@Override
	public List<EmployeeCustom> findUserList() {
		List<EmployeeCustom> list = this.sysPermissionMapperCustom.findUserAndRoleList();
		return list;
	}
	
	//查询上级
	@Override
	public Employee findManager(Long managerId) {
		Employee manager = this.employeeMapper.selectByPrimaryKey(managerId);
		return manager;
	}
	//查询该用户所有权限和角色
	@Override
	public SysRole viewPermissionByUser(String user_name) {
		SysRole sysRole = this.sysPermissionMapperCustom.findRoleAndPermissionListByUserId(user_name);
		
		return sysRole;
	}
	
	//添加用户
	@Override
	public void addEmployee(Employee employee) {
		
		//添加 salt
		employee.setSalt("eteokues");
		
		//md5加密
		Md5Hash hash = new Md5Hash(employee.getPassword(), employee.getSalt(), 2);
		
		employee.setPassword(hash.toString());
		
		//开始添加
		this.employeeMapper.insert(employee);
		
	}
	@Override
	public void updateRoles(String roleId, String userId) {
		//1.找到该用户
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(userId);
		
		Employee employee = this.employeeMapper.selectByExample(example).get(0);
		//2.修改role id
		employee.setRole(Integer.valueOf(roleId));
		this.employeeMapper.updateByPrimaryKey(employee);
		
		//3.找到用户角色表
		SysUserRoleExample sr=new SysUserRoleExample();
		sr.createCriteria().andSysUserIdEqualTo(userId);
		
		SysUserRole sysUserRole = this.sysUserRoleMapper.selectByExample(sr).get(0);
		sysUserRole.setSysRoleId(roleId);
		//System.out.println(sysUserRole.getSysRoleId());
		//4.修改用户角色表
		this.sysUserRoleMapper.updateByPrimaryKey(sysUserRole);
		
		//System.out.println("i:"+i);
		
	}
	@Override
	public List<SysRole> findRoles() {
		List<SysRole> list = this.sysRoleMapper.selectByExample(null);
		return list;
	}
	@Override
	public List<SysPermission> findPermissionByRoleId(String roleId) {
		List<SysPermission> list = this.sysPermissionMapperCustom.findPermissionsByRoleId(roleId);
		return list;
	}
	@Override
	public List<MenuTree> findAllRolesAndPremission() {
		List<MenuTree> list = this.sysPermissionMapperCustom.getAllMenuAndPermision();
		return list;
	}
	@Override
	public void updateRoleAndPermission(String roleId, int[] permissionIds) {
		// 1.删除原有的角色权限关系
		SysRolePermissionExample example = new SysRolePermissionExample();
		example.createCriteria().andSysRoleIdEqualTo(roleId);
		
		this.sysRolePermissionMapper.deleteByExample(example);
		
		//2.重新分配角色权限关系
		for (Integer pid : permissionIds) {
			UUID uuid = UUID.randomUUID();
			SysRolePermission permission = new SysRolePermission();
			
			permission.setId(uuid.toString());
			permission.setSysRoleId(roleId);
			permission.setSysPermissionId(pid.toString());
			
			//重新添加
			this.sysRolePermissionMapper.insert(permission);
		}
		
	}
	@Override
	public void deleteRole(String roleId) {
		// 删除角色
		this.sysRoleMapper.deleteByPrimaryKey(roleId);
		
	}
	@Override
	public List<MenuTree> loadPermissions() {
		List<MenuTree> list = this.sysPermissionMapperCustom.getAllMenuAndPermision();
		return list;
	}
	@Override
	public List<SysPermission> findMenu() {
		
		SysPermissionExample example=new SysPermissionExample();
		example.createCriteria().andTypeEqualTo("menu");
		List<SysPermission> list = this.sysPermissionMapper.selectByExample(example);
		return list;
	}
	@Override
	public void saveRoleAndPermissions(SysRole role, int[] permissionIds) {
		role.setId(UUID.randomUUID().toString());
		//默认可用
		role.setAvailable("1");
		//添加角色表
		this.sysRoleMapper.insert(role);
		
		//添加角色权限表
		for (Integer pid : permissionIds) {
			SysRolePermission permission = new SysRolePermission();
			permission.setId(UUID.randomUUID().toString());
			permission.setSysRoleId(role.getId());
			permission.setSysPermissionId(pid.toString());
			
			this.sysRolePermissionMapper.insert(permission);
		}
		
	}
	@Override
	public void addPermission(SysPermission sysPermission) {
		this.sysPermissionMapper.insert(sysPermission);
		
	}
	

}
