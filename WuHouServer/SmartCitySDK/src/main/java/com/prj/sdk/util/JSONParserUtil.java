package com.prj.sdk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 把json字符串和Map、List、String、Integer等相互转换
 * 
 * @author LiaoBo
 * 
 */
public class JSONParserUtil {

	/**
	 * 将json解析成Map对象
	 * 
	 * @param jsonStr
	 * @return
	 */
	public Map<String, Object> json2Map(String jsonStr) {

		Map<String, Object> result = null;
		if (null != jsonStr) {
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				result = parseJSONObject(jsonObject);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	private Object parseValue(Object inputObject) throws JSONException {
		Object outputObject = null;

		if (null != inputObject) {

			if (inputObject instanceof JSONArray) {
				outputObject = parseJSONArray((JSONArray) inputObject);
			} else if (inputObject instanceof JSONObject) {
				outputObject = parseJSONObject((JSONObject) inputObject);
			} else if (inputObject instanceof String || inputObject instanceof Boolean || inputObject instanceof Integer) {
				outputObject = inputObject;
			}
		}
		return outputObject;
	}

	private List<Object> parseJSONArray(JSONArray jsonArray) throws JSONException {
		List<Object> valueList = null;
		if (null != jsonArray) {
			valueList = new ArrayList<Object>();

			for (int i = 0; i < jsonArray.length(); i++) {
				Object itemObject = jsonArray.get(i);
				if (null != itemObject) {
					valueList.add(parseValue(itemObject));
				}
			}
		}

		return valueList;
	}

	private Map<String, Object> parseJSONObject(JSONObject jsonObject) throws JSONException {
		Map<String, Object> valueObject = null;
		if (null != jsonObject) {
			valueObject = new HashMap<String, Object>();
			Iterator<String> keyIter = jsonObject.keys();
			while (keyIter.hasNext()) {
				String keyStr = keyIter.next();
				Object itemObject = jsonObject.opt(keyStr);
				if (null != itemObject) {
					valueObject.put(keyStr, parseValue(itemObject));
				}
			}
		}

		return valueObject;
	}

	/**
	 * 将map数据解析出来，并拼接成json字符串
	 * 
	 * @param map
	 * @return
	 */
	public JSONObject map2Json(Map<String, Object> map) throws Exception {
		JSONObject json = null;
		StringBuffer temp = new StringBuffer();
		if (!map.isEmpty()) {
			temp.append("{");
			// 遍历map
			Set set = map.entrySet();
			Iterator i = set.iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = (String) entry.getKey();
				Object value = entry.getValue();
				temp.append("\"" + key + "\":");
				if (value instanceof Map<?, ?>) {
					temp.append("\"").append(map2Json((Map<String, Object>) value)).append("\"").append(",");
				} else if (value instanceof List<?>) {
					temp.append(list2Json((List<Object>) value)).append(",");
				} else {
					temp.append("\"").append(value).append("\"").append(",");
				}
			}
			if (temp.length() > 1) {
				temp = new StringBuffer(temp.substring(0, temp.length() - 1));
			}
			temp.append("}");
			json = new JSONObject(temp.toString());

		}
		return json;
	}

	/**
	 * 将单个list转成json字符串
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public String list2Json(List<Object> list) throws Exception {
		String jsonL = "";
		StringBuffer temp = new StringBuffer();
		temp.append("[");
		for (int i = 0; i < list.size(); i++) {
			Object m = list.get(i);
			if (i == list.size() - 1) {
				temp.append("\"").append(m).append("\"");
			} else {
				temp.append("\"").append(m).append("\"").append(",");
			}
		}
		if (temp.length() > 1) {
			temp = new StringBuffer(temp.substring(0, temp.length()));
		}
		temp.append("]");
		jsonL = temp.toString();
		return jsonL;
	}

	public List<Object> json2List(String jsonData) throws JSONException {
		List<Object> l = new ArrayList<Object>();
		JSONArray arr = new JSONArray(jsonData);
		for (int i = 0; i < arr.length(); i++) {
			l.add(arr.get(i));
		}
		return l;

	}

}