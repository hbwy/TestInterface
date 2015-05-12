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
 * @data:2015��4��10�� ����3:45:51
 * @description: PostDelComment ɾ������ ��Ҫ��¼ ֻ�����������۵��˲���
 */
public class TestPostDelComment {

	private static List<String> reqJsons;
	private static String token;
	private static int postId;
	private static Map<String, Object> reqData;
	private static int commentId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdelcomment");
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

	// ��֤ɾ�������۲鲻�� ��� ���� ɾ�� ����
	@Test
	public void testNotFind() {
		//�������һ��post,��ɾ��������
		getReqJson0Response();
		//��ѯ������
		List<String> reqJsons = (List<String>) reqData.get("postgetcomment");
		String reqJson = "{" + reqJsons.get(0) + "}";
		String _reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(_reqJson);
		// ����post��û�и�����
		Assert.assertFalse("Error:The post is still contains the comment",
				MyUtils.idInArray(_response, "comments", commentId));
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
		//�����һ������ ��ɾ��
		List<String> _reqJsons = (List<String>) reqData.get("postaddcomment");
		String addJson = "{" + token + _reqJsons.get(0) + "}";

		addJson = MyUtils.replaceIdinBackCommand(addJson, postId);
		String addResponse = MyUtils.sendPost(addJson);
		//��ȡ����ӵ����۵�id
		commentId = (int) JSONObject.fromObject(addResponse).getJSONObject("responseData").getJSONObject("comment")
				.get("id");
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, commentId);
		//����ɾ��ƽ�۵�����
		return MyUtils.sendPost(reqJson0);
	}
}
