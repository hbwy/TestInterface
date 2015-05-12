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

public class TestTagCreat {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Tagcreat");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为null, 不通过则为null
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	//验证返回的id不为null
	@Test
	public void testResIdNotNull() {
		String response = this.getReqJson0Response();
		int id = JSONObject.fromObject(response).getJSONObject("responseData").getInt("id");
		Assert.assertTrue("Error:The id is eror", id >= 0);
	}

	/**
	 * 验证添加是否成功 添加--查询
	 */
	@Test
	public void testFind() {
		//添加tag
		String response = getReqJson0Response();
		int tagId = JSONObject.fromObject(response).getJSONObject("responseData").getInt("id");
		//获取查询tag的指令
		String tagInfoCommand = ((List<String>) reqData.get("Taginfo")).get(0);
		String commandInfo = JSONObject.fromObject(tagInfoCommand).getJSONObject("commandInfo").toString();
		tagInfoCommand = tagInfoCommand.replace(commandInfo, "{\"id\":" + tagId + "}");
		//发送查询tagInfo指令
		String response1 = MyUtils.sendBackPost(tagInfoCommand);
		String tagName = JSONObject.fromObject(response1).getJSONObject("responseData").getString("name");
		Assert.assertNotNull("Error:Failed to create the tag", tagName);
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
