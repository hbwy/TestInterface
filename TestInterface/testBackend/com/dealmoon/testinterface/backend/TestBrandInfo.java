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

/**
 * @author: WY
 * @data:2015年4月24日 下午3:01:52
 * @description: 查看品牌信息
 */
public class TestBrandInfo {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Brandinfo");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		System.out.println(response);
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

	//验证返回的结构是否完整
	@Test
	public void testStructure() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		int id = responseData.getInt("id");
		String titleEn = responseData.getString("titleEn");
		String logo = responseData.getString("logo");
		int viewNum = responseData.getInt("viewNum");
		int favoriteNum = responseData.getInt("favoriteNum");
		int state = responseData.getInt("state");
		int postNum = responseData.getInt("postNum");

		Assert.assertTrue("Error:The id is null", id >= 0);
		Assert.assertNotNull("Error:The titleEn is null", titleEn);
		Assert.assertNotNull("Error:The logo is null", logo);
		Assert.assertTrue("Error:The viewNum is null", viewNum >= 0);
		Assert.assertTrue("Error:The favoriteNum is null", favoriteNum >= 0);
		Assert.assertTrue("Error:The state is null", state >= 0);
		Assert.assertTrue("Error:The postNum is null", postNum >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = reqJsons.get(0);
		return MyUtils.sendBackPost(reqJson0);
	}

}
