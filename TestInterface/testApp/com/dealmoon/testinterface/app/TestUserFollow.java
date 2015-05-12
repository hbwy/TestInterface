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
 * @data:2015��4��13�� ����2:38:43
 * @description: UserFollow ��ע�û� ��Ҫ��¼
 */
public class TestUserFollow {

	private static List<String> reqJsons;
	private static String token; //ִ�й�ע�û��������û�token
	private static int userId; //����ע�û���id

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userfollow");
		//�õ�ִ�й�ע�������û�id
		Map id_token = MyUtils.getRandomIdToken();
		int id = Integer.parseInt((String) id_token.get("id"));
		token = (String) id_token.get("token");
		//�õ�����ע�û���id
		userId = MyUtils.getRandomUserId(id);
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
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
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, userId);
		return MyUtils.sendPost(reqJson0);
	}
}
