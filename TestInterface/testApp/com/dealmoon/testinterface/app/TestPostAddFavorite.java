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
 * @data:2015��4��7�� ����9:16:51
 * @description: PostAddFavorite ��עɹ��,��Ҫ��¼
 */
public class TestPostAddFavorite {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;
	private static int postId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddfavorite");
		token = MyUtils.getRandomToken();

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

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		String _response = MyUtils.sendPost(reqJson0);
		
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	//��֤�ղ�ɹ���ɹ�  ��ѯ���post,�ղ�,�鿴�ղ��б�
	@Test
	public void testFind() {
		getReqJson0Response();
		List<String> reqJson2 = (List<String>) reqData.get("postgetfavoritelist");
		String listJson = "{" + token + reqJson2.get(0) + "}";
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
