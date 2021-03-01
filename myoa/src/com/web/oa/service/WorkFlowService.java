package com.web.oa.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;

public interface WorkFlowService {

	void addProcess(InputStream inputStream, String processName);

	List<Deployment> lookProcessList();

	List<ProcessDefinition> getProcessDefinition();

	void delProcess(String deploymentId);

	InputStream viewImage(String deploymentId, String imageName);

	void startProcess(Long BillId,ActiveUser activeUser);

	List<Task> lookAssigneeTaskList(String username);

	BaoxiaoBill viewTaskForm(String taskId);

	List<String> getoutcomeList(String taskId);

	void submitTask(Long id, String taskId, String outcome, String comment);

	List<Comment> findWcommentList(String taskId);

	List<Comment> findHiComment(Long id);

	ProcessDefinition selectProcessDefinition(String taskId);

	Map<String, Object> findPosionXYZ(String taskId);

}
