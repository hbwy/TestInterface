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
 * @data:2015��4��24�� ����3:43:42
 * @description: ����Ʒ��
 */
public class TestBrandCreat {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Brandcreat");
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

	//��֤���ص�id��Ϊnull
	@Test
	public void testResIdNotNull() {
		String response = this.getReqJson0Response();
		int id = JSONObject.fromObject(response).getJSONObject("responseData").getInt("id");
		Assert.assertTrue("Error:The id is eror", id >= 0);
	}

	/**
	 * ��֤�����Ƿ�ɹ� ����--��ѯ
	 */
	@Test
	public void testFind() {
		//����brand
		String response = getReqJson0Response();
		int brandId = JSONObject.fromObject(response).getJSONObject("responseData").getInt("id");
		//��ȡ��ѯbrand��ָ��
		String reqJson1 = ((List<String>) reqData.get("Brandinfo")).get(0);
		String commandInfo = JSONObject.fromObject(reqJson1).getJSONObject("commandInfo").toString();
		reqJson1 = reqJson1.replace(commandInfo, "{\"id\":" + brandId + "}");
		//���Ͳ�ѯtagInfoָ��
		String response1 = MyUtils.sendBackPost(reqJson1);
		String brandName = JSONObject.fromObject(response1).getJSONObject("responseData").getString("titleEn");
		Assert.assertNotNull("Error:Failed to create the brand", brandName);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = reqJsons.get(0);
		return MyUtils.sendBackPost(reqJson0);
	}

}