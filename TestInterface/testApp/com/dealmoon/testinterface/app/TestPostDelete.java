package com.dealmoon.testinterface.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @data:2015年4月3日 下午5:59:23
 * @description:TestPostDelete 删除晒单 需要登录
 */
public class TestPostDelete {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static Map<String, Object> reqData;
	private static int postId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdelete");
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证删除后查不到
	@Test
	public void testNotFind() {
		String response = this.getReqJson0Response();
		// 构造查询该post的指令
		String response2 = MyUtils.getPostInfo(postId);
		JSONObject json_resData = JSONObject.fromObject(response2).getJSONObject("responseData");
		Assert.assertTrue("Error:Delete is not successful", json_resData.isNullObject());
	}

	// 验证非自己的晒单不能删除 code=1004
	@Test
	public void testDelNotOwn() {
		String reqJson2 = "{" + MyUtils.getRandomToken(userId) + reqJsons.get(2) + "}";
		String _response = MyUtils.sendPost(reqJson2);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {

		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String _response = MyUtils.sendPost(reqJson1);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	// 释放资源
	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		// 获取create post的指令
		List<String> reqJsons = (List<String>) reqData.get("postcreate");
		String addJson = "{" + token + reqJsons.get(0) + "}";

		// textMap用于存文本,fileMap用于存图片
		Map<String, String> textMap = new HashMap<String, String>();
		Map<String, String> fileMap = new HashMap<String, String>();

		textMap.put("requestData", addJson);

		String image_path1 = "image/1.jpg";

		fileMap.put("images", image_path1);
		// 发送create post请求
		String response1 = MyUtils.postUpload(textMap, fileMap, addJson);
		// 获取新创建的post的id
		postId = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONObject("post").getInt("id");
		// 构造删除该post的指令
		String delJson = "{" + token + this.reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		// 发送删除该post的请求
		return MyUtils.sendPost(delJson);
	}
}
