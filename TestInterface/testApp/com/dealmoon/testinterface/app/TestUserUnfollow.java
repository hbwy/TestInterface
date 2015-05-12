package com.dealmoon.testinterface.app;

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
 * @data:2015��4��13�� ����2:46:17
 * @description: UserUnfollow ȡ����ע�û� ��Ҫ��¼
 */
public class TestUserUnfollow {

	private static List<String> reqJsons;
	private static String token; //ִ�й�ע�û��������û�token
	private static int id; //ִ�й�ע�û��������û�id
	private static int userId; //����ע�û���id
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userunfollow");
		//�õ�ִ�й�ע�������û�id
		Map id_token = MyUtils.getRandomIdToken();
		id = Integer.parseInt((String) id_token.get("id"));
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

	// ��֤ȡ����ע�û� �ȹ�ע�û�,��ѯ��ע���û����б�,ȡ����ע�û�,��ѯ��ע���û����б�
	@Test
	public void testNotFind() {
		String response = getReqJson0Response();
		// ��ȡ��ѯ���˹�ע���û��б��ָ��
		List<String> reqJson2 = (List<String>) reqData.get("userfollowlist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceIdinBackCommand(listJson, id);
		// ���Ͳ�ѯ����
		String response2 = MyUtils.sendPost(listJson);
		// �ڹ�ע�б��в�ѯ��ע���û�
		Assert.assertFalse("Error��Follow the user failed", MyUtils.idInArray(response2, "users", userId));
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
		// ��ȡ��ע�û�ָ��
		List<String> reqJson1 = (List<String>) reqData.get("userfollow");
		String addJson = "{" + token + reqJson1.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, userId);
		// ���͹�ע�û�������
		MyUtils.sendPost(addJson);
		//��ȡȡ����ע�û�ָ��
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, userId);
		return MyUtils.sendPost(reqJson0);
	}
}
