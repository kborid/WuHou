package com.yunfei.wh.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.wheel.OnWheelChangedListener;
import com.prj.sdk.widget.wheel.WheelView;
import com.prj.sdk.widget.wheel.adapters.ArrayWheelAdapter;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 区域选择滚轮列表展示对话框
 *
 * @author LiaoBo
 */
public class AreaWheelDialog extends Dialog implements OnWheelChangedListener, View.OnClickListener {

    /**
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
     */
    private JSONArray mJsonObj;
    /**
     * 省的WheelView控件
     */
    private WheelView mProvince;
    /**
     * 市的WheelView控件
     */
    private WheelView mCity;
    /**
     * 区的WheelView控件
     */
    private WheelView mArea;

    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    private int mProvinceType;

    /**
     * key - 省 value - 市s
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区s
     */
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

    /**
     * 当前省的名称
     */
    private String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    private String mCurrentCityName;
    /**
     * 当前区的名称
     */
    private String mCurrentAreaName = "";

    private TextView tv_cancel, tv_confirm;

    private Context mContext;

    private AreaWheelCallback mAreaWheelCallback;

    //	private Map<String, String>		mPId					= new HashMap<String, String>();
//	private Map<String, String>		mCId					= new HashMap<String, String>();
//	private Map<String, String>		mAId					= new HashMap<String, String>();
    private Handler mHandler;

    public AreaWheelDialog(Context context) {
        this(context, null);
    }

    public AreaWheelDialog(Context context, AreaWheelCallback mAreaWheelCallback) {
        super(context);
        ((BaseActivity) context).showProgressDialog("正在获取，请稍候...", false);
        this.mContext = context;
        this.mAreaWheelCallback = mAreaWheelCallback;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.wheel_view);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0));// 去除窗口透明部分显示的黑色
        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (Utils.mScreenWidth);
        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.BOTTOM);
        this.setCanceledOnTouchOutside(false);// 点击空白区域默认消失
        initViews();
        initParams();
        initListeners();
    }

    /**
     * 初始化ui
     */
    private final void initViews() {
        mProvince = (WheelView) findViewById(R.id.id_province);
        mCity = (WheelView) findViewById(R.id.id_city);
        mArea = (WheelView) findViewById(R.id.id_district);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
    }

    /**
     * 初始化参数
     */
    @SuppressLint("HandlerLeak")
    public final void initParams() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
                mProvince.setVisibleItems(7);
                mCity.setVisibleItems(7);
                mArea.setVisibleItems(7);
                updateCities();
                updateAreas();
                ((BaseActivity) mContext).removeProgressDialog();
            }
        };
        mHandler.post(runnable);

    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            initJsonData();
            initDatas();
            Message msg = mHandler.obtainMessage();
            mHandler.sendMessage(msg);
        }
    };

    public final void initListeners() {
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        // 添加change事件
        mProvince.addChangingListener(this);
        // 添加change事件
        mCity.addChangingListener(this);
        // 添加change事件
        mArea.addChangingListener(this);
    }


    /**
     * 设置点击空白区域是否消失
     *
     * @param bool
     */
    public final void setCanceled(boolean bool) {
        this.setCanceledOnTouchOutside(bool);
    }

    public void show() {
        super.show();
    }

    /**
     * 取消窗口显示
     */
    public void dismiss() {
        super.dismiss();
    }

    public boolean isShowing() {
        return super.isShowing();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mAreaDatasMap.get(mCurrentCityName);

        if (areas == null || areas.length == 0) {
            areas = new String[]{""};
        }
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
        mArea.setCurrentItem(0);
        mCurrentAreaName = areas[0];
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
        mCity.setCurrentItem(0);
        updateAreas();
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas() {
        try {
            mProvinceDatas = new String[mJsonObj.length()];
            for (int i = 0; i < mJsonObj.length(); i++) {
                JSONObject jsonP = mJsonObj.getJSONObject(i);
                String province = jsonP.getString("name");
                mProvinceType = jsonP.getInt("type");
                mProvinceDatas[i] = province;

                JSONArray jsonC = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is not a JSONArray.
                     */
                    jsonC = jsonP.getJSONArray("sub");
                } catch (Exception e1) {
                    continue;
                }

                int cityLength = (mProvinceType == 0) ? 1 : jsonC.length();
                String[] mCitiesDatas = new String[cityLength];
                for (int j = 0; j < cityLength; j++) {
                    JSONObject jsonCity = jsonC.getJSONObject(j);
                    String city = (mProvinceType == 0) ? province : jsonCity.getString("name");
                    mCitiesDatas[j] = city;

                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or is not a JSONArray.
                         */
                        jsonAreas = (mProvinceType == 0) ? jsonC : jsonCity.getJSONArray("sub");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        JSONObject json = jsonAreas.getJSONObject(k);
                        mAreasDatas[k] = json.getString("name");
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }
                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象
     */
    private void initJsonData() {
        try {
//			InputStreamReader inputReader = new InputStreamReader(mContext.getResources().getAssets().open("area.json"));
            InputStreamReader inputReader = new InputStreamReader(mContext.getResources().getAssets().open("shortArea.json"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuffer result = new StringBuffer();
            while ((line = bufReader.readLine()) != null) {
                result.append(line);
            }
            inputReader.close();
            bufReader.close();
            mJsonObj = new JSONArray(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * change事件的处理
     */
    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mProvince) {
            updateCities();
        } else if (wheel == mCity) {
            updateAreas();
        } else if (wheel == mArea) {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_confirm:
                // // TODO: 2016/6/2
                mAreaWheelCallback.onAreaWheelInfo(mCurrentProviceName, mCurrentCityName, mCurrentAreaName);
                dismiss();
                break;

            default:
                break;
        }
    }

    /**
     * 回调地址信息
     *
     * @author LiaoBo
     */
    public interface AreaWheelCallback {
        public void onAreaWheelInfo(String ProviceName, String CityName, String AreaName);
    }

}
