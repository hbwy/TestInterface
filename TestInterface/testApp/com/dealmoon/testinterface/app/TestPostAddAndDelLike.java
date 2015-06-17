package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
 * @data:2015年6月15日 下午3:23:00
 * @description: 喜欢/取消喜欢 晒单  需要登录
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPostAddAndDelLike {

	private static List<String> addReqJsons;
	private static List<String> delReqJsons;
	private static List<String> listReqJsons;

	private static int userId;
	private static String token;
	private static int postId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		addReqJsons = (List<String>) reqData.get("postaddlike");
		delReqJsons = (List<String>) reqData.get("postdellike");
		listReqJsons = (List<String>) reqData.get("postgetlikelist");

		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");

		//随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// 验证喜欢晒单result code = 0
	@Test
	public void test1AddLike() {

		String reqJson0 = "{" + token + addReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		String addResponse = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//验证添加喜欢成功
	@Test
	public void test2Find() {
		String response = find();
		Assert.assertTrue("Error：favorite the post failed", MyUtils.idInArray(response, "posts", postId));
	}

	@Test
	public void test3DelLike() {
		//获取删除喜欢的指令
		String delJson = "{" + token + delReqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//发送取消喜欢的指令
		String delResponse = MyUtils.sendPost(delJson);

		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证取消喜欢晒单是否成功 喜欢晒单--查看喜欢的晒单的列表--取消喜欢--查看喜欢的晒单的列表
	@Test
	public void test4NotFind() {
		String response = find();
		Assert.assertFalse("Error：The post still exist", MyUtils.idInArray(response, "posts", postId));
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

	private void mustLogin(String reqJson) {
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	private String find(){
		String listJson = "{" + token + listReqJsons.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		return MyUtils.sendPost(listJson);
	}
	@AfterClass
	public static void release() {
		token = null;
		addReqJsons = null;
		delReqJsons = null;
		listReqJsons = null;
	}

}
