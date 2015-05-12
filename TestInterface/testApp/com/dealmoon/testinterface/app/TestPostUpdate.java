package com.dealmoon.testinterface.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��4��7�� ����3:22:05
 * @description: PostUpdate ����ɹ��,��Ҫ�ϴ�ͼƬ,urlΪ
 *               http://api2.test.dealmoon.net/Post,��Ҫ��¼
 */
public class TestPostUpdate {

	private static List<String> reqJsons;
	private static String token;
	private static int postId;
	private static List<Integer> imagelist;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postupdate");
		token = MyUtils.getRandomToken();

		// ��ȡcreate post��ָ��
		List<String> addReqJsons = (List<String>) reqData.get("postcreate");
		String addJson = "{" + token + addReqJsons.get(4) + "}";

		// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
		Map<String, String> textMap = new HashMap<String, String>();
		Map<String, String> fileMap = new HashMap<String, String>();

		textMap.put("requestData", addJson);

		String image_path1 = "image/1.jpg";
		String image_path2 = "image/2.jpg";
		String image_path3 = "image/3.jpg";
		String image_path4 = "image/4.jpg";

		fileMap.put("images", image_path1);
		fileMap.put("images", image_path2);
		fileMap.put("images", image_path3);
		fileMap.put("images", image_path4);
		// ����create post����
		String response1 = MyUtils.postUpload(textMap, fileMap, addJson);
		// ��ȡ�´�����post��id
		postId = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONObject("post").getInt("id");
		JSONArray images = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONObject("post")
				.getJSONArray("images");
		imagelist = new ArrayList<Integer>();
		for (Iterator iterator = images.iterator(); iterator.hasNext();) {
			JSONObject image = (JSONObject) iterator.next();
			imagelist.add(image.getInt("id"));
		}
		System.out.println(postId);
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJsonResponse("{" + token + reqJsons.get(0) + "}");
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤responseData��Ϊ��
	@Test
	public void testResponseDataNotNull() {
		String response = this.getReqJsonResponse("{" + token + reqJsons.get(0) + "}");
		JSONObject responseData = JSONObject.fromObject(response).getJSONObject("responseData");

		Assert.assertFalse("The responseData is null", responseData.isNullObject());
	}

	// ��֤responseData��description,store id
	@Test
	public void testResData() {
		String reqJson = "{" + token + reqJsons.get(0) + "}";
		String response = this.getReqJsonResponse(reqJson);
		System.out.println(response);
		JSONObject commandInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo");
		String req_description = commandInfo.getString("description");
		int req_storeId = commandInfo.getInt("storeId");

		JSONObject res_post = JSONObject.fromObject(response).getJSONObject("responseData");
		String res_description = res_post.getJSONObject("post").getString("description");
		int res_storeId = res_post.getJSONObject("post").getJSONObject("store").getInt("id");

		Assert.assertEquals("Error��The description error", req_description, res_description);
		Assert.assertEquals("Error��The storeId error", req_storeId, res_storeId);
	}

	// ��֤�Ƽ���post���ܸ��� code=1004 1011ϵͳ��æ
	@Ignore
	@Test
	public void testRecommendPost() {
		String reqJson1 = "{" + token + reqJsons.get(1) + "}";
		String response = this.getReqJsonResponse(reqJson1);

		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {

		String reqJson = "{" + reqJsons.get(0) + "}";
		String response = this.getReqJsonResponse(reqJson);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code si not 1004", 1004, code);

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

		reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
		String deletePhotoIds = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").getJSONArray("deletePhotoIds").toString();
		String ids = "";
		for(int i=0;i<imagelist.size();i++){
			if(i<imagelist.size()-1){
				ids = ids + imagelist.get(i).toString()+",";
			}else{
				ids = ids + imagelist.get(i).toString();
			}
			
		}
		reqJson = reqJson.replace("\"deletePhotoIds\":"+deletePhotoIds, "\"deletePhotoIds\":["+ids+"]");
		System.out.println(reqJson);
		textMap.put("requestData", reqJson);
		// �޸�post��ʱ���ͼƬ��url��Ҫ��,ҪôɾͼƬ��Ҫֻ��һ��url

		String image_path1 = "image/1.jpg";

		fileMap.put("images", image_path1);
		// �����޸ĵ�Post�ӿڽṹ
		return MyUtils.postUpload(textMap, fileMap, reqJson);
	}
}
