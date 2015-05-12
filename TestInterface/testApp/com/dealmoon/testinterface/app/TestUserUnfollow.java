package com.dealmoon.testinterface.app;

import static org.junit.Assert.*;

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
 * @data:2015年4月13日 下午2:46:17
 * @description: UserUnfollow 取消关注用户 需要登录
 */
public class TestUserUnfollow {

	private static List<String> reqJsons;
	private static String token; //执行关注用户操作的用户token
	private static int id; //执行关注用户操作的用户id
	private static int userId; //被关注用户的id
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userunfollow");
		//拿到执行关注操作的用户id
		Map id_token = MyUtils.getRandomIdToken();
		id = Integer.parseInt((String) id_token.get("id"));
		token = (String) id_token.get("token");
		//拿到被关注用户的id
		userId = MyUtils.getRandomUserId(id);
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证取消关注用户 先关注用户,查询关注的用户的列表,取消关注用户,查询关注的用户的列表
	@Test
	public void testNotFind() {
		String response = getReqJson0Response();
		// 获取查询本人关注的用户列表的指令
		List<String> reqJson2 = (List<String>) reqData.get("userfollowlist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceIdinBackCommand(listJson, id);
		// 发送查询请求
		String response2 = MyUtils.sendPost(listJson);
		// 在关注列表中查询关注的用户
		Assert.assertFalse("Error：Follow the user failed", MyUtils.idInArray(response2, "users", userId));
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);

		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJson0Response() {
		// 获取关注用户指令
		List<String> reqJson1 = (List<String>) reqData.get("userfollow");
		String addJson = "{" + token + reqJson1.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, userId);
		// 发送关注用户的请求
		MyUtils.sendPost(addJson);
		//获取取消关注用户指令
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, userId);
		return MyUtils.sendPost(reqJson0);
	}
}
