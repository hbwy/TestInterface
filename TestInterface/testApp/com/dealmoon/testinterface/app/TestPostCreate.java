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
 * @data:2015��4��7�� ����3:20:54
 * @description: PostCreate ����ɹ��,��Ҫ�ϴ�ͼƬ,urlΪ
 *               http://api2.test.dealmoon.net/Post,��Ҫ��¼
 */
public class TestPostCreate {

	private static List<String> reqJsons;
	private static String token;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postcreate");
		token = MyUtils.getRandomToken();
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJsonResponse("{" + token + reqJsons.get(0) + "}");
		System.out.println(response);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��, ��ͨ����Ϊ��
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJsonResponse("{" + token + reqJsons.get(0) + "}");
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// ��֤responseData��description,images�����С,author�Ƿ�Ϊ��,store id
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

		Assert.assertEquals("Error��The description error", req_description, res_description);
		Assert.assertEquals("Error��The images array size error", req_imagesSize, res_imagesSize);
		Assert.assertEquals("Error��The storeId error", req_storeId, res_storeId);
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
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

		// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
		Map<String, String> textMap = new HashMap<String, String>();
		Map<String, String> fileMap = new HashMap<String, String>();

		textMap.put("requestData", reqJson);

		String image_path1 = "image/1.jpg";

		fileMap.put("images", image_path1);
		return MyUtils.postUpload(textMap, fileMap, reqJson);
	}

}
