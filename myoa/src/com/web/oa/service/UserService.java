package com.web.oa.service;

import java.util.List;

import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeCustom;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

public interface UserService {

	Employee getUser(String username);

	List<EmployeeCustom> findUserList();

	Employee findManager(Long managerId);

	SysRole viewPermissionByUser(String user_name);

	void addEmployee(Employee employee);

	void updateRoles(String roleId, String userId);

	List<SysRole> findRoles();

	List<SysPermission> findPermissionByRoleId(String roleId);

	List<MenuTree> findAllRolesAndPremission();

	void updateRoleAndPermission(String roleId, int[] permissionIds);

	void deleteRole(String roleId);

	List<MenuTree> loadPermissions();

	List<SysPermission> findMenu();

	void saveRoleAndPermissions(SysRole roleName, int[] permissionIds);

	void addPermission(SysPermission sysPermission);

	

}
