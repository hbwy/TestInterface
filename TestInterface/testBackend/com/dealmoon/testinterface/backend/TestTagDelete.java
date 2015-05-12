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

public class TestTagDelete {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Tagdelete");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//��֤tag�Ƿ�ɹ�ɾ��     ���--��ѯ--ɾ��--��ѯ
	@Test
	public void testNotFind() {
		//���tag
		String reqJson1 = ((List<String>) reqData.get("Tagcreat")).get(0);
		String response1 = MyUtils.sendBackPost(reqJson1);
		//��ȡtag id
		int tagId = JSONObject.fromObject(response1).getJSONObject("responseData").getInt("id");

		//��ȡɾ��tag��ָ��
		String reqJson2 = this.reqJsons.get(0);
		String commandInfo2 = JSONObject.fromObject(reqJson2).getJSONObject("commandInfo").toString();
		reqJson2 = reqJson2.replace(commandInfo2, "{\"id\":" + tagId + "}");
		//����ɾ��tag��ָ��
		MyUtils.sendBackPost(reqJson2);

		//��ȡ��ѯtag��ָ��
		String reqJson3 = ((List<String>) reqData.get("Taginfo")).get(0);
		String commandInfo3 = JSONObject.fromObject(reqJson3).getJSONObject("commandInfo").toString();
		reqJson3 = reqJson3.replace(commandInfo3, "{\"id\":" + tagId + "}");
		//���Ͳ�ѯtagָ��
		String response3 = MyUtils.sendBackPost(reqJson3);
		JSONObject responseData = JSONObject.fromObject(response3).getJSONObject("responseData");

		Assert.assertTrue("Error:delete failed", responseData.isNullObject());
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
