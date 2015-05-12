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
 * @data:2015��4��24�� ����2:28:08
 * @description: ��ѯƷ�� (������ѯ)
 */
public class TestBrandList {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Brandlist");
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

	//��֤total  ���ڵ���0
	@Test
	public void testTotal() {
		String response = this.getReqJson0Response();
		int total = JSONObject.fromObject(response).getJSONObject("responseData").getInt("total");
		Assert.assertTrue("Error:The total is null", total >= 0);
	}

	// ��֤brands���鲻Ϊ��
	@Test
	public void testBrandsNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int size = responseData.getJSONArray("brands").size();
		Assert.assertTrue("Error:The brands array is null", size >= 0);

	}

	//��֤state=0
	@Test
	public void testState0() {
		String reqJson1 = reqJsons.get(1);
		String response = MyUtils.sendBackPost(reqJson1);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("brands").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	//��֤state=1
	@Test
	public void testState1() {
		String reqJson2 = reqJsons.get(2);
		String response = MyUtils.sendBackPost(reqJson2);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("brands").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	//��֤state=2
	@Test
	public void testState2() {
		String reqJson3 = reqJsons.get(3);
		String response = MyUtils.sendBackPost(reqJson3);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("brands").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	//��֤�����ַ�?��
	@Test
	public void testSearchQuestionMark() {
		String reqJson4 = reqJsons.get(4);
		String response = MyUtils.sendBackPost(reqJson4);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("brands").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The brands array is null", size >= 0);
	}

	//��֤�����ַ�*��
	@Test
	public void testSearchAsterisk() {
		String reqJson5 = reqJsons.get(5);
		String response = MyUtils.sendBackPost(reqJson5);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int total = responseData.getInt("total");
		int size = responseData.getJSONArray("brands").size();
		Assert.assertTrue("Error:The total is null", total >= 0);
		Assert.assertTrue("Error:The brands array is null", size >= 0);
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
