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
 * @data:2015年4月7日 上午9:37:59
 * @description:PostDelFavorite 取消关注晒单,需要登录
 */
public class TestPostDelFavorite {

	private static List<String> reqJsons;
	private static String token;
	private static int postId;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdelfavorite");
		token = MyUtils.getRandomToken();
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

	// / 验证取消收藏晒单是否成功 收藏晒单--查看收藏的晒单的列表--取消收藏--查看收藏的晒单的列表
	@Test
	public void testNotFind() {
		getReqJson0Response();
		// 获取查询本人收藏的晒单的指令
		List<String> reqJson2 = (List<String>) reqData.get("postgetfavoritelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		// 发送查询请求
		String response = MyUtils.sendPost(listJson);
		// 在收藏的post列表中查询该收藏的列表
		Assert.assertFalse("Error：The user still exist", MyUtils.idInArray(response, "posts", postId));
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
		//随机取得一个post并添加收藏
		List<String> _reqJsons = (List<String>) reqData.get("postaddfavorite");
		String addJson = "{" + token + _reqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, postId);
		//发送收藏post的请求
		MyUtils.sendPost(addJson);
		//获取删除收藏的指令
		String delJson = "{" + token + reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//发送取消收藏的指令
		return MyUtils.sendPost(delJson);
	}
}
