package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月3日 下午5:44:00
 * @description: TestPostInfo 获取单条晒单信息 不需要登录
 */
public class TestPostInfo {

	private static List<String> reqJsons;
	private static int postId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postinfo");
 
		// 随机获取一个最新的post
		List<Integer> postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size() - 1));
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空, 不通过则为空
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJson0Response();
		JSONObject json_responseData = JSONObject.fromObject(response).getJSONObject("responseData");
		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// 验证post结构 id description images数组 tags数组 store_id author_id comments数组
	@Test
	public void testPostStructure() {
		String response = this.getReqJson0Response();

		JSONObject post = JSONObject.fromObject(response).getJSONObject("responseData").getJSONObject("post");
		int id = post.getInt("id");
		String description = post.getString("description");
		int img_size = post.getJSONArray("images").size();
		int tag_size = post.getJSONArray("tags").size();
		int author_id = post.getJSONObject("author").getInt("id");
		int comment_size = post.getJSONArray("comments").size();

		Assert.assertNotNull("Error:The post id is null", id);
		Assert.assertNotNull("Error:The post description is null", description);
		Assert.assertTrue("Error:The imges array is null", img_size >= 0);
		Assert.assertTrue("Error:The tags array is null", tag_size >= 0);
		Assert.assertNotNull("Error:The author id is null", author_id);
		Assert.assertTrue("Error:The comments array is null", comment_size >= 0);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, postId);
		return MyUtils.sendPost(reqJson0);
	}
}
