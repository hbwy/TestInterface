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

/**
 * @author: WY
 * @data:2015年4月23日 下午1:15:54
 * @description:查询tag多条记录
 */
public class TestTagList {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Taglist");
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

	//验证total  大于等于0
	@Test
	public void testTotal() {
		String response = this.getReqJson0Response();
		int total = JSONObject.fromObject(response).getJSONObject("responseData").getInt("total");
		Assert.assertTrue("Error:The total is null", total >= 0);
	}

	// 验证tags数组不为空
	@Test
	public void testTagsNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int size = responseData.getJSONArray("tags").size();
		Assert.assertTrue("Error:The tags array is null", size >= 0);

	}

	//验证待审、 非热门
	@Test
	public void testPendingAndNotRecommend() {
		String reqJson1 = reqJsons.get(1);
		String response = MyUtils.sendBackPost(reqJson1);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("tags").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	//验证特殊字符*号
	@Test
	public void testSearchAsterisk() {
		String reqJson2 = reqJsons.get(2);
		String response = MyUtils.sendBackPost(reqJson2);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("tags").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	//验证特殊字符"号
	@Test
	public void testSearchSemicolon() {
		String reqJson3 = reqJsons.get(3);
		String response = MyUtils.sendBackPost(reqJson3);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("tags").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
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
