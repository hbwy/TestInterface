package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月10日 下午4:11:57
 * @description: PostGetLikeList 获取用户喜欢的晒单的列表 *登录与不登录看到的用户喜欢的晒单列表不同
 */
public class TestPostGetLikeList {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postgetlikelist");
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

	// 验证responseData不为空, 不通过则为空
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// 验证posts数组不为null,没有评论返回空数组,不应该为null
	@Test
	public void testPostsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceUserid(reqJson0, userId);
		return MyUtils.sendPost(reqJson0);
	}

}
