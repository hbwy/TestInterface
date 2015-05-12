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
 * @data:2015年5月4日 上午9:24:01
 * @description:用户注册
 */
public class TestUserRegister {

	private static List<String> reqJsons;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
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
