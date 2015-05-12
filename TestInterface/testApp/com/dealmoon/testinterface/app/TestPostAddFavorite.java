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
 * @data:2015年4月7日 上午9:16:51
 * @description: PostAddFavorite 关注晒单,需要登录
 */
public class TestPostAddFavorite {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;
	private static int postId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddfavorite");
		token = MyUtils.getRandomToken();

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

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		String _response = MyUtils.sendPost(reqJson0);
		
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	//验证收藏晒单成功  查询随机post,收藏,查看收藏列表
	@Test
	public void testFind() {
		getReqJson0Response();
		List<String> reqJson2 = (List<String>) reqData.get("postgetfavoritelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
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
