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
 * @data:2015年4月10日 下午2:06:01
 * @description: PostAddComment 添加评论 需要登录
 */
public class TestPostAddComment {

	private static Map<String, Object> reqData;
	private static List<String> reqJsons;
	private static String token;
	private static int postId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddcomment");
		token = MyUtils.getRandomToken();

		//随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空
	@Test
	public void testResponseDataNotNull() {
		String response = getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	//验证返回结果中包含gold score coment结构
	@Test
	public void testStructure() {
		String response = getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int gold = responseData.getInt("gold");
		int score = responseData.getInt("score");
		JSONObject comment = responseData.getJSONObject("comment");

		Assert.assertTrue("Error:The gold is error", gold >= 0);
		Assert.assertTrue("Error:The score is error", score >= 0);
		Assert.assertFalse("Error:The comment is null", comment.isNullObject());
	}

	// 验证该晒单增加了该评论 获取晒单的评论 与 增加评论返回的晒单id对比
	@Test
	public void testFind() {
		String response = getReqJson0Response();
		int commentId = (int) JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("comment")
				.get("id");
		List<String> reqJsons = (List<String>) reqData.get("postgetcomment");
		String reqJson = "{" + reqJsons.get(0) + "}";
		String _reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(_reqJson);
		Assert.assertTrue("Error:The post does not contain the comment",
				MyUtils.idInArray(_response, "comments", commentId));
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

	// 验证敏感词汇"喜欢的加q" 未发布送审
	@Test
	public void testBadWord1() {
		String reqJson1 = "{" + token + reqJsons.get(1) + "}";
		reqJson1 = MyUtils.replaceIdinBackCommand(reqJson1, postId);
		String _response = MyUtils.sendPost(reqJson1);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");

		Assert.assertTrue("Error:Sensitive words handling errors", code == 1020);
	}

	// 验证敏感词汇"藏独"  直接屏蔽敏感词
	@Test
	public void testBadWord2() {
		String reqJson2 = "{" + token + reqJsons.get(2) + "}";
		reqJson2 = MyUtils.replaceIdinBackCommand(reqJson2, postId);
		String _response = MyUtils.sendPost(reqJson2);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");

		Assert.assertTrue("Error:Sensitive words handling errors", code == 1020);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}
}
