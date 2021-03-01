package com.web.oa.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.BaoxiaoBillService;

@Controller
public class BaoXiaoBillController {
	@Autowired
	private BaoxiaoBillService baoxiaoBillService;
	@RequestMapping(value = "/myBaoxiaoBill")
	public String lookMyBaoxiaoBillList(String pageNum,Model model) {
		
		pageNum=pageNum==null?"1":pageNum;//当前页  首次进入为第一页
		//获取到当前用户
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		
		int pagesize=5;//每页显示数量
		PageHelper.startPage(Integer.parseInt(pageNum), pagesize);
		List<BaoxiaoBill> list=this.baoxiaoBillService.lookMyBaoxiaoBillList(activeUser.getUserid());
		PageInfo<BaoxiaoBill> info=new PageInfo<BaoxiaoBill>(list);
		/*
		info.getPageNum();
		info.getTotal();
		info.getPages();
		*/
		model.addAttribute("baoxiaoList", list);
		model.addAttribute("pageModel", info);
		return "baoxiaobill";
	}
	@RequestMapping(value = "/leaveBillAction_delete")
	public String leaveBillAction_delete(String id) {
		this.baoxiaoBillService.leaveBillAction_delete(id);
		
		return "redirect:/myBaoxiaoBill";
	}
}
