package com.mikuwxc.autoreply.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.common.MyApp;
import com.mikuwxc.autoreply.utils.CryptoUtil;
import com.mikuwxc.autoreply.utils.HttpUtils;
import com.mikuwxc.autoreply.utils.PreferenceUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText userName;
    private EditText uPassWord;
    private ProgressBar progre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_hide);
        Log.d("LoginActivity>>>", "onCreate: ");
        findViewById(R.id.register_tv).setOnClickListener(this);
        findViewById(R.id.login_tv).setOnClickListener(this);
        progre = (ProgressBar) findViewById(R.id.main_progress);
        userName = (EditText) findViewById(R.id.un_tv);
        uPassWord = (EditText) findViewById(R.id.up_tv);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.register_tv:
                progre.setVisibility(View.VISIBLE);
                register();
                break;
            case R.id.login_tv:
                progre.setVisibility(View.VISIBLE);
                login();
                break;
        }
    }

    private void register() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, HttpUtils.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("login", "response -> " + response);
                        progre.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ffff", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("mobile", userName.getText().toString());
                map.put("password", uPassWord.getText().toString());
                return map;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    private void login() {
//        final CookieManager manager = new CookieManager(
//                new PersistentCookieStore(LoginActivity.this),
//                CookiePolicy.ACCEPT_ALL);
//        CookieHandler.setDefault(manager);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, HttpUtils.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("", "response -> " + response);
                        progre.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getJSONObject("result").getString("flag").equals("1")) {
                                PreferenceUtil.setLoginStatus(LoginActivity.this, true);
                                getWechatInfo();
                                //startActivity(new Intent(LoginActivity.this,RunningActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("", "cookie: " + MyApp.manager.getCookieStore().getCookies().toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progre.setVisibility(View.GONE);
                String msg = null;
                if (error.getCause() != null) {
                    msg = error.getCause().getMessage();
                } else {
                    msg = error.getMessage();
                }
                Log.e("ffff", msg, error);
                Toast.makeText(LoginActivity.this, "错误信息:" + msg, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("mobile", userName.getText().toString());
                map.put("password", CryptoUtil.encodePwd(LoginActivity.this, uPassWord.getText().toString()));
                return map;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    private void getWechatInfo() {
        if (MyApp.api.isWXAppInstalled()) {
          /*  if (MyApp.api.isWXAppSupportAPI()) {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk";
                MyApp.api.sendReq(req);
            } else {
                Toast.makeText(this, "不支持微信授权", Toast.LENGTH_LONG).show();
            }*/
        } else {
            Toast.makeText(this, "没安装有微信哦", Toast.LENGTH_LONG).show();
        }
    }
}
