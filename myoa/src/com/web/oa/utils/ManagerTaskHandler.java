package com.web.oa.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.UserService;

public class ManagerTaskHandler implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		//获取Spring容器
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		
		//获取当前对象
		//1.如果要获取到Session 则要先获取到 request 来request.getSession ...
		//HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		//HttpSession session = request.getSession();
		//session.getAttribute(arg0);
		ActiveUser emp = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		
		
		//获取到Service  @Service("employeeService")
		UserService employeeService = (UserService) context.getBean("employeeService");
		
		//查询出上级
		Employee manager=employeeService.findManager(emp.getManagerId());
		
		//设置代办人
		delegateTask.setAssignee(manager.getName());
		
	}

}
