package com.web.oa.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.BaoxiaoBillService;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;

@Controller
public class WorkFlowController {
	
	@Autowired
	private WorkFlowService workFlowService;
	@Autowired
	private BaoxiaoBillService baoxiaoBillService;
	@Autowired
	private TaskService taskService;
	
	@RequestMapping(value = "/deployProcess")
	public String addProcess(MultipartFile fileName,String processName) {
		try {
			this.workFlowService.addProcess(fileName.getInputStream(),processName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "add_process";
		
	}
	
	//查看已经发布的流程列表
	@RequestMapping(value = "/processDefinitionList")
	public String lookProcessList(Model model) {
		//部署信息管理列表 act_re_deployment表
		List<Deployment> deployment=this.workFlowService.lookProcessList();
		
		//流程定义信息列表  act_re_procdef
		List<ProcessDefinition> ProcessDefinition=this.workFlowService.getProcessDefinition();
		
		model.addAttribute("depList", deployment);
		model.addAttribute("pdList", ProcessDefinition);
			
		return "workflow_list";
		
	}
	//删除流程
	@RequestMapping(value = "/delDeployment")
	public String delProcess(String deploymentId) {
		System.out.println(deploymentId);
		this.workFlowService.delProcess(deploymentId);
		
		return "redirect:/processDefinitionList";
		
	}
	
	//查看流程定义的规则图片
	@RequestMapping(value = "/viewImage")
	public void viewImage(String deploymentId,String imageName,HttpServletResponse response) {
		//System.out.println("1.......");
		try(InputStream in= this.workFlowService.viewImage(deploymentId,imageName);
		ServletOutputStream os = response.getOutputStream()){
			//System.out.println("2.......");
			//System.out.println("2......."+in);
			int len=0;
			byte[] b=new byte[1024];
			while ((len=in.read(b))>0) {
					os.write(b, 0, len);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		//System.out.println("3.......");
	}
	
	//查看我的待办事务
	@RequestMapping(value = "/myTaskList")
	public String  myTaskList(Model model) {
		//获取当前认证的对象
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
				
		List<Task> tastList=this.workFlowService.lookAssigneeTaskList(activeUser.getUsername());
		
		model.addAttribute("taskList", tastList);
		
		return "workflow_task";
	}
	
	
	//提交报销申请
	//1.报销信息写入数据库 leavebill
	//2.启动流程
	@RequestMapping(value = "/saveStartBaoxiao")
	public String  saveStartBaoxiao(BaoxiaoBill bill) {
		
		//获取当前认证的对象
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		
		//封装baoxiaoBill
		bill.setUserId(activeUser.getId());
		bill.setCreatdate(new Date());
		//初始审核状态为1
		bill.setState(1);
		
		//1.报销信息写入数据库 leavebill
		this.baoxiaoBillService.saveBaoxiaoBill(bill);
		//插入成功后返回id
		Long BillId = bill.getId();
		
		//2.启动流程
		//activeUser 用于分配待办人
		this.workFlowService.startProcess(BillId,activeUser);
		
		return "redirect:/myTaskList";
	}
	
	// 办理事务 推进
	@RequestMapping(value = "/submitTask")
	// id-BaixiaoBill  outcome-后续连线名称
	public String  submitTask(Long id,String taskId,String outcome,String comment) {
		System.out.println("taskId:"+taskId);
		this.workFlowService.submitTask(id,taskId,outcome,comment);
		return "redirect:/myTaskList";
	}
	
	
	// 办理事务 回显
	@RequestMapping(value = "/viewTaskForm")
	public String  viewTaskForm(String taskId,Model model) {
		BaoxiaoBill bill= this.workFlowService.viewTaskForm(taskId);
		List<String> list=this.workFlowService.getoutcomeList(taskId);
		
		
		//回显批注信息 commentList
		List<Comment> commentList=this.workFlowService.findWcommentList(taskId);
		
		model.addAttribute("commentList", commentList);
		model.addAttribute("outcomeList", list);
		model.addAttribute("baoxiaoBill", bill);
		model.addAttribute("taskId", taskId);
		return "approve_baoxiao";
	}
	
	// 显示历史批注信息
	@RequestMapping(value = "/viewHisComment")
	public String  viewHisComment(Long id,Model model) {
		
		//查新BaoXiaoBill
		BaoxiaoBill bill= this.baoxiaoBillService.findBaoxiaoBill(id);
		
		//查询批注信息
		List<Comment> hiCommentList= this.workFlowService.findHiComment(id);
		
		model.addAttribute("baoxiaoBill", bill);
		model.addAttribute("commentList", hiCommentList);
		
		return "workflow_commentlist";
	}
	
	// 查看流程图
	@RequestMapping(value = "/viewCurrentImage")
	public String  viewCurrentImage(String taskId,Model model) {
		
		//1.根据任务id查询出流程对象
		ProcessDefinition pd=this.workFlowService.selectProcessDefinition(taskId);
		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		
		//2.查找坐标
		Map<String, Object> map=this.workFlowService.findPosionXYZ(taskId);
		map.forEach((k,v)->System.out.println(k+"<-->"+v));
		model.addAttribute("acs",map);
		
		return "viewimage";
	}
	// 查看流程图 ByBill
	@RequestMapping(value = "/viewCurrentImageByBill")
	public String  viewCurrentImageByBill(Long billId,Model model) {
		String business_key=Constants.BAOXIAO_KEY+"."+billId;
		Task task = this.taskService.createTaskQuery()
				.processInstanceBusinessKey(business_key)
				.singleResult();
		//1.根据任务id查询出流程对象
		ProcessDefinition pd=this.workFlowService.selectProcessDefinition(task.getId());
		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		
		//2.查找坐标
		Map<String, Object> map=this.workFlowService.findPosionXYZ(task.getId());
		map.forEach((k,v)->System.out.println(k+"<-->"+v));
		model.addAttribute("acs",map);
		
		return "viewimage";
	}
}
