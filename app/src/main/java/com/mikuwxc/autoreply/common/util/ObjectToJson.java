package com.mikuwxc.autoreply.common.util;

import com.google.gson.Gson;
import com.mikuwxc.autoreply.modle.ImMessageBean;
import com.mikuwxc.autoreply.modle.MessageBean;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ObjectToJson {


    /**
     * javabean to json
     *
     * @param messageBean
     * @return
     */
    public static String javabeanToJson(ImMessageBean messageBean) {
        Gson gson = new Gson();
        String json = gson.toJson(messageBean);
        return json;
    }

    /**
     * list to json
     *
     * @param list
     * @return
     */
    public static String listToJson(List<MessageBean> list) {

        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    /**
     * map to json
     *
     * @param map
     * @return
     */
    public static String mapToJson(Map<String, MessageBean> map) {

        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

    public static String parseResult(String result) {
        String text = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("result") == 0) {
                text = "操作成功";
            } else {
                text = jsonObject.getString("errmsg");
            }
        } catch (Exception e) {
            text = "解析结果失败";
        }
        return text;
    }
}
