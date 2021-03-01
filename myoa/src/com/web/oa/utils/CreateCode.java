package com.web.oa.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;

@Controller
public class CreateCode {
	@RequestMapping(value = "/ramdomCode")
	public void createRandomCode(HttpServletResponse response,HttpSession session) throws IOException {
		//定义图形验证码的长、宽、验证码字符数、干扰元素个数
		ICaptcha captcha = CaptchaUtil.createCircleCaptcha(100, 50, 4, 20);
		//CircleCaptcha captcha = new CircleCaptcha(200, 100, 4, 20);
		
		session.setAttribute("vrifyCode", captcha.getCode());
		//System.out.println(captcha.getCode());
		captcha.write(response.getOutputStream());
	}
}
