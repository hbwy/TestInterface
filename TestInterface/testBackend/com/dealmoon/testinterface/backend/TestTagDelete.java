package com.dealmoon.testinterface.backend;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

public class TestTagDelete {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Tagdelete");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//验证tag是否成功删除     添加--查询--删除--查询
	@Test
	public void testNotFind() {
		//添加tag
		String reqJson1 = ((List<String>) reqData.get("Tagcreat")).get(0);
		String response1 = MyUtils.sendBackPost(reqJson1);
		//获取tag id
		int tagId = JSONObject.fromObject(response1).getJSONObject("responseData").getInt("id");

		//获取删除tag的指令
		String reqJson2 = this.reqJsons.get(0);
		String commandInfo2 = JSONObject.fromObject(reqJson2).getJSONObject("commandInfo").toString();
		reqJson2 = reqJson2.replace(commandInfo2, "{\"id\":" + tagId + "}");
		//发送删除tag的指令
		MyUtils.sendBackPost(reqJson2);

		//获取查询tag的指令
		String reqJson3 = ((List<String>) reqData.get("Taginfo")).get(0);
		String commandInfo3 = JSONObject.fromObject(reqJson3).getJSONObject("commandInfo").toString();
		reqJson3 = reqJson3.replace(commandInfo3, "{\"id\":" + tagId + "}");
		//发送查询tag指令
		String response3 = MyUtils.sendBackPost(reqJson3);
		JSONObject responseData = JSONObject.fromObject(response3).getJSONObject("responseData");

		Assert.assertTrue("Error:delete failed", responseData.isNullObject());
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = reqJsons.get(0);
		return MyUtils.sendBackPost(reqJson0);
	}

}
