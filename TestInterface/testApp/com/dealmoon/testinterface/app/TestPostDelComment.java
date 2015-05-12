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
 * @data:2015年4月10日 下午3:45:51
 * @description: PostDelComment 删除评论 需要登录 只允许发布该评论的人操作
 */
public class TestPostDelComment {

	private static List<String> reqJsons;
	private static String token;
	private static int postId;
	private static Map<String, Object> reqData;
	private static int commentId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdelcomment");
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

	// 验证删除的评论查不到 添加 查找 删除 查找
	@Test
	public void testNotFind() {
		//随机评论一个post,并删除该评论
		getReqJson0Response();
		//查询该评论
		List<String> reqJsons = (List<String>) reqData.get("postgetcomment");
		String reqJson = "{" + reqJsons.get(0) + "}";
		String _reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(_reqJson);
		// 断言post下没有该评论
		Assert.assertFalse("Error:The post is still contains the comment",
				MyUtils.idInArray(_response, "comments", commentId));
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
		reqData = null;
	}

	private String getReqJson0Response() {
		//先添加一条评论 在删除
		List<String> _reqJsons = (List<String>) reqData.get("postaddcomment");
		String addJson = "{" + token + _reqJsons.get(0) + "}";

		addJson = MyUtils.replaceIdinBackCommand(addJson, postId);
		String addResponse = MyUtils.sendPost(addJson);
		//获取新添加的评论的id
		commentId = (int) JSONObject.fromObject(addResponse).getJSONObject("responseData").getJSONObject("comment")
				.get("id");
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, commentId);
		//发送删除平论的请求
		return MyUtils.sendPost(reqJson0);
	}
}
