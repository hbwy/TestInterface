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
 * @data:2015��4��23�� ����10:13:59
 * @description:��ȡ�ҵ���Ϣ�б� ��Ҫ��¼
 */
public class TestMessageGetList {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messagegetlist");
		token = MyUtils.getRandomToken();
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��, ��ͨ����Ϊ��
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// ��֤type=followd followNum,like,messages���鲻Ϊnull
	@Test
	public void testTypeFollowd() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int fanNum = responseData.getInt("fanNum");
		int likeNum = responseData.getInt("likeNum");
		int size = responseData.getJSONArray("messages").size();

		Assert.assertTrue("Error:The followdNum is null", fanNum >= 0);
		Assert.assertTrue("Error:The likedNum is null", likeNum >= 0);
		Assert.assertTrue("Error:The messages array is null", size >= 0);
	}

	// ��֤type=like followNum,like,messages���鲻Ϊnull
	@Test
	public void testTypeLike() {
		String reqJson1 = "{" + token + reqJsons.get(1) + "}";
		String response = MyUtils.sendPost(reqJson1);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int fanNum = responseData.getInt("fanNum");
		int likeNum = responseData.getInt("likeNum");
		int size = responseData.getJSONArray("messages").size();

		Assert.assertTrue("Error:The followdNum is null", fanNum >= 0);
		Assert.assertTrue("Error:The likedNum is null", likeNum >= 0);
		Assert.assertTrue("Error:The messages array is null", size >= 0);
	}

	// ��֤type=null Ϊnull������£���ȡ��Ϣ��ҳ�б� ,followNum,like,messages���鲻Ϊnull
	@Test
	public void testTypeNull() {
		String reqJson2 = "{" + token + reqJsons.get(2) + "}";
		String response = MyUtils.sendPost(reqJson2);
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int fanNum = responseData.getInt("fanNum");
		int likeNum = responseData.getInt("likeNum");
		int size = responseData.getJSONArray("messages").size();

		Assert.assertTrue("Error:The followdNum is null", fanNum >= 0);
		Assert.assertTrue("Error:The likedNum is null", likeNum >= 0);
		Assert.assertTrue("Error:The messages array is null", size >= 0);
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
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}

}
