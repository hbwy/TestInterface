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
 * @data:2015年4月16日 上午11:19:34
 * @description: BrandAddFavorite 关注品牌 需要登录
 */
public class TestBrandAddLike {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static int brandId;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("brandaddlike");
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
		// 随机获取一个品牌Id
		List<Integer> brandIds = MyUtils.getBrandList();
		brandId = 1;
		int size = brandIds.size();
		if (size > 0) {
			brandId = brandIds.get(new Random().nextInt(size));
		}
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证添加喜欢品牌成功
	@Test
	public void testFind() {
		List<String> reqJsons = (List<String>) reqData.get("brandlikelist");
		String reqJson = "{" + reqJsons.get(0) + "}";
		reqJson = MyUtils.replaceUserid(reqJson, userId);
		String response = MyUtils.sendPost(reqJson);
		Assert.assertTrue("Error:Like brand failed", MyUtils.idInArray(response, "brands", brandId));
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);

		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceIdinBackCommand(reqJson0, brandId);
		return MyUtils.sendPost(reqJson0);
	}

}
