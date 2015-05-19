package com.dealmoon.testinterface.app;

import java.util.HashMap;
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
 * @data:2015年4月7日 下午3:20:54
 * @description: PostCreate 发表晒单,需要上传图片,url为
 *               http://api2.test.dealmoon.net/Post,需要登录
 */
public class TestPostCreate {

	private static List<String> reqJsons;
	private static String token;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postcreate");
		token = MyUtils.getRandomToken();
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJsonResponse("{" + token + reqJsons.get(0) + "}");
		System.out.println(response);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空, 不通过则为空
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJsonResponse("{" + token + reqJsons.get(0) + "}");
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// 验证responseData的description,images数组大小,author是否为空,store id
	@Test
	public void testResData() {
		String reqJson = "{" + token + reqJsons.get(0) + "}";
		String response = this.getReqJsonResponse(reqJson);
		JSONObject commandInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo");
		String req_description = commandInfo.getString("description");
		int req_imagesSize = commandInfo.getJSONArray("images").size();
		int req_storeId = (int) commandInfo.get("storeId");

		JSONObject res_post = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("post");
		String res_description = res_post.getString("description");
		int res_imagesSize = res_post.getJSONArray("images").size();
		int res_storeId = (int) res_post.getJSONObject("store").get("id");

		Assert.assertEquals("Error：The description error", req_description, res_description);
		Assert.assertEquals("Error：The images array size error", req_imagesSize, res_imagesSize);
		Assert.assertEquals("Error：The storeId error", req_storeId, res_storeId);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = this.getReqJsonResponse(reqJson0);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
	}

	private String getReqJsonResponse(String reqJson) {

		// textMap用于存文本,fileMap用于存图片
		Map<String, String> textMap = new HashMap<String, String>();
		Map<String, String> fileMap = new HashMap<String, String>();

		textMap.put("requestData", reqJson);

		String image_path1 = "image/1.jpg";

		fileMap.put("images", image_path1);
		return MyUtils.postUpload(textMap, fileMap, reqJson);
	}

}
