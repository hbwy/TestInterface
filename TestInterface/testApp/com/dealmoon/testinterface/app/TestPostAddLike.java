package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月7日 上午9:42:55
 * @description: PostAddLike 添加喜欢,需要登录
 */
public class TestPostAddLike {

	private static List<String> reqJsons;
	private static int userId;
	private static String token;
	private static int postId;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddlike");
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");

		//随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	//验证添加喜欢成功
	@Test
	public void testFind() {
		getReqJson0Response();
		List<String> reqJson2 = (List<String>) reqData.get("postgetlikelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		String response = MyUtils.sendPost(listJson);
		Assert.assertTrue("Error：favorite the post failed", MyUtils.idInArray(response, "posts", postId));
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}
}
