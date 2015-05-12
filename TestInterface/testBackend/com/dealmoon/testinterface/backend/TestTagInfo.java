package com.dealmoon.testinterface.backend;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

public class TestTagInfo {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Taginfo");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		System.out.println(response);
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

	//验证返回的结构是否完整
	@Test
	public void testStructure() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int id = responseData.getInt("id");
		String name = responseData.getString("name");
		String type = responseData.getString("type");
		boolean isRecommend = responseData.getBoolean("isRecommend");
		int nums = responseData.getInt("nums");
		String addTime = responseData.getString("addTime");
		int state = responseData.getInt("state");
		
		Assert.assertTrue("Error:The id is null", id >= 0);
		Assert.assertTrue("Error:The nums is null", nums >= 0);
		Assert.assertNotNull("Error:The name is null", name);
		Assert.assertNotNull("Error:The type is null", type);
		Assert.assertNotNull("Error:The addTime is null", addTime);
		Assert.assertTrue("Error:The isRecommend is null", isRecommend == true||isRecommend == false);
		Assert.assertTrue("Error:The state is null", state >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = reqJsons.get(0);
		return MyUtils.sendBackPost(reqJson0);
	}

}
