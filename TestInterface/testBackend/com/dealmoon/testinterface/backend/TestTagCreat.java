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

public class TestTagCreat {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Tagcreat");
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
	 * ��֤����Ƿ�ɹ� ���--��ѯ
	 */
	@Test
	public void testFind() {
		//���tag
		String response = getReqJson0Response();
		int tagId = JSONObject.fromObject(response).getJSONObject("responseData").getInt("id");
		//��ȡ��ѯtag��ָ��
		String tagInfoCommand = ((List<String>) reqData.get("Taginfo")).get(0);
		String commandInfo = JSONObject.fromObject(tagInfoCommand).getJSONObject("commandInfo").toString();
		tagInfoCommand = tagInfoCommand.replace(commandInfo, "{\"id\":" + tagId + "}");
		//���Ͳ�ѯtagInfoָ��
		String response1 = MyUtils.sendBackPost(tagInfoCommand);
		String tagName = JSONObject.fromObject(response1).getJSONObject("responseData").getString("name");
		Assert.assertNotNull("Error:Failed to create the tag", tagName);
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
