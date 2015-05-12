package com.dealmoon.testinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author: WY
 * @data:2015��4��3�� ����11:25:46
 * @description:��ȡ�����ļ���صĹ�����
 */
public class PropertiesReader {

	private static Map<String, Object> appRequestData = new HashMap<String, Object>(); //app����������
	private static Map<String, List<String>> backendRequestData = new HashMap<String, List<String>>(); //��̨��������
	private static Map<String, String> tokens = new HashMap<String, String>();

	public static Map<String, Object> getAppRequestData() {
		return appRequestData;
	}

	public static Map<String, List<String>> getBackendRequestData() {
		return backendRequestData;
	}

	public static Map<String, String> getTokens() {
		return tokens;
	}

	//��ȡapp����������
	static {
		// �ļ��������·���µ�,Ĭ�ϵ�Ŀ¼Ϊ���̸�Ŀ¼,o�ļ���Ϊ�����ļ��洢���ļ���
		String unchanged_json_path = "pro/app_unchanged.properties";
		String changed_json_path = "pro/app_changed.properties";

		String[] strs = appUnchangedJsonReader(unchanged_json_path);
		List<String> command_infos = changedJsonReader(changed_json_path);

		appRequestData.put("token", strs[0]);
		List<String> reqjsons;
		for (int i = 0; i < command_infos.size(); i++) {
			String changed_json = command_infos.get(i);
			String reqjson = changed_json + strs[1];
			JSONObject jo = JSONObject.fromObject("{" + reqjson + "}");
			String interName = jo.getString("command").replaceAll("/", "");

			if (appRequestData.get(interName) == null) {
				reqjsons = new ArrayList<String>();
				reqjsons.add(reqjson);
			} else {
				reqjsons = (List<String>) appRequestData.get(interName);
				reqjsons.add(reqjson);
			}

			appRequestData.put(interName, reqjsons);
		}
	}

	//��ȡ��̨��������
	static {
		// �ļ��������·���µ�,Ĭ�ϵ�Ŀ¼Ϊ���̸�Ŀ¼,o�ļ���Ϊ�����ļ��洢���ļ���
		String backend_unchanged_path = "pro/backend_unchanged.properties";
		String backend_changed_path = "pro/backend_changed.properties";

		String strs = backendUnchangedJsonReader(backend_unchanged_path);
		List<String> command_infos = changedJsonReader(backend_changed_path);

		List<String> reqjsons;
		for (int i = 0; i < command_infos.size(); i++) {
			String changed_json = command_infos.get(i);
			String reqjson = "{" + changed_json + strs + "}";
			JSONObject jo = JSONObject.fromObject(reqjson);
			String interName = jo.getString("command").replaceAll("/", "");

			if (backendRequestData.get(interName) == null) {
				reqjsons = new ArrayList<String>();
				reqjsons.add(reqjson);
			} else {
				reqjsons = (List<String>) backendRequestData.get(interName);
				reqjsons.add(reqjson);
			}

			backendRequestData.put(interName, reqjsons);
		}
	}

	static {
		String token_path = "pro/token.properties";
		tokens = proReader(token_path);
	}

	/**
	 * ��ȡ�����ļ��е�unchangedjson����
	 * 
	 * @param changed_json_path changedjson�ļ�·��
	 * @return �洢command commandInfo�ַ����ļ���
	 */
	public static List<String> changedJsonReader(String changed_json_path) {
		ObjectMapper mapper = new ObjectMapper();

		//changed_jsonת����map,�ṹΪ:Map<String, List<Map<String, Object>>>
		Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> list;
		List<String> command_info = new ArrayList<String>();

		try {
			//��ȡchanged_json,ת����map����
			map = mapper.readValue(new File(changed_json_path),
					new TypeReference<Map<String, List<Map<String, Object>>>>() {
					});
			//����map
			Set<?> entries = map.entrySet();
			for (Iterator<?> iterator = entries.iterator(); iterator.hasNext();) {
				Map.Entry<String, List<Map<String, Object>>> entry = (Map.Entry<String, List<Map<String, Object>>>) iterator
						.next();

				String command = entry.getKey();
				list = entry.getValue();
				//����list����
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> maps = list.get(i);
					String commandInfo = mapper.writeValueAsString(maps);
					//����command,commandInfo json�ṹ
					command_info.add("\"command\":\"" + command + "\",\"commandInfo\":" + commandInfo + ",");
				}

			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return command_info;
	}

	/**
	 * ��ȡ�����ļ��е�unchangedjson����
	 * 
	 * @param app_unchanged_path unchangedjson�ļ�·��
	 * @return ȥ���հ��ַ���unchangedjson�ַ���
	 */
	public static String[] appUnchangedJsonReader(String app_unchanged_path) {

		String unchanged_json = stringReader(app_unchanged_path);
		// ��������ʽ�����ַ���,model��activeTime�����пո�
		String model = MyUtils.matchString(unchanged_json, "(\"model\":\\s*\"(.*)\",\\s*\"smsCenter)", 2);
		String activeTime = MyUtils.matchString(unchanged_json,
				"(\"activeTime\":\\s*\"(.*)\",\\s*\"resourceUpdateTime)", 2);

		String _model = MyUtils.removeBlank(model);
		String _activeTime = MyUtils.removeBlank(activeTime);
		String _unchanged_json = MyUtils.removeBlank(unchanged_json);

		String finalUnchangedJsonData = _unchanged_json.replace(_model, model).replace(_activeTime, activeTime);

		String token = MyUtils.matchString(finalUnchangedJsonData, "(\"token\":\"\\S*\",)\"protocol\"", 1);

		if (token != null && !token.equals("")) {
			finalUnchangedJsonData = finalUnchangedJsonData.replace(token, "");
		}
		// ���ش������json �ַ���
		String[] strings = { token, finalUnchangedJsonData };
		return strings;
	}

	/**
	 * ��ȡ��̨request�в���Ĳ���
	 * 
	 * @param backend_unchanged_path �����ļ�·��
	 * @return �������ַ���
	 */
	public static String backendUnchangedJsonReader(String backend_unchanged_path) {
		String unchanged_json = stringReader(backend_unchanged_path);
		return MyUtils.removeBlank(unchanged_json);
	}

	/**
	 * ��ȡproperties���͵������ļ�
	 * 
	 * @param path �ļ�·��
	 * @return map �洢��ֵ��
	 */
	public static Map<String, String> proReader(String path) {
		Reader reader;
		Map<String, String> user_token = new HashMap<String, String>();
		try {
			reader = new FileReader(path);
			Properties prop = new Properties();
			Set keySet = null;
			prop.load(reader);
			keySet = prop.keySet();
			Iterator it = keySet.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				// �������ļ��еļ�ֵ�Դ浽map��
				user_token.put(key, (String) prop.get(key));
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user_token;
	}

	/**
	 * дproperties�ļ�
	 * @param filename �ļ�ȫ·����
	 * @param map key-value
	 */
	public static void proWriter(String filename, Map<String, String> map) {

		Properties prop = new Properties();// ���Լ��϶���     
		try {
			FileInputStream fis = new FileInputStream(filename);// �����ļ�������     
			prop.load(fis);// �������ļ���װ�ص�Properties������     
			fis.close();// �ر���    
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Entry) it.next();
				prop.setProperty((String) entry.getKey(), (String) entry.getValue());
			}
			FileOutputStream fos = new FileOutputStream(filename);
			prop.store(fos, "Copyright wy");
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param path �ļ�·��
	 * @return �ַ���
	 */
	public static String stringReader(String path) {
		File file = new File(path);
		StringBuffer jsonData = new StringBuffer();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String lines;

			while ((lines = br.readLine()) != null) {
				lines = new String(lines.getBytes(), "UTF-8");
				jsonData.append(lines);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonData.toString();
	}
}
