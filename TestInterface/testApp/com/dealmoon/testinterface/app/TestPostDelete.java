package com.dealmoon.testinterface.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @data:2015��4��3�� ����5:59:23
 * @description:TestPostDelete ɾ��ɹ�� ��Ҫ��¼
 */
public class TestPostDelete {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static Map<String, Object> reqData;
	private static int postId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postdelete");
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ɾ����鲻��
	@Test
	public void testNotFind() {
		String response = this.getReqJson0Response();
		// �����ѯ��post��ָ��
		String response2 = MyUtils.getPostInfo(postId);
		JSONObject json_resData = JSONObject.fromObject(response2).getJSONObject("responseData");
		Assert.assertTrue("Error:Delete is not successful", json_resData.isNullObject());
	}

	// ��֤���Լ���ɹ������ɾ�� code=1004
	@Test
	public void testDelNotOwn() {
		String reqJson2 = "{" + MyUtils.getRandomToken(userId) + reqJsons.get(2) + "}";
		String _response = MyUtils.sendPost(reqJson2);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {

		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String _response = MyUtils.sendPost(reqJson1);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	// �ͷ���Դ
	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		// ��ȡcreate post��ָ��
		List<String> reqJsons = (List<String>) reqData.get("postcreate");
		String addJson = "{" + token + reqJsons.get(0) + "}";

		// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
		Map<String, String> textMap = new HashMap<String, String>();
		Map<String, String> fileMap = new HashMap<String, String>();

		textMap.put("requestData", addJson);

		String image_path1 = "image/1.jpg";

		fileMap.put("images", image_path1);
		// ����create post����
		String response1 = MyUtils.postUpload(textMap, fileMap, addJson);
		// ��ȡ�´�����post��id
		postId = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONObject("post").getInt("id");
		// ����ɾ����post��ָ��
		String delJson = "{" + token + this.reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		// ����ɾ����post������
		return MyUtils.sendPost(delJson);
	}
}
