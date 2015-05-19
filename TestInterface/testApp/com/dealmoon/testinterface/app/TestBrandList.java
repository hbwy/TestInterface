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
 * @data:2015��4��16�� ����11:03:49
 * @description: BrandList Ʒ���б� ����Ҫ��¼
 */
public class TestBrandList {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("brandlist");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		System.out.println(response);
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

	// ��֤brands���鲻Ϊnull
	@Test
	public void testBrandsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	// ��֤tag NameΪ�մ� �᷵�����е�Ʒ���б�
	@Test
	public void testBrandNameNull() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String response = MyUtils.sendPost(reqJson1);
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands is null", size >= 0);
	}

	// ��֤tag NameΪ*�� ���ؿ�����
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
