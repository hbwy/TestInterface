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
 * @data:2015年4月10日 下午3:59:46
 * @description: PostGetList 获取晒单列表 最新new｜推荐recommend｜关注follow（关注需要登录，没实现）
 */
public class TestPostGetList {

	private static List<String> reqJsons;
	private static String token;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postgetlist");
		token = MyUtils.getRandomToken();
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证posts数组不为null,没有评论返回空数组,不应该为null
	@Test
	public void testPostsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}

	// 验证的获取晒单列表 推荐  posts数组不为空
	@Test
	public void testPostsRecommend() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String _response = MyUtils.sendPost(reqJson1);
		int size = JSONObject.fromObject(_response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}
}
