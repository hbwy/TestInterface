package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
 * @data:2015��6��15�� ����10:52:46
 * @description:ϲ��/ȡ��ϲ��Ʒ��
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBrandAddAndDelLike {

	private static List<String> delReqJsons; //ȡ����עƷ�Ƶ���������
	private static List<String> addReqJsons; //��עƷ�Ƶ���������
	private static List<String> listReqJsons; //��עƷ�Ƶ���������

	private static int userId; //�����ȡ���û�id
	private static String token; //userId��Ӧ��token

	private static int brandId; //�����ȡ��Ʒ��id

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();

		delReqJsons = (List<String>) reqData.get("branddellike");
		addReqJsons = (List<String>) reqData.get("brandaddlike");
		listReqJsons = (List<String>) reqData.get("brandlikelist");

		//����õ�һ���û�id token
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
		// �����ȡһ��Ʒ��Id
		List<Integer> brandIds = MyUtils.getBrandList();
		brandId = 1;
		int size = brandIds.size();
		if (size > 0) {
			brandId = brandIds.get(new Random().nextInt(size));
		}
	}

	// ϲ��Ʒ��
	@Test
	public void test1AddLike() {
		// ��ȡϲ��Ʒ��ָ��
		String addJson = "{" + token + addReqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, brandId);
		// ����ϲ��Ʒ�Ƶ�����
		String addResponse = MyUtils.sendPost(addJson);

		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤���ϲ��Ʒ�Ƴɹ�
	@Test
	public void test2Find() {
		String response = getBrandLikeList();
		Assert.assertTrue("Error:Like brand failed", MyUtils.idInArray(response, "brands", brandId));
	}

	// ȡ��ϲ��Ʒ��
	@Test
	public void test3DelLike() {
		// ����ȡ��ϲ����ָ��
		String delJson = "{" + token + delReqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, brandId);
		String delResponse = MyUtils.sendPost(delJson);

		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ȡ��ϲ���Ƿ�ɹ�
	@Test
	public void test4NotFind() {
		String response = getBrandLikeList();
		Assert.assertFalse("Error��The brand still exist", MyUtils.idInArray(response, "brands", brandId));
	}

	// ��ִ֤�й�עƷ�Ʊ����¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void test5AddLikeMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// ��ִ֤��ȡ����עƷ�Ʊ����¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void test6DelLikeMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	private String getBrandLikeList() {
		String reqJson = "{" + listReqJsons.get(0) + "}";
		reqJson = MyUtils.replaceUserid(reqJson, userId);
		return MyUtils.sendPost(reqJson);
	}

	private void mustLogin(String reqJson) {
		reqJson = MyUtils.replaceIdinBackCommand(reqJson, brandId);
		String response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		delReqJsons = null;
		addReqJsons = null;
		token = null;
	}
}
