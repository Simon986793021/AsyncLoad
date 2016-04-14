package com.alpha.imooc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {
    private ListView listView;
    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv_listview);
        new newsAsyncTask().execute(URL);
    }

    /**
     * 
     * 将url对应的json格式数据转化为我们所封装的newbean对象
     * 
     * @param string
     * @return
     * @throws JSONException
     */
    private List<NewBean> getJsonData(String url) throws JSONException {
        // TODO Auto-generated method stub
        List<NewBean> list = new ArrayList<>();
        try {
            String jsonString = readsStream(new URL(url).openStream());
            // Log.d("111", jsonString);
            NewBean newBean = null;
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = new JSONArray();
            jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                newBean = new NewBean();
                newBean.picture = jsonObject.getString("picSmall");
                newBean.title = jsonObject.getString("name");
                newBean.content = jsonObject.getString("description");
                list.add(newBean);
            }

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return list;
    }

    private String readsStream(InputStream is)
            throws UnsupportedEncodingException {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return result;

    }

    /**
     * 实现异步访问
     * 
     * @author Simon
     */

    class newsAsyncTask extends AsyncTask<String, Void, List<NewBean>> {

        /*
         * asyncTask 必须重写的方法，在子线程中运行，耗时操作在这里处理
         */
        @Override
        protected List<NewBean> doInBackground(String... params) {
            // TODO Auto-generated method stub
            List<NewBean> list = null;
            try {
                list = getJsonData(params[0]);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return list;
        }

        /*
         * 最后默认执行的方法，会把doInBackground 方法返回的参数传进来处理
         */
        @Override
        protected void onPostExecute(List<NewBean> newsbeanlist) {
            // TODO Auto-generated method stub
            super.onPostExecute(newsbeanlist);
            MyBaseAdapter myBaseAdapter = new MyBaseAdapter(MainActivity.this,
                    newsbeanlist);
            listView.setAdapter(myBaseAdapter);
        }

    }
}
