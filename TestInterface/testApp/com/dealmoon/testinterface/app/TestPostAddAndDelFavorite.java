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
 * @data:2015��6��15�� ����3:22:04
 * @description: �ղ�/ȡ���ղ�ɹ��  ��Ҫ��¼
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPostAddAndDelFavorite {

	private static List<String> addReqJsons;
	private static List<String> delReqJsons;
	private static List<String> listReqJsons;

	private static String token;
	private static int postId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		addReqJsons = (List<String>) reqData.get("postaddfavorite");
		delReqJsons = (List<String>) reqData.get("postdelfavorite");
		listReqJsons = (List<String>) reqData.get("postgetfavoritelist");

		token = MyUtils.getRandomToken();

		//�����ȡһ�����µ�post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size() - 1));
	}

	// ��֤result code = 0
	@Test
	public void test1AddFavorite() {
		String reqJson0 = "{" + token + addReqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		String addResponse = MyUtils.sendPost(reqJson0);

		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//��֤�ղ�ɹ���ɹ�  ��ѯ���post,�ղ�,�鿴�ղ��б�
	@Test
	public void test2Find() {
		String response = find();
		Assert.assertTrue("Error��favorite the post failed", MyUtils.idInArray(response, "posts", postId));
	}

	//��֤ȡ���ղ� result code = 0 
	@Test
	public void test3DelFavorite() {
		//��ȡɾ���ղص�ָ��
		String delJson = "{" + token + delReqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		//����ȡ���ղص�ָ��
		String delResponse = MyUtils.sendPost(delJson);

		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ȡ���ղ�ɹ���Ƿ�ɹ�
	@Test
	public void test4NotFind() {
		String response = find();
		Assert.assertFalse("Error��The user still exist", MyUtils.idInArray(response, "posts", postId));
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

	private void mustLogin(String resJson){
		
		resJson = MyUtils.replaceIdinBackCommand(resJson, postId);
		String _response = MyUtils.sendPost(resJson);

		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}
	
	private String find(){
		String listJson = "{" + token + listReqJsons.get(0) + "}";
		return MyUtils.sendPost(listJson);
	}
	@AfterClass
	public static void release() {
		addReqJsons = null;
		delReqJsons = null;
		listReqJsons = null;
		token = null;
	}

}
