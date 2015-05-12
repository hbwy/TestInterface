package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月13日 下午2:08:53
 * @description: UserProfile 用户资料 不需要登录
 */
public class TestUserProfile {

	private static List<String> reqJsons;
	private static int userId;
	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userprofile");
		userId = Integer.parseInt((String) MyUtils.getRandomIdToken().get("id"));
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	// 验证userInfo不为null
	@Test
	public void testUserInfoNotNull() {
		String response = this.getReqJson0Response();
		JSONObject userInfo = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("userInfo");
		Assert.assertFalse("Error:The userInfo is null", userInfo.isNullObject());
	}

	// 验证posts数组size >= 0
	@Test
	public void testPostsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();
		Assert.assertTrue("Error:The posts array  is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, userId);
		return MyUtils.sendPost(reqJson0);
	}
}
