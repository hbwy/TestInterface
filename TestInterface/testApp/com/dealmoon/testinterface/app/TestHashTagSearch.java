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
 * @data:2015年4月3日 下午5:36:53
 * @description:HashTagSearch 搜索tag 不需要登录
 */
public class TestHashTagSearch {

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData =PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("hashtagsearch");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", responseData.isNullObject());
	}

	// 验证tags数组不为null
	@Test
	public void testTagsNotNull() {
		String response = this.getReqJson0Response();
		int size = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	/**
	 * 验证特殊字符,模糊查找条件为空串 集合中的第二条指令 code=0 可以搜索出所有的推荐的tag
	 */
	@Test
	public void testSearchNull() {
		String reqJson1 = "{" + reqJsons.get(1) + "}";
		String response = MyUtils.sendPost(reqJson1);
		JSONObject json_response = JSONObject.fromObject(response);
		int code = json_response.getJSONObject("result").getInt("code");
		int size = json_response.getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertEquals("Error:The result code is not 0", 0, code);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	/**
	 * 验证特殊字符,模糊查找条件为*号 集合中的第三条指令 code=0 tags数组为空
	 */
	@Test
	public void testSearchAsterisk() {
		String reqJson2 = "{" + reqJsons.get(2) + "}";
		String response = MyUtils.sendPost(reqJson2);
		JSONObject json_response = JSONObject.fromObject(response);
		int code = json_response.getJSONObject("result").getInt("code");
		int size = json_response.getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertEquals("Error:The result code is not 0", 0, code);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	/**
	 * 验证特殊字符,模糊查找条件为"号 集合中的第四条指令 code=0 tags数组为空
	 */
	@Test
	public void testSearchSemicolon() {
		String reqJson3 = "{" + reqJsons.get(3) + "}";
		String response = MyUtils.sendPost(reqJson3);
		JSONObject json_response = JSONObject.fromObject(response);
		int code = json_response.getJSONObject("result").getInt("code");
		int size = json_response.getJSONObject("responseData").getJSONArray("tags").size();

		Assert.assertEquals("Error:The result code is not 0", 0, code);
		Assert.assertTrue("Error:The tags array is null", size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}
}
