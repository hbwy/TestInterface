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
 * @data:2015��4��24�� ����4:55:51
 * @description: ��ѯɹ����������ѯ��
 */
public class TestPostList {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Postlist");
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

	//��֤postNums
	@Test
	public void testPostNums(){
		String response = this.getReqJson0Response();
		JSONObject postNums = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("postNums");
		Assert.assertFalse("Error:The postNums is null", postNums.isNullObject());
	}
	//��֤total  ���ڵ���0
	@Test
	public void testTotal() {
		String response = this.getReqJson0Response();
		int total = JSONObject.fromObject(response).getJSONObject("responseData").getInt("total");
		Assert.assertTrue("Error:The total is null", total >= 0);
	}

	// ��֤posts���鲻Ϊ��
	@Test
	public void testPostsNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int size = responseData.getJSONArray("posts").size();
		Assert.assertTrue("Error:The posts array is null", size >= 0);

	}

	//��֤state����Ϊ��   Ϊ��ʱ�����������͵�post
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
