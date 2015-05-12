package com.dealmoon.testinterface.app;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��4��23�� ����11:12:43
 * @description:����Ϣ ��Ҫ��¼
 */
public class TestMessageRead {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messageread");
		token = MyUtils.getRandomToken();
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//��֤��Ϣ�Ѷ�   �鿴 ����Ϣ �鿴
	@Test
	public void testRead() {
		//�鿴��Ϣ�б� ��ȡһ��δ����Ϣ��id
		List<String> reqJsons = (List<String>) reqData.get("messagegetlist");
		String response0 = MyUtils.sendPost("{" + token + reqJsons.get(2) + "}");
		JSONArray messages = JSONObject.fromObject(response0).getJSONObject("responseData").getJSONArray("messages");
		//Ĭ���ǵ�һ����Ϣ��id,���۵�һ�����Ѷ�����δ��
		int messageId = 1;
		for (Iterator iterator = messages.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			if (!obj.getBoolean("isRead")) {
				messageId = obj.getInt("id");
				break;
			}
		}
		//��ȡ����Ϣ��ָ��
		String reqJson0 = "{" + token + this.reqJsons.get(0) + "}";
		String commandInfo = JSONObject.fromObject(reqJson0).getString("commandInfo");
		reqJson0 = reqJson0.replace(commandInfo, "{\"id\":[" + messageId + "]}");
		//���Ͷ���Ϣָ��
		MyUtils.sendPost(reqJson0);
		//�鿴����Ϣ��״̬   isRead=true
		String response1 = MyUtils.sendPost("{" + token + reqJsons.get(2) + "}");
		JSONArray messages1 = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONArray("messages");
		boolean isRead = false;
		for (Iterator iterator = messages1.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			if (obj.getInt("id") == messageId) {
				isRead = obj.getBoolean("isRead");
				break;
			}
		}

		Assert.assertTrue("Error:Read message failed", isRead);
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}

}
