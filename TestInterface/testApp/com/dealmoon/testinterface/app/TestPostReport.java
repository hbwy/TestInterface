package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��4��10�� ����4:33:40
 * @description: PostReport �ٱ�ɹ�� ��Ҫ��¼
 */
public class TestPostReport {

	private static List<String> reqJsons;
	private static Map<String, String> tokens;
	private static Map<String, Object> reqData;
	private static int postId;
	private static int post_userId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postreport");
		tokens = PropertiesReader.getTokens();
		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
		//��ѯpost��Ϣ
		String response = MyUtils.getPostInfo(postId);
		//��ȡpost��author��Id
		post_userId = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("post")
				.getJSONObject("author").getInt("id");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:result code ��Ϊ 0", 0, code);
	}

	// ��֤ͬһɹ������ͬ�û��ٱ�6�β��ܲ�ѯ��
	// ��ͬ�û��ٱ�ͬ1ɹ��6�Σ�post�����أ��༭���Լ��ɼ�  ��6���û��ٱ�ͬ1ɹ�� ɹ������ �鿴 �ɼ� �����û��鿴 ���ɼ�
	@Test
	public void testNotFind() {
		String reportJson = reqJsons.get(0);
		reportJson = MyUtils.replaceIdinAppCommand(reportJson, postId);
		for (int i = 0; i < 8; i++) {
			String reqJson0 = "{" + MyUtils.getRandomToken(post_userId) + reportJson + "}";
			String response1 = MyUtils.sendPost(reqJson0);
		}
		String response = MyUtils.getPostInfo(postId);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertTrue("Error:Failure to report", responseData.isNullObject());

	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:result code ��Ϊ 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
		tokens = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + MyUtils.getRandomToken(post_userId) + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}

}
