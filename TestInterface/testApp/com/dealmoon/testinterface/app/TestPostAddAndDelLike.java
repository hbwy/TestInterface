package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��6��15�� ����3:23:00
 * @description: ϲ��/ȡ��ϲ�� ɹ��  ��Ҫ��¼
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPostAddAndDelLike {

	private static List<String> addReqJsons;
	private static List<String> delReqJsons;
	private static List<String> listReqJsons;

	private static int userId;
	private static String token;
	private static int postId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		addReqJsons = (List<String>) reqData.get("postaddlike");
		delReqJsons = (List<String>) reqData.get("postdellike");
		listReqJsons = (List<String>) reqData.get("postgetlikelist");

		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");

		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// ��֤ϲ��ɹ��result code = 0
	@Test
	public void test1AddLike() {

		String reqJson0 = "{" + token + addReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		String addResponse = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//��֤���ϲ���ɹ�
	@Test
	public void test2Find() {
		String response = find();
		Assert.assertTrue("Error��favorite the post failed", MyUtils.idInArray(response, "posts", postId));
	}

	@Test
	public void test3DelLike() {
		//��ȡɾ��ϲ����ָ��
		String delJson = "{" + token + delReqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//����ȡ��ϲ����ָ��
		String delResponse = MyUtils.sendPost(delJson);

		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ȡ��ϲ��ɹ���Ƿ�ɹ� ϲ��ɹ��--�鿴ϲ����ɹ�����б�--ȡ��ϲ��--�鿴ϲ����ɹ�����б�
	@Test
	public void test4NotFind() {
		String response = find();
		Assert.assertFalse("Error��The post still exist", MyUtils.idInArray(response, "posts", postId));
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void test5AddMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void test6DelMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	private void mustLogin(String reqJson) {
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	private String find(){
		String listJson = "{" + token + listReqJsons.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		return MyUtils.sendPost(listJson);
	}
	@AfterClass
	public static void release() {
		token = null;
		addReqJsons = null;
		delReqJsons = null;
		listReqJsons = null;
	}

}
