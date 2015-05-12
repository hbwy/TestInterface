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
 * @data:2015年4月7日 上午9:45:49
 * @description: PostDelLike 取消喜欢,需要登录
 */
public class TestPostDelLike {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static int postId;
	private static Map<String, Object> reqData;
	

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdellike");
		Map user_token = MyUtils.getRandomIdToken();
		userId= Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
		//随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size() - 1));
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证取消喜欢晒单是否成功 喜欢晒单--查看喜欢的晒单的列表--取消喜欢--查看喜欢的晒单的列表
	@Test
	public void testNotFind() {
		getReqJson0Response();
		// 获取查询本人喜欢的晒单的指令
		List<String> reqJson2 = (List<String>) reqData.get("postgetlikelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		// 发送查询请求
		String response3 = MyUtils.sendPost(listJson);
		// 在喜欢的post列表中查询该喜欢的列表
		Assert.assertFalse("Error：The post still exist", MyUtils.idInArray(response3, "posts", postId));
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
		//随机取得一个post并添加喜欢
		List<String> _reqJsons = (List<String>) reqData.get("postaddlike");
		String addJson = "{" + token + _reqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, postId);
		//发送喜欢post的请求
		MyUtils.sendPost(addJson);
		//获取删除喜欢的指令
		String delJson = "{" + token + reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//发送取消喜欢的指令
		return MyUtils.sendPost(delJson);
	}
}
