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
 * @data:2015��4��24�� ����5:32:31
 * @description: ��ѯɹ����Ϣ
 */
public class TestPostInfo {

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, List<String>> reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("Postinfo");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊnull, ��ͨ����Ϊnull
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	//��֤���صĽṹ�Ƿ�����  description images[] tags[] store author likeUsers[] comments[]
	@Test
	public void testPost() {
		String response = this.getReqJson0Response();
		JSONObject post = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("post");
		
		String description = post.getString("description");
		int images_size = post.getJSONArray("images").size();
		int tags_size = post.getJSONArray("tags").size();
		JSONObject store = post.getJSONObject("store");
		JSONObject author = post.getJSONObject("author");
		int likeUsers_size = post.getJSONArray("likeUsers").size();
		int comments_size = post.getJSONArray("comments").size();

		Assert.assertNotNull("Error:The description is null", description);
		Assert.assertTrue("Error:The imags array is null", images_size >= 0);
		Assert.assertTrue("Error:The tags array is null", tags_size >= 0);
		Assert.assertFalse("Error:The store is null", store.isNullObject());
		Assert.assertFalse("Error:The author is null", author.isNullObject());
		Assert.assertTrue("Error:The likeUsers array is null", likeUsers_size >= 0);
		Assert.assertTrue("Error:The comments array is null", comments_size >= 0);
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
