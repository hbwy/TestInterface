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
 * @data:2015年4月16日 上午9:07:30
 * @description: HashTagInfo Tag详情 不需要登录
 */
public class TestTagInfo {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("taginfo");
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

	// 验证tag type=hashtag 返回hashtag结构和post数组 都不能为null
	@Test
	public void testHashTag() {
		String response = this.getReqJson0Response();
		String reqType = JSONObject.fromObject("{" + reqJsons.get(0) + "}").getJSONObject("commandInfo")
				.getString("type");
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject hashtag = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("hashtag");
		Assert.assertEquals("Error:The type error", reqType, resType);
		Assert.assertFalse("Error:The hashtag is null", hashtag.isNullObject());
	}

	// 验证tag type=brand 返回brand结构和post数组 都不能为null
	@Test
	public void testBrand() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String reqType = JSONObject.fromObject(reqJson1).getJSONObject("commandInfo").getString("type");
		String response = MyUtils.sendPost(reqJson1);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject brand = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("brand");
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertEquals("Error:The type error", reqType, resType);
		Assert.assertFalse("Error:The hashtag is null", brand.isNullObject());
		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}
	
	// 验证tag type=brand brand不存在  返回brand结构和post数组 都不能为null
	//	@Test
	//	public void testBrandNoExist(){
	//		String reqJson1 = "{" + reqJsons.get(6) + "}";
	//		String reqType = JSONObject.fromObject(reqJson1).getJSONObject("commandInfo").getString("type");
	//		String response = MyUtils.sendPost(reqJson1);
	//		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
	//		JSONObject brand = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("brand");
	//		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();
	//
	//		Assert.assertEquals("Error:The type error", reqType, resType);
	//		Assert.assertFalse("Error:The hashtag is null", brand.isNullObject());
	//		Assert.assertTrue("Error:The posts array is null", size >= 0);
	//	}

	// 验证tag type=store 返回store结构和post数组 都不能为null
	@Test
	public void testStore() {
		String reqJson2 = "{" + reqJsons.get(2) + "}";
		String reqType = JSONObject.fromObject(reqJson2).getJSONObject("commandInfo").getString("type");
		String response = MyUtils.sendPost(reqJson2);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject store = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("store");
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertEquals("Error:The type error", reqType, resType);
		Assert.assertFalse("Error:The hashtag is null", store.isNullObject());
		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}

	// 验证tag Name=??????? 查找不到会返回type和hashtag结构
	@Test
	public void testTagNotExist() {
		String reqJson3 = "{" + reqJsons.get(3) + "}";
		String response = MyUtils.sendPost(reqJson3);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject hashtag = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("hashtag");
		Assert.assertEquals("Error:The type error", "hashtag", resType);
		Assert.assertFalse("Error:The hashtag is null", hashtag.isNullObject());
	}

	// 验证tag Name为空串
	@Test
	public void testTagNameNull() {
		String reqJson4 = "{" + reqJsons.get(4) + "}";
		String response = MyUtils.sendPost(reqJson4);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject hashtag = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("hashtag");
		Assert.assertEquals("Error:The type error", "hashtag", resType);
		Assert.assertFalse("Error:The hashtag is null", hashtag.isNullObject());
	}

	// 验证tag Name为*号
	@Test
	public void testTagNameAsterisk() {
		String reqJson5 = "{" + reqJsons.get(5) + "}";
		String response = MyUtils.sendPost(reqJson5);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject hashtag = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("hashtag");
		Assert.assertEquals("Error:The type error", "hashtag", resType);
		Assert.assertFalse("Error:The hashtag is null", hashtag.isNullObject());
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}

}
