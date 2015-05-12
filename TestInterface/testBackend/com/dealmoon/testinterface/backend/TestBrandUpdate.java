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
 * @data:2015��4��24�� ����4:18:43
 * @description: ����Ʒ��
 */
public class TestBrandUpdate {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Brandupdate");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//��֤���³ɹ�       ����--����--��ѯ
	@Test
	public void testFind() {
		//���brand
		String reqJson1 = ((List<String>) reqData.get("Brandcreat")).get(0);
		String response1 = MyUtils.sendBackPost(reqJson1);
		//��ȡbrand id
		int brandId = JSONObject.fromObject(response1).getJSONObject("responseData").getInt("id");

		//��ȡ����brand��ָ��
		String reqJson2 = this.reqJsons.get(0);
		JSONObject commandInfo2 = JSONObject.fromObject(reqJson2).getJSONObject("commandInfo");
		int id = commandInfo2.getInt("id");
		reqJson2 = reqJson2.replace("\"id\":" + id + ",", "\"id\":" + brandId + ",");
		//���͸���brand��ָ��
		MyUtils.sendBackPost(reqJson2);

		//��ȡ��ѯbrand��ָ��
		String reqJson3 = ((List<String>) reqData.get("Brandinfo")).get(0);
		String commandInfo3 = JSONObject.fromObject(reqJson3).getJSONObject("commandInfo").toString();
		reqJson3 = reqJson3.replace(commandInfo3, "{\"id\":" + brandId + "}");
		//���Ͳ�ѯbrandָ��
		String response3 = MyUtils.sendBackPost(reqJson3);
		JSONObject responseData = JSONObject.fromObject(response3).getJSONObject("responseData");

		Assert.assertEquals("Error:The titleEn is error", commandInfo2.getString("titleEn"),
				responseData.getString("titleEn"));
		Assert.assertEquals("Error:The logo is error", commandInfo2.getString("logo"), responseData.getString("logo"));
		Assert.assertEquals("Error:The state is error", commandInfo2.getString("state"),
				responseData.getString("state"));
		Assert.assertEquals("Error:The isHot is error", commandInfo2.getString("isHot"),
				responseData.getString("isHot"));
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
