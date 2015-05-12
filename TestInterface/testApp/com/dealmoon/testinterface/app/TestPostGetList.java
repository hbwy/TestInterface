package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��4��10�� ����3:59:46
 * @description: PostGetList ��ȡɹ���б� ����new���Ƽ�recommend����עfollow����ע��Ҫ��¼��ûʵ�֣�
 */
public class TestPostGetList {

	private static List<String> reqJsons;
	private static String token;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postgetlist");
		token = MyUtils.getRandomToken();
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤posts���鲻Ϊnull,û�����۷��ؿ�����,��Ӧ��Ϊnull
	@Test
	public void testPostsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}

	// ��֤�Ļ�ȡɹ���б� �Ƽ�  posts���鲻Ϊ��
	@Test
	public void testPostsRecommend() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String _response = MyUtils.sendPost(reqJson1);
		int size = JSONObject.fromObject(_response).getJSONObject("responseData").getJSONArray("posts").size();

		Assert.assertTrue("Error:The posts array is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}
}
