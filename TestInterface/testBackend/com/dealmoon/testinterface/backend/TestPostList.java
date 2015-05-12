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
 * @data:2015年4月24日 下午4:55:51
 * @description: 查询晒单（多条查询）
 */
public class TestPostList {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Postlist");
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

	//验证postNums
	@Test
	public void testPostNums(){
		String response = this.getReqJson0Response();
		JSONObject postNums = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("postNums");
		Assert.assertFalse("Error:The postNums is null", postNums.isNullObject());
	}
	//验证total  大于等于0
	@Test
	public void testTotal() {
		String response = this.getReqJson0Response();
		int total = JSONObject.fromObject(response).getJSONObject("responseData").getInt("total");
		Assert.assertTrue("Error:The total is null", total >= 0);
	}

	// 验证posts数组不为空
	@Test
	public void testPostsNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int size = responseData.getJSONArray("posts").size();
		Assert.assertTrue("Error:The posts array is null", size >= 0);

	}

	//验证state数组为空   为空时返回所有类型的post
	@Test
	public void testStateNull() {
		String reqJson1 = reqJsons.get(1);
		String response = MyUtils.sendBackPost(reqJson1);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		JSONObject postNums = responseData.getJSONObject("postNums");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("posts").size();
		Assert.assertFalse("Error:The postNums is null", postNums.isNullObject());
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The posts array is null", size >= 0);
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
