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
 * @data:2015��4��7�� ����9:45:49
 * @description: PostDelLike ȡ��ϲ��,��Ҫ��¼
 */
public class TestPostDelLike {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static int postId;
	private static Map<String, Object> reqData;
	

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdellike");
		Map user_token = MyUtils.getRandomIdToken();
		userId= Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size() - 1));
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ȡ��ϲ��ɹ���Ƿ�ɹ� ϲ��ɹ��--�鿴ϲ����ɹ�����б�--ȡ��ϲ��--�鿴ϲ����ɹ�����б�
	@Test
	public void testNotFind() {
		getReqJson0Response();
		// ��ȡ��ѯ����ϲ����ɹ����ָ��
		List<String> reqJson2 = (List<String>) reqData.get("postgetlikelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		// ���Ͳ�ѯ����
		String response3 = MyUtils.sendPost(listJson);
		// ��ϲ����post�б��в�ѯ��ϲ�����б�
		Assert.assertFalse("Error��The post still exist", MyUtils.idInArray(response3, "posts", postId));
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
		//���ȡ��һ��post�����ϲ��
		List<String> _reqJsons = (List<String>) reqData.get("postaddlike");
		String addJson = "{" + token + _reqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, postId);
		//����ϲ��post������
		MyUtils.sendPost(addJson);
		//��ȡɾ��ϲ����ָ��
		String delJson = "{" + token + reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//����ȡ��ϲ����ָ��
		return MyUtils.sendPost(delJson);
	}
}
