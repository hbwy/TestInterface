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
 * @data:2015��4��10�� ����2:06:01
 * @description: PostAddComment ������� ��Ҫ��¼
 */
public class TestPostAddComment {

	private static Map<String, Object> reqData;
	private static List<String> reqJsons;
	private static String token;
	private static int postId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddcomment");
		token = MyUtils.getRandomToken();

		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��
	@Test
	public void testResponseDataNotNull() {
		String response = getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	//��֤���ؽ���а���gold score coment�ṹ
	@Test
	public void testStructure() {
		String response = getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int gold = responseData.getInt("gold");
		int score = responseData.getInt("score");
		JSONObject comment = responseData.getJSONObject("comment");

		Assert.assertTrue("Error:The gold is error", gold >= 0);
		Assert.assertTrue("Error:The score is error", score >= 0);
		Assert.assertFalse("Error:The comment is null", comment.isNullObject());
	}

	// ��֤��ɹ�������˸����� ��ȡɹ�������� �� �������۷��ص�ɹ��id�Ա�
	@Test
	public void testFind() {
		String response = getReqJson0Response();
		int commentId = (int) JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("comment")
				.get("id");
		List<String> reqJsons = (List<String>) reqData.get("postgetcomment");
		String reqJson = "{" + reqJsons.get(0) + "}";
		String _reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(_reqJson);
		Assert.assertTrue("Error:The post does not contain the comment",
				MyUtils.idInArray(_response, "comments", commentId));
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

	// ��֤���дʻ�"ϲ���ļ�q" δ��������
	@Test
	public void testBadWord1() {
		String reqJson1 = "{" + token + reqJsons.get(1) + "}";
		reqJson1 = MyUtils.replaceIdinBackCommand(reqJson1, postId);
		String _response = MyUtils.sendPost(reqJson1);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");

		Assert.assertTrue("Error:Sensitive words handling errors", code == 1020);
	}

	// ��֤���дʻ�"�ض�"  ֱ���������д�
	@Test
	public void testBadWord2() {
		String reqJson2 = "{" + token + reqJsons.get(2) + "}";
		reqJson2 = MyUtils.replaceIdinBackCommand(reqJson2, postId);
		String _response = MyUtils.sendPost(reqJson2);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");

		Assert.assertTrue("Error:Sensitive words handling errors", code == 1020);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}
}
