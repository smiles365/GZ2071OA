package com.web.oa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeCustom;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;
import com.web.oa.service.SysService;
import com.web.oa.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userSertvice;
	@Autowired
	private SysService sysService;
@RequestMapping(value = "/login")
public String login(HttpServletRequest request,Model model){
		
		String exceptionName = (String) request.getAttribute("shiroLoginFailure");
		System.out.println(exceptionName);
		if (exceptionName != null) {
			if (UnknownAccountException.class.getName().equals(exceptionName)) {
				model.addAttribute("errorMsg", "用户账号不存在");
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionName)) {
				model.addAttribute("errorMsg", "密码不正确");
			} else if(AuthenticationException.class.getName().equals(exceptionName)) {
				model.addAttribute("errorMsg", "用户账号不存在或已过期");
			} else if("randomcodeError".equals(exceptionName)) {
				model.addAttribute("errorMsg", "验证码不正确");
			}
			else {
				model.addAttribute("errorMsg", "未知错误");
			}
		}
		return "login";
	}
	
	
	//首页
	@RequestMapping(value = "/main")
	public String index(Model model) {
		ActiveUser active = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		model.addAttribute("activeUser", active);
		
		return "index";
		
	}
	//用户列表
	@RequestMapping(value = "/findUserList")
	public String findUserList(Model model) {
	List<EmployeeCustom> emploueeList=this.userSertvice.findUserList();
	List<SysRole> allRoles=this.sysService.findRoleList();
		
		model.addAttribute("userList", emploueeList);
		model.addAttribute("allRoles", allRoles);
		
		return "userlist";
		
	}
	//用户列表
	@RequestMapping(value = "/viewPermissionByUser")
	@ResponseBody
	public SysRole viewPermissionByUser(String userName) {
		SysRole sysRole =this.userSertvice.viewPermissionByUser(userName);
		
		
		return sysRole;
		
	}
	//添加用户
	@RequestMapping(value = "/saveUser")
	public String saveUser(Employee employee) {
		
		//添加用户
		this.userSertvice.addEmployee(employee);
		
		//添加成功后返回id
		//Long id = employee.getId();
		//System.out.println("id:"+id);
		
		//添加sys_user_role表
		this.sysService.addUserRole(employee);
		
		return "redirect:/findUserList";
		
	}
	//findNextManager
	@RequestMapping(value = "/assignRole")
	@ResponseBody
	public Map<String, Object> findNextManager(String roleId,String userId) {
		Map<String, Object> map=new HashMap<String, Object>();
		System.out.println("已经进入");
		System.out.println(roleId);
		System.out.println(userId);
		//System.out.println(emp.getName());
		//System.out.println(emp.getEmail());
		//修改用户角色
		try{
			this.userSertvice.updateRoles(roleId,userId);
			map.put("msg", "分配权限成功！");
		}catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "分配权限失败！");
		}
		
		return map;
		
	}
	
	//toAddRole
	@RequestMapping(value = "/toAddRole")
	public String toAddRole(Model model) {
		List<MenuTree> allPermissions=this.userSertvice.loadPermissions();
		List<SysPermission> menu=this.userSertvice.findMenu();
		
		
		model.addAttribute("menuTypes", menu);
		model.addAttribute("allPermissions", allPermissions);
		return "rolelist";
		
	}
	//toAddRole
	@RequestMapping(value = "/findRoles")
	public String findRoles(Model model) {
		List<SysRole> role= this.userSertvice.findRoles();
		List<MenuTree> menuTreeList=this.userSertvice.findAllRolesAndPremission();
		
		model.addAttribute("allRoles", role);
		model.addAttribute("allMenuAndPermissions", menuTreeList);
		return "permissionlist";
		
	}
	//loadMyPermissions
	@RequestMapping(value = "/loadMyPermissions")
	@ResponseBody
	public List<SysPermission> loadMyPermissions(String roleId,Model model) {
		List<SysPermission> sysPermission=this.userSertvice.findPermissionByRoleId(roleId);
		
		return sysPermission;
		
	}
	//重新分配权限
	@RequestMapping(value = "/updateRoleAndPermission")
	public String updateRoleAndPermission(String roleId,int[] permissionIds,Model model) {
		this.userSertvice.updateRoleAndPermission(roleId,permissionIds);
		return "redirect:/findRoles";
		
	}
	//删除角色
	@RequestMapping(value = "/deleteRole")
	public String deleteRole(String roleId,Model model) {
		//System.out.println(roleId);
		this.userSertvice.deleteRole(roleId);
		
		return "redirect:/findRoles";
		
	}
	//添加角色 分配权限
	@RequestMapping(value = "/saveRoleAndPermissions")
	public String saveRoleAndPermissions(SysRole roleName,int[] permissionIds) {
		//System.out.println(roleId);
		this.userSertvice.saveRoleAndPermissions(roleName,permissionIds);
		
		return "redirect:/toAddRole";
		
	}
	//新建权限
	@RequestMapping(value = "/saveSubmitPermission")
	public String saveSubmitPermission(SysPermission sysPermission) {
		if (sysPermission.getAvailable()==null) {
			sysPermission.setAvailable("0");
		}
		
		this.userSertvice.addPermission(sysPermission);
		return "redirect:/toAddRole";
		
	}
}
