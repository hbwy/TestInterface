package com.dealmoon.testinterface.backend;

import static org.junit.Assert.*;

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
 * @data:2015��4��24�� ����11:44:17
 * @description: �޸�tag
 */
public class TestTagUpdate {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;
	
	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Tagupdate");
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
		//���tag
		String reqJson1 = ((List<String>) reqData.get("Tagcreat")).get(0);
		String response1 = MyUtils.sendBackPost(reqJson1);
		//��ȡtag id
		int tagId = JSONObject.fromObject(response1).getJSONObject("responseData").getInt("id");
		
		//��ȡ����tag��ָ��
		String reqJson2 = this.reqJsons.get(0);
		JSONObject commandInfo2 = JSONObject.fromObject(reqJson2).getJSONObject("commandInfo");
		int id = commandInfo2.getInt("id");
		reqJson2 = reqJson2.replace("\"id\":"+id+",", "\"id\":" + tagId + ",");
		//���͸���tag��ָ��
		MyUtils.sendBackPost(reqJson2);

		//��ȡ��ѯtag��ָ��
		String reqJson3 = ((List<String>) reqData.get("Taginfo")).get(0);
		String commandInfo3 = JSONObject.fromObject(reqJson3).getJSONObject("commandInfo").toString();
		reqJson3 = reqJson3.replace(commandInfo3, "{\"id\":" + tagId + "}");
		//���Ͳ�ѯtagָ��
		String response3 = MyUtils.sendBackPost(reqJson3);
		JSONObject responseData = JSONObject.fromObject(response3).getJSONObject("responseData");

		Assert.assertEquals("Error:The name is error", commandInfo2.getString("name"), responseData.getString("name"));
		Assert.assertEquals("Error:The type is error", commandInfo2.getString("type"), responseData.getString("type"));
		Assert.assertEquals("Error:The state is error", commandInfo2.getString("state"), responseData.getString("state"));
		Assert.assertEquals("Error:The isRecommend is error", commandInfo2.getString("isRecommend"), responseData.getString("isRecommend"));
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
