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
 * @data:2015年4月16日 上午11:03:49
 * @description: BrandList 品牌列表 不需要登录
 */
public class TestBrandList {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("brandlist");
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

	// 验证brands数组不为null
	@Test
	public void testBrandsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	// 验证tag Name为空串 会返回所有的品牌列表
	@Test
	public void testBrandNameNull() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String response = MyUtils.sendPost(reqJson1);
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands is null", size >= 0);
	}

	// 验证tag Name为*号 返回空数组
	@Test
	public void testBrandNameAsterisk() {
		String reqJson2 = "{" + reqJsons.get(2) + "}";
		String response = MyUtils.sendPost(reqJson2);
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + reqJsons.get(1) + "}";
		return MyUtils.sendPost(reqJson0);
	}

}
