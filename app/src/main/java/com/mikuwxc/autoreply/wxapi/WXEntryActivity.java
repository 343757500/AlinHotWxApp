package com.mikuwxc.autoreply.wxapi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.mikuwxc.autoreply.StaticData;
import com.mikuwxc.autoreply.activity.LoginActivity;
import com.mikuwxc.autoreply.activity.RunningActivity;
import com.mikuwxc.autoreply.bean.BindWXRespon;
import com.mikuwxc.autoreply.bean.WXTokenResponse;
import com.mikuwxc.autoreply.bean.WXUSerInfoResponse;
import com.mikuwxc.autoreply.common.MyApp;
import com.mikuwxc.autoreply.utils.HttpUtils;
import com.mikuwxc.autoreply.utils.MStringRequest;
import com.mikuwxc.autoreply.utils.PreferenceUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by xiaxin on 15-1-6.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.api.handleIntent(getIntent(), this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MyApp.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d("wechat>>>", "onReq: " + baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d("wechat>>>", "onResp: " + baseResp.errCode + " transaction:" + baseResp.transaction);
        if (baseResp.errCode == 0) {
            // 2018/5/25 如果本地存有access_token和refresh_token,就不需要再重新获取token,刷新token便可
            if (!PreferenceUtil.getWxAccessToken(WXEntryActivity.this).isEmpty() && !PreferenceUtil.getWxRefreshToken(WXEntryActivity.this).isEmpty()) {
                refreshToken(PreferenceUtil.getWxRefreshToken(WXEntryActivity.this));
            } else {
                getToken(baseResp);
            }
        } else {
            MyApp.manager.getCookieStore().removeAll();
            PreferenceUtil.setLoginStatus(WXEntryActivity.this, false);
            StaticData.isStartTask = false;
            finish();
            startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
        }
    }

    private void getToken(BaseResp baseResp) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + MyApp.WX_APP_ID + "&secret=" + MyApp.WX_APP_SECRET + "&code=" + ((SendAuth.Resp) baseResp).code + "&grant_type=authorization_code";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.v("getToken>>>", "onResponse: " + s);
                WXTokenResponse wxTokenResponse = JSON.parseObject(s, WXTokenResponse.class);
                getUserInfo(wxTokenResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String msg = null;
                if (volleyError.getCause() != null) {
                    msg = volleyError.getCause().toString();
                } else {
                    msg = volleyError.toString();
                }
                Log.e("getToken>>>", "出错了:" + msg);
                Toast.makeText(WXEntryActivity.this, "出错了:" + msg, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void refreshToken(String refresh_token) {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + MyApp.WX_APP_ID + "&grant_type=refresh_token&refresh_token=" + refresh_token;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("refreshToken>>>", "onResponse: " + s);
                WXTokenResponse wxTokenResponse = JSON.parseObject(s, WXTokenResponse.class);
                PreferenceUtil.setWxAccessToken(WXEntryActivity.this, wxTokenResponse.getAccess_token());
                PreferenceUtil.setWxRefreshToken(WXEntryActivity.this, wxTokenResponse.getRefresh_token());
                getUserInfo(wxTokenResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String msg = null;
                if (volleyError.getCause() != null) {
                    msg = volleyError.getCause().toString();
                } else {
                    msg = volleyError.toString();
                }
                Log.e("refreshToken>>>", "刷新token出错了:" + msg);
                Toast.makeText(WXEntryActivity.this, "刷新token出错了:" + msg, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getUserInfo(WXTokenResponse wxTokenResponse) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + wxTokenResponse.getAccess_token() + "&openid=" + wxTokenResponse.getOpenid();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        MStringRequest stringRequest = new MStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(">>>", "onResponse: " + s);
                WXUSerInfoResponse wxuSerInfoResponse = JSON.parseObject(s, WXUSerInfoResponse.class);
                wxuSerInfoResponse.getOpenid();
                PreferenceUtil.setOpenId(WXEntryActivity.this, wxuSerInfoResponse.getOpenid());
                PreferenceUtil.setHeadimgurl(WXEntryActivity.this, wxuSerInfoResponse.getHeadimgurl());
                PreferenceUtil.setNickname(WXEntryActivity.this, wxuSerInfoResponse.getNickname());
                startActivity(new Intent(WXEntryActivity.this, RunningActivity.class));
//                uploadDeviceInfo();
//                getWxNum();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getWxNum() {
        StaticData.isGetWxInfo = true;
        Intent intent = new Intent();
        intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        startActivity(intent);
    }

    private void uploadDeviceInfo() {
        Map<String, String> map = new HashMap<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.put("deviceInfo", MyApp.tm.getDeviceId());
        map.put("deviceName", MyApp.bd.MODEL);
        map.put("lemonName", PreferenceUtil.getNickname(this));
        map.put("wxPic", PreferenceUtil.getHeadimgurl(this));
        map.put("openId", PreferenceUtil.getOpenid(this));
        map.put("wxNum", "song1519818108");
        final String mRequestBody = appendParameter(HttpUtils.ADD_DEVICE_INFO_URL, map);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, HttpUtils.ADD_DEVICE_INFO_URL, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                BindWXRespon bindWXRespon = JSON.parseObject(response.toString(), BindWXRespon.class);
                PreferenceUtil.setCurrentUserId(WXEntryActivity.this, bindWXRespon.getResult().getWxInfo().getUserId());
                PreferenceUtil.setCurrentWxId(WXEntryActivity.this, bindWXRespon.getResult().getWxInfo().getId());
                PreferenceUtil.setCurrentUser(WXEntryActivity.this, bindWXRespon.getResult().getWxInfo().getWxNum());
                Log.d("onResponse", "onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }

            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            mRequestBody, PROTOCOL_CHARSET);
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d("Cookie", "getHeaders: " + MyApp.manager.getCookieStore().getCookies().toString());
                Map<String, String> mHeaders = new HashMap<String, String>(1);
                mHeaders.put("Cookie", MyApp.manager.getCookieStore().getCookies().toString());
                return mHeaders;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private String appendParameter(String url, Map<String, String> params) {
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getQuery();
    }

    private TelephonyManager telephonyManager;


}
