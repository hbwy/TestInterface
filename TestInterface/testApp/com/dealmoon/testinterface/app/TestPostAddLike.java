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
 * @data:2015��4��7�� ����9:42:55
 * @description: PostAddLike ���ϲ��,��Ҫ��¼
 */
public class TestPostAddLike {

	private static List<String> reqJsons;
	private static int userId;
	private static String token;
	private static int postId;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddlike");
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");

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

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	//��֤���ϲ���ɹ�
	@Test
	public void testFind() {
		getReqJson0Response();
		List<String> reqJson2 = (List<String>) reqData.get("postgetlikelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		String response = MyUtils.sendPost(listJson);
		Assert.assertTrue("Error��favorite the post failed", MyUtils.idInArray(response, "posts", postId));
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}
}
