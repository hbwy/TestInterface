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
import com.sun.jersey.core.util.Base64;

/**
 * @author: WY
 * @data:2015��5��4�� ����9:24:01
 * @description:�û�ע��
 */
public class TestUserRegister {

	private static List<String> reqJsons;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userregister");
	}

	@Test
	public void testCodeZero() {
		String reqJson = "{" + reqJsons.get(0) + "}";
		String _password = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").getJSONObject("userInfo")
				.getString("password");
		String password = "";
		try {
			password = new String(Base64.encode(MyUtils.encrypt(_password.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		reqJson = reqJson.replace("\"password\":\"" + _password + "\"", "\"password\":\"" + password + "\"");
		String response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		JSONObject userInfo = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("userInfo");
		
		Assert.assertTrue("Error:The code is error", code == 0);
		Assert.assertFalse("Error:The userInfo is null", userInfo.isNullObject());
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
	}

}
