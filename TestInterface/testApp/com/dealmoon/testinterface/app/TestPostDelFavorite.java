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
 * @data:2015��4��7�� ����9:37:59
 * @description:PostDelFavorite ȡ����עɹ��,��Ҫ��¼
 */
public class TestPostDelFavorite {

	private static List<String> reqJsons;
	private static String token;
	private static int postId;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdelfavorite");
		token = MyUtils.getRandomToken();
		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// / ��֤ȡ���ղ�ɹ���Ƿ�ɹ� �ղ�ɹ��--�鿴�ղص�ɹ�����б�--ȡ���ղ�--�鿴�ղص�ɹ�����б�
	@Test
	public void testNotFind() {
		getReqJson0Response();
		// ��ȡ��ѯ�����ղص�ɹ����ָ��
		List<String> reqJson2 = (List<String>) reqData.get("postgetfavoritelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		// ���Ͳ�ѯ����
		String response = MyUtils.sendPost(listJson);
		// ���ղص�post�б��в�ѯ���ղص��б�
		Assert.assertFalse("Error��The user still exist", MyUtils.idInArray(response, "posts", postId));
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
		//���ȡ��һ��post������ղ�
		List<String> _reqJsons = (List<String>) reqData.get("postaddfavorite");
		String addJson = "{" + token + _reqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, postId);
		//�����ղ�post������
		MyUtils.sendPost(addJson);
		//��ȡɾ���ղص�ָ��
		String delJson = "{" + token + reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//����ȡ���ղص�ָ��
		return MyUtils.sendPost(delJson);
	}
}
