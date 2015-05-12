package com.dealmoon.testinterface.backend;


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
 * @data:2015年4月27日 上午11:56:17
 * @description: 更新post 图片不需要再次上传,图片url地址写服务器上图片存储地址
 */
public class TestPostUpdate {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Postupdate");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJsonResponse();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJsonResponse();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("The responseData is null", responseData.isNullObject());
	}

	// 验证post不为null
	@Test
	public void testResData() {
		String response = this.getReqJsonResponse();
		JSONObject post = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("post");

		Assert.assertFalse("The responseData is null", post.isNullObject());
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJsonResponse() {
		String reqJson0 = reqJsons.get(0);
		return MyUtils.sendBackPost(reqJson0);
	}

}
