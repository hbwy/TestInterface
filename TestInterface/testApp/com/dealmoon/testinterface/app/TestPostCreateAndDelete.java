package com.dealmoon.testinterface.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年6月15日 下午3:23:42
 * @description: 创建/删除晒单 需要登录
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPostCreateAndDelete {

	private static List<String> addReqJsons;
	private static List<String> delReqJsons;

	private static String addResponse;
	private static int postId;

	private static int userId;
	private static String token;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		addReqJsons = (List<String>) reqData.get("postcreate");
		delReqJsons = (List<String>) reqData.get("postdelete");

		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
	}

	// 验证添加晒单 result code = 0
	@Test
	public void test1PostCreate() {
		addResponse = getReqJsonResponse("{" + token + addReqJsons.get(0) + "}");
		postId = JSONObject.fromObject(addResponse).getJSONObject("responseData").getJSONObject("post").getInt("id");
		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证responseData不为空, 不通过则为空
	@Test
	public void test2ResponseDataNotNull() {
		JSONObject json_responseData = JSONObject.fromObject(addResponse).getJSONObject("responseData");

		Assert.assertFalse("Error:The responseData is null", json_responseData.isNullObject());
	}

	// 验证responseData的description,images数组大小,author是否为空,store id
	@Test
	public void test3ResData() {

		String reqJson = "{" + token + addReqJsons.get(0) + "}";
		JSONObject commandInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo");
		String req_description = commandInfo.getString("description");
		int req_imagesSize = commandInfo.getJSONArray("images").size();

		JSONObject res_post = JSONObject.fromObject(addResponse).getJSONObject("responseData").getJSONObject("post");
		String res_description = res_post.getString("description");
		int res_imagesSize = res_post.getJSONArray("images").size();

		Assert.assertEquals("Error：The description error", req_description, res_description);
		Assert.assertEquals("Error：The images array size error", req_imagesSize, res_imagesSize);
	}

	// 验证删除晒单
	@Test
	public void test4PostDelete() {
		// 构造删除该post的指令
		String delJson = "{" + token + delReqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, postId);
		// 发送删除该post的请求
		String delResponse = MyUtils.sendPost(delJson);

		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证删除后查不到
	@Test
	public void test5NotFind() {
		// 构造查询该post的指令
		String response2 = MyUtils.getPostInfo(postId);
		JSONObject json_resData = JSONObject.fromObject(response2).getJSONObject("responseData");
		Assert.assertTrue("Error:Delete is not successful", json_resData.isNullObject());
	}

	// 验证非自己的晒单不能删除 code=1004
	@Test
	public void test6DelNotOwn() {
		String reqJson2 = "{" + MyUtils.getRandomToken(userId) + delReqJsons.get(2) + "}";
		String _response = MyUtils.sendPost(reqJson2);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1011", 1011, code);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test7AddMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test8DelMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	private void mustLogin(String reqJson) {
		String _response = getReqJsonResponse(reqJson);
		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
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

	@AfterClass
	public static void release() {
		token = null;
		addReqJsons = null;
		delReqJsons = null;
		addResponse = null;
	}

}
