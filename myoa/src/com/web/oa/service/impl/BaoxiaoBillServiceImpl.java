package com.web.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.oa.mapper.BaoxiaoBillMapper;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.pojo.BaoxiaoBillExample;
import com.web.oa.pojo.BaoxiaoBillExample.Criteria;
import com.web.oa.service.BaoxiaoBillService;
@Service
public class BaoxiaoBillServiceImpl implements BaoxiaoBillService {
	
	@Autowired
	private BaoxiaoBillMapper baoxiaoBillMapper;
	
	@Override
	//1.报销信息写入数据库 leavebill
	public void saveBaoxiaoBill(BaoxiaoBill bill) {
		this.baoxiaoBillMapper.insert(bill);
	}

	@Override
	public List<BaoxiaoBill> lookMyBaoxiaoBillList(String userid) {
		BaoxiaoBillExample example = new BaoxiaoBillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(Long.parseLong(userid));
		
		List<BaoxiaoBill> list = this.baoxiaoBillMapper.selectByExample(example);
		return list;
	}

	@Override
	public void leaveBillAction_delete(String id) {
		this.baoxiaoBillMapper.deleteByPrimaryKey(Long.parseLong(id));
		
	}

	@Override
	public BaoxiaoBill findBaoxiaoBill(Long id) {
		BaoxiaoBill bill = this.baoxiaoBillMapper.selectByPrimaryKey(id);
		return bill;
	}

}
