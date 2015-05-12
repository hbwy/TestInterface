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
 * @data:2015��4��27�� ����11:56:17
 * @description: ����post ͼƬ����Ҫ�ٴ��ϴ�,ͼƬurl��ַд��������ͼƬ�洢��ַ
 */
public class TestPostUpdate {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Postupdate");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJsonResponse();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJsonResponse();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("The responseData is null", responseData.isNullObject());
	}

	// ��֤post��Ϊnull
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
