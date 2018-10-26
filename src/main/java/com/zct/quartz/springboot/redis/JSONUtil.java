package com.zct.quartz.springboot.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zct.quartz.springboot.core.util.StringUtil;

/**
 * 
 * @author vic
 * @desc json util 
 */
public class JSONUtil {
	
	private static Gson gson = null; 
	
	static{
		gson  = new Gson();//todo yyyy-MM-dd HH:mm:ss 
	}
	
	public static synchronized Gson newInstance(){
		if(gson == null){
			gson =  new Gson();
		}
		return gson;
	}
	
	/**
	 * 将Map转化为Json
	 * 
	 * @param map
	 * @return String
	 */
	public static  String mapToJson(Map<String, Map> map) {
	 String jsonStr = gson.toJson(map);
	 return jsonStr;
	}
	
	public static String toJson(Object obj){
		String json = gson.toJson(obj).replace("\\\"", "");
		return json;
	}
	
	public static <T> T toBean(String json,Class<T> clz){
		
		return gson.fromJson(json, clz);
	}
	
	public static <T> Map<String, T> toMap(String json,Class<T> clz){
		 Map<String, JsonObject> map = gson.fromJson(json, new TypeToken<Map<String,JsonObject>>(){}.getType());
		 Map<String, T> result = new HashMap<>();
		 for(String key:map.keySet()){
			 result.put(key,gson.fromJson(map.get(key),clz) );
		 }
		 return result;
	}
	
	public static Map<String, Object> toMap(String json){
		 Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String,Object>>(){}.getType());
		 return map;
	}
	
	public static <T> List<T> toList(String json,Class<T> clz){
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();  
		List<T> list  = new ArrayList<>();
		for(final JsonElement elem : array){  
	         list.add(gson.fromJson(elem, clz));
	    }
	    return list;
	}
	
	/** 
	 * 转义正则特殊字符 （$()*+.[]?\^{},|） 
	 *  
	 * @param keyword 
	 * @return 
	 */  
	public static String escapeExprSpecialWord(String keyword) {  
	    if (StringUtil.isEmpty(keyword)) {  
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
	        for (String key : fbsArr) {  
	            if (keyword.contains(key)) {  
	                keyword = keyword.replace(key, "\\" + key);  
	            }  
	        }  
	    }  
	    return keyword;  
	}
	
	public static void main(String[] args) {
	}
	
}
