package com.soulocean.bento_machine_c.util;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import java.util.HashMap;

public class Sample {
    /**
     * 设置APPID/AK/SK
     *
     */
    public static final String APP_ID = "17543177";
    public static final String API_KEY = "8CGIWOp3FG7efnb8yGhXvejZ";
    public static final String SECRET_KEY = "3t6jvHhGOvQxCgDaSiczsgoCPhIgxzry";
    public static AipFace client= null;
    static {
         client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
    }
    public static String uploadFace(String imagebase,String username) {


        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "LOW");
        options.put("action_type", "REPLACE");

        String image = imagebase;
        String imageType = "BASE64";
        String groupId = "user_group";
        String userId = username;

        // 人脸注册
        JSONObject res = client.addUser(image, imageType, groupId, userId, options);
        try{
            if(res.toString(2).indexOf("SUCCESS")!=-1){
                return "success";
            }
        }catch (Exception ignored){

        }
        return "failed";
    }


}
