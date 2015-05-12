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
 * @data:2015年4月22日 下午3:44:08
 * @description: 喜欢的品牌的列表 不需要登录
 */
public class TestBrandLikeList {

	private static List<String> reqJsons;
	private static int userId;
	
	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("brandlikelist");
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为null, 不通过则为null
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// 验证brands数组不为null
	@Test
	public void testBrandsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceUserid(reqJson0, userId);
		return MyUtils.sendPost(reqJson0);
	}

}
