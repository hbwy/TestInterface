package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年6月15日 下午3:25:08
 * @description: 关注/取消关注用户  需要登录
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUserFollowAndUnfollow {

	private static List<String> delReqJsons;
	private static List<String> addReqJsons;
	private static List<String> listReqJsons;

	private static String token; //执行关注用户操作的用户token
	private static int id; //执行关注用户操作的用户id
	private static int userId; //被关注用户的id

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		delReqJsons = (List<String>) reqData.get("userunfollow");
		addReqJsons = (List<String>) reqData.get("userfollow");
		listReqJsons = (List<String>) reqData.get("userfollowlist");

		Map id_token = MyUtils.getRandomIdToken();
		id = Integer.parseInt((String) id_token.get("id"));
		token = (String) id_token.get("token");
		//拿到被关注用户的id
		userId = MyUtils.getRandomUserId(id);
	}

	// 验证result code = 0
	@Test
	public void test1UserFollow() {
		String addJson = "{" + token + addReqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, userId);
		String addResponse = MyUtils.sendPost(addJson);
		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证关注用户
	@Test
	public void test2Find() {
		String resposne = find();
		Assert.assertTrue("Error：Follow the user failed", MyUtils.idInArray(resposne, "users", userId));
	}

	//取消关注用户
	@Test
	public void test3UserUnfollow() {
		String reqJson0 = "{" + token + delReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, userId);
		String delResponse = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证取消关注用户 
	@Test
	public void test4NotFind() {
		String resposne = find();
		Assert.assertFalse("Error：Unfollow the user failed", MyUtils.idInArray(resposne, "users", userId));
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test5AddMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test6DelMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	private String find() {
		String listJson = "{" + token + listReqJsons.get(0) + "}";
		listJson = MyUtils.replaceIdinBackCommand(listJson, id);
		// 发送查询请求
		return MyUtils.sendPost(listJson);
	}

	private void mustLogin(String reqJson) {
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		token = null;
		delReqJsons = null;
		addReqJsons = null;
		listReqJsons = null;
	}

}
