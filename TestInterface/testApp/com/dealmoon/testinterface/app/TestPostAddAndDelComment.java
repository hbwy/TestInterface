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
 * @data:2015年6月15日 下午3:20:53
 * @description:添加/删除评论 需要登录
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPostAddAndDelComment {

	private static List<String> addReqJsons;
	private static List<String> delReqJsons;
	private static List<String> listReqJsons;

	private static String addResponse;
	private static String token;
	private static int postId;
	private static int commentId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		addReqJsons = (List<String>) reqData.get("postaddcomment");
		delReqJsons = (List<String>) reqData.get("postdelcomment");
		listReqJsons = (List<String>) reqData.get("postgetcomment");

		token = MyUtils.getRandomToken();
		//随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	//验证添加评论result code=0
	@Test
	public void test0AddComment() {
		String reqJson0 = "{" + token + addReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		addResponse = MyUtils.sendPost(reqJson0);

		commentId = (int) JSONObject.fromObject(addResponse).getJSONObject("responseData").getJSONObject("comment")
				.get("id");

		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空
	@Test
	public void test1ResponseDataNotNull() {
		JSONObject responseData = JSONObject.fromObject(addResponse).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	// 验证返回结果中包含gold score coment结构
	@Test
	public void test2Structure() {
		JSONObject responseData = JSONObject.fromObject(addResponse).getJSONObject("responseData");
		int gold = responseData.getInt("gold");
		int score = responseData.getInt("score");
		JSONObject comment = responseData.getJSONObject("comment");

		Assert.assertTrue("Error:The gold is error", gold >= 0);
		Assert.assertTrue("Error:The score is error", score >= 0);
		Assert.assertFalse("Error:The comment is null", comment.isNullObject());
	}

	// 验证该晒单增加了该评论 获取晒单的评论 与 增加评论返回的晒单id对比
	@Test
	public void test3Find() {
		String response = find();
		Assert.assertTrue("Error:The post does not contain the comment",
				MyUtils.idInArray(response, "comments", commentId));
	}

	// 验证删除评论 result code = 0
	@Test
	public void test4DelComment() {
		String delResponse = delComment(commentId);
		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证删除的评论查不到
	@Test
	public void test5NotFind() {
		String response = find();
		// post下没有该评论
		Assert.assertFalse("Error:The post is still contains the comment",
				MyUtils.idInArray(response, "comments", commentId));
	}

	// 验证敏感词汇"喜欢的加q" 未发布送审
	@Test
	public void test6BadWord1() {
		String reqJson1 = "{" + token + addReqJsons.get(1) + "}";
		badWord(reqJson1);
	}

	// 验证敏感词汇"藏独"  直接屏蔽敏感词
	@Test
	public void test7BadWord2() {
		String reqJson2 = "{" + token + addReqJsons.get(2) + "}";
		badWord(reqJson2);
	}

	// 验证添加评论操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test8AddMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// 验证删除评论操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test9DelMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	//删除评论
	private String delComment(int commentId) {

		String reqJson0 = "{" + token + delReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, commentId);
		//发送删除平论的请求
		return MyUtils.sendPost(reqJson0);
	}

	//获取晒单评论
	private String find() {
		String reqJson = "{" + listReqJsons.get(0) + "}";
		String _reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		return MyUtils.sendPost(_reqJson);
	}

	//验证评论敏感词
	private void badWord(String reqJson) {
		reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertTrue("Error:Sensitive words handling errors", code == 1020);
	}

	private void mustLogin(String reqJson){
		reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}
	
	@AfterClass
	public static void release() {
		addReqJsons = null;
		delReqJsons = null;
		listReqJsons = null;
		addResponse = null;
		token = null;
	}
}
