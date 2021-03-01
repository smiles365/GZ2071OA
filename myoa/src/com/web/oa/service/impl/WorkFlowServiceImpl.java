package com.web.oa.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;
import com.web.oa.mapper.BaoxiaoBillMapper;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
@Service
public class WorkFlowServiceImpl implements WorkFlowService{
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private FormService formService;
	@Autowired
	private BaoxiaoBillMapper baoxiaoBillMapper;
	
	//部署流程
	@Override
	public void addProcess(InputStream in, String processName) {
		ZipInputStream zipInputStream = new ZipInputStream(in);
		this.repositoryService.createDeployment()
		.name(processName)
		.addZipInputStream(zipInputStream)
		.deploy();
	}
	
	//查看已经发布的流程列表
	@Override
	public List<Deployment> lookProcessList() {
		List<Deployment> list = this.repositoryService.createDeploymentQuery()
		.orderByDeploymenTime()
		.desc()
		.list();
		
		return list;
	}
	
	
	////流程定义信息列表
	@Override
	public List<ProcessDefinition> getProcessDefinition() {
		List<ProcessDefinition> list = 
				this.repositoryService.createProcessDefinitionQuery()
				.orderByProcessDefinitionVersion()
				.asc()
				.list();
				
		return list;
	}

	//删除流程
	@Override
	public void delProcess(String deploymentId) {
		this.repositoryService.deleteDeployment(deploymentId,true);
		
	}
	
	//查看流程定义的规则图片
	@Override
	public InputStream viewImage(String deploymentId, String imageName) {
		InputStream in = this.repositoryService.getResourceAsStream(deploymentId, imageName);
		return in;
	}

	@Override
	public void startProcess(Long BillId,ActiveUser activeUser) {
		String businessKey=Constants.BAOXIAO_KEY+"."+BillId;
		
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("inputUser", activeUser.getUsername());
		map.put("objXX", businessKey);
		// startProcessInstanceByMessage(String messageName, String businessKey, Map<String, Object> processVariables);
		//this.runtimeService.startProcessInstanceByMessage(Constants.BAOXIAO_KEY, businessKey, map);
		this.runtimeService.startProcessInstanceByKey(Constants.BAOXIAO_KEY, businessKey, map);
	}

	@Override
	public List<Task> lookAssigneeTaskList(String username) {
		List<Task> list = this.taskService.createTaskQuery()
		.taskAssignee(username)
		.orderByTaskCreateTime()
		.desc()
		.list();
		return list;
	}
	
	// 办理事务 回显
	@Override
	public BaoxiaoBill viewTaskForm(String taskId) {
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
			.processInstanceId(task.getProcessInstanceId())
			.singleResult();
			
		String business_key=pi.getBusinessKey();
		//从business_key截取到 baoxiaobill的id
		if (StringUtils.isEmptyOrWhitespaceOnly(business_key)) {
			return null;
		}
		String id = business_key.split("\\.")[1];
		System.out.println("business_key"+id);
		BaoxiaoBill bill = this.baoxiaoBillMapper.selectByPrimaryKey(Long.parseLong(id));
		
		return bill;
	}
	
	//查询后续连线的名称
	@Override
	public List<String> getoutcomeList(String taskId) {
		//存放后续连线的名称  用于返回
		List<String> list=new ArrayList<String>();
		
		//根据任务id获取到当前任务对象
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		
		//根据task 获取到流程定义id
		String definitionId = task.getProcessDefinitionId();
		
		//根据流程定义id获取到ProcessDefinitionEntity 来获取当前任务完成之后的连线名称
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(definitionId);
		
		//根据task 获取到流程实例id
		String instanceId = task.getProcessInstanceId();
		
		//根据流程实例id查询正在执行的流程对象表 返回流程对象
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
				.processInstanceId(instanceId)
				.singleResult();
		
		//根据流程对象获取当前活动id
		String activityId = pi.getActivityId();
		
		//根据processDefinitionEntity获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		
		//获取活动完成后的连线
		List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
		
		if (outgoingTransitions!=null && outgoingTransitions.size()>0) {
			for (PvmTransition pvm : outgoingTransitions) {
				String name = (String) pvm.getProperty("name");
				if(!StringUtils.isEmptyOrWhitespaceOnly(name)) {
					list.add(name);
				} else {
					list.add("默认提交");
				}
			}
		}
		
		return list;
	}

	@Override
	public void submitTask(Long id, String taskId, String outcome, String comment) {
		//1.添加批注
		//2.流程推进
		//System.out.println(taskId+":1111111111");
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		//System.out.println(task+"222222222222");
		//获取流程实例id
		String processInstanceId = task.getProcessInstanceId();
		
		//添加当前流程的审核者
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		Authentication.setAuthenticatedUserId(activeUser.getUsername());
		
		//添加批注信息
		this.taskService.addComment(taskId, processInstanceId, comment);
		
		//任务推进
		/**
		 * 2：如果连线的名称是“默认提交”，那么就不需要设置，如果不是，就需要设置流程变量
		 * 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
				 流程变量的名称：outcome
				 流程变量的值：连线的名称
		 */
		Map<String, Object> variables=new HashMap<String, Object>();
		if (outcome!=null && !outcome.equals("默认提交")) {
			variables.put("message", outcome);
			
			//3：使用任务ID，完成当前人的个人任务，同时流程变量
			this.taskService.complete(taskId, variables);
			
		} else {
			this.taskService.complete(taskId);
		}
		
		//查看流程是否结束--结束修改审核状态
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
		.processInstanceId(processInstanceId)
		.singleResult();
		
		//如果流程已经结束
		if (pi==null) {
			//查找到Baoxiaobill--update
			BaoxiaoBill bill = this.baoxiaoBillMapper.selectByPrimaryKey(id);
			bill.setState(2);
			
			this.baoxiaoBillMapper.updateByPrimaryKey(bill);
		}
		
		
		
	}

	@Override
	public List<Comment> findWcommentList(String taskId) {
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		
		String processInstanceId = task.getProcessInstanceId();
		
		List<Comment> comment = this.taskService.getProcessInstanceComments(processInstanceId);
		return comment;
	}

	@Override
	public List<Comment> findHiComment(Long id) {
		//拼接bisiness_key
		String business_key=Constants.BAOXIAO_KEY+"."+id;
		
		HistoricProcessInstance hpi = this.historyService
			.createHistoricProcessInstanceQuery()
			.processInstanceBusinessKey(business_key)
			.singleResult();
		
		List<Comment> list = this.taskService.getProcessInstanceComments(hpi.getId());
		
		return list;
	}

	//获取当前流程对象 产看流程图
	@Override
	public ProcessDefinition selectProcessDefinition(String taskId) {
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		
		String definitionId = task.getProcessDefinitionId();
		
		ProcessDefinition definition = this.repositoryService.createProcessDefinitionQuery()
		.processDefinitionId(definitionId)
		.singleResult();
		
		return definition;
	}

	@Override
	public Map<String, Object> findPosionXYZ(String taskId) {
		//用来存放坐标
		Map<String, Object> map=new HashMap<String, Object>();
		
		// 获取task
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		
		//获取任务定义id
		String definitionId = task.getProcessDefinitionId();
		//得到ProcessDefinitionEntity  获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = 
				(ProcessDefinitionEntity)repositoryService
				.getProcessDefinition(definitionId);
				
		
		//获取任务id
		String instanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
		.processInstanceId(instanceId)
		.singleResult();
		
		//获取当前活动的id
		String id = pi.getActivityId();
		
		//获取当前活动的对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(id);
		
		//得到坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("height", activityImpl.getHeight());
		map.put("width", activityImpl.getWidth());
		
		return map;
	}
	
	

}
