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
 * @data:2015��4��3�� ����5:36:53
 * @description:HashTagSearch ����tag ����Ҫ��¼
 */
public class TestHashTagSearch {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData =PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("hashtagsearch");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	// ��֤tags���鲻Ϊnull
	@Test
	public void testTagsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	/**
	 * ��֤�����ַ�,ģ����������Ϊ�մ� �����еĵڶ���ָ�� code=0 �������������е��Ƽ���tag
	 */
	@Test
	public void testSearchNull() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String response = MyUtils.sendPost(reqJson1);
		JSONObject json_response = JSONObject.fromObject(response);
		int code = json_response.getJSONObject("result").getInt("code");
		int size = json_response.getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertEquals("Error:The result code is not 0", 0, code);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	/**
	 * ��֤�����ַ�,ģ����������Ϊ*�� �����еĵ�����ָ�� code=0 tags����Ϊ��
	 */
	@Test
	public void testSearchAsterisk() {
		String reqJson2 = "{" + reqJsons.get(2) + "}";
		String response = MyUtils.sendPost(reqJson2);
		JSONObject json_response = JSONObject.fromObject(response);
		int code = json_response.getJSONObject("result").getInt("code");
		int size = json_response.getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertEquals("Error:The result code is not 0", 0, code);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	/**
	 * ��֤�����ַ�,ģ����������Ϊ"�� �����еĵ�����ָ�� code=0 tags����Ϊ��
	 */
	@Test
	public void testSearchSemicolon() {
		String reqJson3 = "{" + reqJsons.get(3) + "}";
		String response = MyUtils.sendPost(reqJson3);
		JSONObject json_response = JSONObject.fromObject(response);
		int code = json_response.getJSONObject("result").getInt("code");
		int size = json_response.getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertEquals("Error:The result code is not 0", 0, code);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
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
