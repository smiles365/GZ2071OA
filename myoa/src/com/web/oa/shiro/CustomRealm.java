package com.web.oa.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.service.SysService;
import com.web.oa.service.UserService;

public class CustomRealm extends AuthorizingRealm {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SysService sysService;
	
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("开始认证....");
		String username = (String) token.getPrincipal();
		
		//从数据库取值
		Employee employee=this.userService.getUser(username);
		if (employee==null) {
			return null;
		}
		
		
		List<MenuTree> menu= this.sysService.getMenuTree();
		//组装AcyivitiUser
		ActiveUser user = new ActiveUser();
		user.setId(employee.getId());
		user.setManagerId(employee.getManagerId());
		user.setUsercode(employee.getName());
		user.setUserid(employee.getId().toString());
		user.setUsername(employee.getName());
		user.setEmail(employee.getEmail());
		
		user.setMenuTree(menu);
		String password=employee.getPassword();
		String salt=employee.getSalt();
		System.out.println(employee.getName()+password+salt);
		SimpleAuthenticationInfo simpleAuthenticationInfo = 
				//new SimpleAuthenticationInfo(employee,password,"xxrealmName");
				new SimpleAuthenticationInfo(user, password, ByteSource.Util.bytes(salt), "realmName");
		return simpleAuthenticationInfo;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
		ActiveUser active = (ActiveUser) principal.getPrimaryPrincipal();
		
		List<SysPermission> list = null;
		List<String> permissionLists = new ArrayList<String>();
		
		try {
			list = this.sysService.getPermissionList(active.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (SysPermission sysPermission : list) {
			permissionLists.add(sysPermission.getPercode());
		}
		
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermissions(permissionLists);
		return info;
	}

	
	
	

}
