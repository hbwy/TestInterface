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
 * @data:2015��4��16�� ����9:07:30
 * @description: HashTagInfo Tag���� ����Ҫ��¼
 */
public class TestTagInfo {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("taginfo");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊnull, ��ͨ����Ϊnull
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// ��֤tag type=hashtag ����hashtag�ṹ��post���� ������Ϊnull
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

	// ��֤tag type=brand ����brand�ṹ��post���� ������Ϊnull
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
	
	// ��֤tag type=brand brand������  ����brand�ṹ��post���� ������Ϊnull
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

	// ��֤tag type=store ����store�ṹ��post���� ������Ϊnull
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

	// ��֤tag Name=??????? ���Ҳ����᷵��type��hashtag�ṹ
	@Test
	public void testTagNotExist() {
		String reqJson3 = "{" + reqJsons.get(3) + "}";
		String response = MyUtils.sendPost(reqJson3);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject hashtag = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("hashtag");
		Assert.assertEquals("Error:The type error", "hashtag", resType);
		Assert.assertFalse("Error:The hashtag is null", hashtag.isNullObject());
	}

	// ��֤tag NameΪ�մ�
	@Test
	public void testTagNameNull() {
		String reqJson4 = "{" + reqJsons.get(4) + "}";
		String response = MyUtils.sendPost(reqJson4);
		String resType = JSONObject.fromObject(response).getJSONObject("responseData").getString("type");
		JSONObject hashtag = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("hashtag");
		Assert.assertEquals("Error:The type error", "hashtag", resType);
		Assert.assertFalse("Error:The hashtag is null", hashtag.isNullObject());
	}

	// ��֤tag NameΪ*��
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
