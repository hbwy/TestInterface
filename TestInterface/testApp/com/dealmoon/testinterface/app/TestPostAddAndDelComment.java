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
 * @data:2015��6��15�� ����3:20:53
 * @description:���/ɾ������ ��Ҫ��¼
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPostAddAndDelComment {

	private static List<String> addReqJsons;
	private static List<String> delReqJsons;
	private static List<String> listReqJsons;

	private static String addResponse;
	private static String token;
	private static int postId;
	private static int commentId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		addReqJsons = (List<String>) reqData.get("postaddcomment");
		delReqJsons = (List<String>) reqData.get("postdelcomment");
		listReqJsons = (List<String>) reqData.get("postgetcomment");

		token = MyUtils.getRandomToken();
		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	//��֤�������result code=0
	@Test
	public void test0AddComment() {
		String reqJson0 = "{" + token + addReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		addResponse = MyUtils.sendPost(reqJson0);

		commentId = (int) JSONObject.fromObject(addResponse).getJSONObject("responseData").getJSONObject("comment")
				.get("id");

		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��
	@Test
	public void test1ResponseDataNotNull() {
		JSONObject responseData = JSONObject.fromObject(addResponse).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	// ��֤���ؽ���а���gold score coment�ṹ
	@Test
	public void test2Structure() {
		JSONObject responseData = JSONObject.fromObject(addResponse).getJSONObject("responseData");
		int gold = responseData.getInt("gold");
		int score = responseData.getInt("score");
		JSONObject comment = responseData.getJSONObject("comment");

		Assert.assertTrue("Error:The gold is error", gold >= 0);
		Assert.assertTrue("Error:The score is error", score >= 0);
		Assert.assertFalse("Error:The comment is null", comment.isNullObject());
	}

	// ��֤��ɹ�������˸����� ��ȡɹ�������� �� �������۷��ص�ɹ��id�Ա�
	@Test
	public void test3Find() {
		String response = find();
		Assert.assertTrue("Error:The post does not contain the comment",
				MyUtils.idInArray(response, "comments", commentId));
	}

	// ��֤ɾ������ result code = 0
	@Test
	public void test4DelComment() {
		String delResponse = delComment(commentId);
		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ɾ�������۲鲻��
	@Test
	public void test5NotFind() {
		String response = find();
		// post��û�и�����
		Assert.assertFalse("Error:The post is still contains the comment",
				MyUtils.idInArray(response, "comments", commentId));
	}

	// ��֤���дʻ�"ϲ���ļ�q" δ��������
	@Test
	public void test6BadWord1() {
		String reqJson1 = "{" + token + addReqJsons.get(1) + "}";
		badWord(reqJson1);
	}

	// ��֤���дʻ�"�ض�"  ֱ���������д�
	@Test
	public void test7BadWord2() {
		String reqJson2 = "{" + token + addReqJsons.get(2) + "}";
		badWord(reqJson2);
	}

	// ��֤������۲��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void test8AddMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// ��֤ɾ�����۲��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void test9DelMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	//ɾ������
	private String delComment(int commentId) {

		String reqJson0 = "{" + token + delReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, commentId);
		//����ɾ��ƽ�۵�����
		return MyUtils.sendPost(reqJson0);
	}

	//��ȡɹ������
	private String find() {
		String reqJson = "{" + listReqJsons.get(0) + "}";
		String _reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		return MyUtils.sendPost(_reqJson);
	}

	//��֤�������д�
	private void badWord(String reqJson) {
		reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertTrue("Error:Sensitive words handling errors", code == 1020);
	}

	private void mustLogin(String reqJson){
		reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String _response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}
	
	@AfterClass
	public static void release() {
		addReqJsons = null;
		delReqJsons = null;
		listReqJsons = null;
		addResponse = null;
		token = null;
	}
}
