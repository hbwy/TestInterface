package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��4��23�� ����11:47:50
 * @description: ɾ����Ϣ ��Ҫ��¼
 */
public class TestMessageDelete {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;
	private static int messageId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messagedelete");
		token = MyUtils.getRandomToken();
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤��Ϣɾ��   �鿴   ɾ��   �鿴
	@Test
	public void testNotFind() {
		//�����ȡ��Ϣ��ɾ��
		String response = getReqJson0Response();
		//�鿴��Ϣ�б��Ƿ���ڸ���Ϣ
		List<Integer> messageList = MyUtils.getMessageList(token);
		int _id = -1;
		for (Integer id : messageList) {
			if (messageId == id) {
				_id = id;
				break;
			}
		}
		Assert.assertFalse("Error:The messages contain the message", messageId == _id);
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
		//�����ȡ��Ϣ�б��е���Ϣ
		List<Integer> messageList = MyUtils.getMessageList(token);
		int size = messageList.size();
		messageId = 1;
		if(size>0){
			messageId = messageList.get(new Random().nextInt(messageList.size()));
		}
		//ɾ����Ϣ
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceMessageid(reqJson0, messageId);
		return MyUtils.sendPost(reqJson0);
	}

}
