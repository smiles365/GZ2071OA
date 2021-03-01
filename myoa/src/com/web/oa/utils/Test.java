package com.web.oa.utils;

import org.apache.shiro.crypto.hash.Md5Hash;

public class Test {

	public static void main(String[] args) {
		Md5Hash hash = new Md5Hash("111111");
		System.out.println(hash);
	}

}
