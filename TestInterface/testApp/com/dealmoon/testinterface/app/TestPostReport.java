package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月10日 下午4:33:40
 * @description: PostReport 举报晒单 需要登录
 */
public class TestPostReport {

	private static List<String> reqJsons;
	private static Map<String, String> tokens;
	private static Map<String, Object> reqData;
	private static int postId;
	private static int post_userId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postreport");
		tokens = PropertiesReader.getTokens();
		//随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
		//查询post信息
		String response = MyUtils.getPostInfo(postId);
		//获取post的author的Id
		post_userId = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("post")
				.getJSONObject("author").getInt("id");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:result code 不为 0", 0, code);
	}

	// 验证同一晒单被不同用户举报6次不能查询到
	// 不同用户举报同1晒单6次，post被隐藏，编辑和自己可见  用6个用户举报同1晒单 晒单本人 查看 可见 其他用户查看 不可见
	@Test
	public void testNotFind() {
		String reportJson = reqJsons.get(0);
		reportJson = MyUtils.replaceIdinAppCommand(reportJson, postId);
		for (int i = 0; i < 8; i++) {
			String reqJson0 = "{" + MyUtils.getRandomToken(post_userId) + reportJson + "}";
			String response1 = MyUtils.sendPost(reqJson0);
		}
		String response = MyUtils.getPostInfo(postId);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertTrue("Error:Failure to report", responseData.isNullObject());

	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:result code 不为 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
		tokens = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + MyUtils.getRandomToken(post_userId) + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}

}
