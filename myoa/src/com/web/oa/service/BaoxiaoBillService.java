package com.web.oa.service;

import java.util.List;

import com.web.oa.pojo.BaoxiaoBill;

public interface BaoxiaoBillService {

	void saveBaoxiaoBill(BaoxiaoBill bill);

	List<BaoxiaoBill> lookMyBaoxiaoBillList(String username);

	void leaveBillAction_delete(String id);

	BaoxiaoBill findBaoxiaoBill(Long id);

}
