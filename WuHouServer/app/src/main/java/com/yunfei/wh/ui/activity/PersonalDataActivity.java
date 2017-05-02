package com.yunfei.wh.ui.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.Base64;
import com.prj.sdk.util.DateUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.ThumbnailUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.UserInfo;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.custom.CircleImageView;
import com.yunfei.wh.ui.dialog.AreaWheelDialog;
import com.yunfei.wh.ui.dialog.CustomDialog;
import com.yunfei.wh.ui.dialog.GetPicDialog;

import java.net.ConnectException;
import java.util.Calendar;
import java.util.Date;

/**
 * 个人资料
 *
 * @author LiaoBo
 */
public class PersonalDataActivity extends BaseActivity implements DataCallback, OnCheckedChangeListener, DialogInterface.OnCancelListener, DatePickerDialog.OnDateSetListener, AreaWheelDialog.AreaWheelCallback {
    private String[] sexString; // 用户性别 01：男，02：女
    private String[] marryString;// 婚姻 01：已婚，02：未婚，03：保密
    private TextView tv_birthday, tv_address, tv_marriage, tv_sex;
    private int sex_index = 0;
    private int marry_index = 0;
    private EditText et_nickname;
    private int mYear = 1990, mMonth = 1, mDay = 1;
    private CircleImageView iv_photo;
    private Uri mCameraFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_personal_data);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        sexString = getResources().getStringArray(R.array.sex_array);
        marryString = getResources().getStringArray(R.array.marry_array);
        tv_center_title.setText(R.string.edit_info);
        tv_right_title.setText(R.string.save);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_marriage = (TextView) findViewById(R.id.tv_marriage);
        iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
    }

    @Override
    public void initParams() {
        super.initParams();
        try {
            setHeadPortrait(SessionContext.mUser.USERBASIC.getPhotoUrl());
            et_nickname.setText(StringUtil.doEmpty(SessionContext.mUser.USERBASIC.nickname, SessionContext.mUser.USERBASIC.username));
            String birthday = SessionContext.mUser.USERBASIC.birthday;
            if (StringUtil.notEmpty(birthday)) {
                tv_birthday.setText(DateUtil.getY_M_D(birthday));
            }

            if (SessionContext.mUser.LOCALUSER != null && SessionContext.mUser.LOCALUSER.residence != null) {
                tv_address.setText(StringUtil.doEmpty(SessionContext.mUser.LOCALUSER.residence));
            } else {
                SessionContext.mUser.LOCALUSER = new UserInfo.LocalUser();
            }
            if ("01".equals(SessionContext.mUser.USERBASIC.sex)) {
                sex_index = 0;
                tv_sex.setText(sexString[0]);
            } else if ("02".equals(SessionContext.mUser.USERBASIC.sex)) {
                sex_index = 1;
                tv_sex.setText(sexString[1]);
            }
            if ("01".equals(SessionContext.mUser.USERBASIC.marry)) {
                marry_index = 0;
                tv_marriage.setText(marryString[0]);
            } else if ("02".equals(SessionContext.mUser.USERBASIC.marry)) {
                marry_index = 1;
                tv_marriage.setText(marryString[1]);
            } else if ("03".equals(SessionContext.mUser.USERBASIC.marry)) {
                marry_index = 2;
                tv_marriage.setText(marryString[2]);
            }

            // if (StringUtil.notEmpty(SessionContext.mUser.USERAUTH.mobilenum)) {// 屏蔽手机号中间位数
            // char[] t = SessionContext.mUser.USERAUTH.mobilenum.toCharArray();
            // for (int i = 3; i < t.length - 4; i++) {
            // t[i] = '*';
            // }
            // tv_mobilenum.setText(String.valueOf(t));
            // } else {
            // tv_mobilenum.setText("--");
            // }
            // tv_email.setText(StringUtil.doEmpty(SessionContext.mUser.USERAUTH.email));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        iv_photo.setOnClickListener(this);
        tv_address.setOnClickListener(this);
        tv_marriage.setOnClickListener(this);
        tv_birthday.setOnClickListener(this);
        tv_sex.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_birthday:
                new DatePickerDialog(this, this, mYear, mMonth, mDay).show();
                break;
            case R.id.tv_right_title:
                if (StringUtil.containsEmoji(et_nickname.getText().toString())) {
                    CustomToast.show("昵称不能包含Emoji表情符号", 0);
                    return;
                }
                checkDataAndLoad();
                break;
            case R.id.iv_photo:
                GetPicDialog picDialog = new GetPicDialog(this);
                mCameraFile = picDialog.getPicPathUri();
                picDialog.showDialog();
                break;
            case R.id.tv_address:
                AreaWheelDialog dialog = new AreaWheelDialog(this, this);
                dialog.show();
                break;
            case R.id.tv_sex:
                final CustomDialog sexDialog = new CustomDialog(this);
                sexDialog.setTitle("请设置性别");
                sexDialog.setSingleChoiceItems(getResources().getStringArray(R.array.sex_array), sex_index, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sex_index = which;
                    }
                });
                sexDialog.setCanceledOnTouchOutside(true);
                sexDialog.setNegativeButton(R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sex = getResources().getStringArray(R.array.sex_array)[sex_index];
                        tv_sex.setText(sex);
                        sexDialog.dismiss();
                    }
                });
                sexDialog.show();
                break;
            case R.id.tv_marriage:
                final CustomDialog marryDialog = new CustomDialog(this);
                marryDialog.setTitle("婚姻");
                marryDialog.setSingleChoiceItems(getResources().getStringArray(R.array.marry_array), marry_index, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marry_index = which;
                    }
                });
                marryDialog.setCanceledOnTouchOutside(true);
                marryDialog.setNegativeButton(R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String marry = getResources().getStringArray(R.array.marry_array)[marry_index];
                        tv_marriage.setText(marry);
                        marryDialog.dismiss();
                    }
                });
                marryDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 设置头像
     */
    public void setHeadPortrait(String url) {
        if (!StringUtil.isEmpty(url)) {
            if (url.length() > 0) {
                ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                    @Override
                    public void imageCallback(Bitmap bm, String url, String imageTag) {
                        if (bm != null) {
                            iv_photo.setImageBitmap(bm);
                        }
                    }
                }, url);
            }
        }
    }

    private void checkDataAndLoad() {
        if (StringUtil.isEmpty(et_nickname.getText())) {
            CustomToast.show("昵称不能为空！", 0);
            return;
        }

        if (StringUtil.isEmpty(tv_address.getText())) {
            CustomToast.show("请选择地址！", 0);
            return;
        }

        if (StringUtil.isEmpty(tv_sex.getText())) {
            CustomToast.show("请选择性别！", 0);
            return;
        }

        if (StringUtil.isEmpty(tv_birthday.getText())) {
            CustomToast.show("请选择出生日期！", 0);
            return;
        }

        if (StringUtil.isEmpty(tv_marriage.getText())) {
            CustomToast.show("请设置婚姻状况！", 0);
            return;
        }

        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        try {
            switch (requestCode) {
                case AppConst.ACTIVITY_GET_IMAGE:// 图库
                    mCameraFile = data.getData();
                    if (mCameraFile != null) {
                        tailorImg(mCameraFile);
                    }
                    break;
                case AppConst.ACTIVITY_IMAGE_CAPTURE:// 相机
                    tailorImg(mCameraFile);
                    break;
                case AppConst.ACTIVITY_TAILOR:// 剪切
                    uploadPhoto();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomToast.show(getString(R.string.load_image_error), 0);
        }

    }

    /**
     * 裁剪图片
     *
     * @param imageUri
     */
    public void tailorImg(Uri imageUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 240);
            intent.putExtra("outputY", 240);
            // intent.putExtra("return-data", true);// 返回值中有图片数据
            intent.putExtra("output", mCameraFile);
            startActivityForResult(intent, AppConst.ACTIVITY_TAILOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传头像
     */
    private void uploadPhoto() {
        try {
            RequestBeanBuilder builder = RequestBeanBuilder.create(true);

            builder.addBody("HEADPHOTO", Base64.encodeToString(ThumbnailUtil.getImageThumbnailBytes(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCameraFile), 90)));

            ResponseData data = builder.syncRequest(builder);
            data.path = NetURL.UPDATA_PHOTO;
//            data.path = "http://192.168.1.81/wh_portal/service/CW9022";
            data.flag = 1;

            if (!isProgressShowing()) {
                showProgressDialog(getString(R.string.submitting_photo), true);
            }
            requestID = DataLoader.getInstance().loadData(this, data);
        } catch (Exception e) {
            e.printStackTrace();
            CustomToast.show(getString(R.string.upload_photo_failed), 0);
        }
    }

    /**
     * 修改用户信息
     *
     * @throws
     */
    private void loadData() {

        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        builder.addBody("NICKNAME", et_nickname.getText().toString().trim());
        if (tv_sex.getText() != null && tv_sex.getText().equals(sexString[0])) {
            builder.addBody("SEX", "01");
        } else if (tv_sex.getText() != null && tv_sex.getText().equals(sexString[1])) {
            builder.addBody("SEX", "02");
        }
        builder.addBody("BIRTHDAT", tv_birthday.getText().toString());// DateUtil.str2Date(tv_birthday.getText().toString(), "yyyy-MM-dd").getTime());
        if (tv_marriage.getText() != null && tv_marriage.getText().equals(marryString[0])) {
            builder.addBody("MARRY", "01");
        } else if (tv_marriage.getText() != null && tv_marriage.getText().equals(marryString[1])) {
            builder.addBody("MARRY", "02");
        } else if (tv_marriage.getText() != null && tv_marriage.getText().equals(marryString[2])) {
            builder.addBody("MARRY", "03");
        }
        builder.addBody("RESIDENCE", tv_address.getText().toString().trim());// 所在地

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.UPDATA_USER_INFO;
//        data.path = "http://192.168.1.25:8080/wh_portal/service/CW9013";

        if (!isProgressShowing()) {
            showProgressDialog(getString(R.string.saving), true);
        }
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        removeProgressDialog();
        if (request.flag == 1) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCameraFile);
            String newUrl = response.body.toString();
            String oldUrl = SessionContext.mUser.USERBASIC.headphotourl;
            ImageLoader.getInstance().remove(oldUrl);
            if (newUrl != null && bitmap != null) {
                SessionContext.mUser.USERBASIC.headphotourl = newUrl;
                ImageLoader.getInstance().putDiskBitmap(newUrl, ThumbnailUtil.getRoundImage(bitmap));
            }
            iv_photo.setImageBitmap(ImageLoader.getInstance().getCacheBitmap(newUrl));

            CustomToast.show(getString(R.string.upload_photo_success), 0);
            this.setResult(RESULT_OK, null);
            return;
        }
        CustomToast.show(getString(R.string.save_success), 0);
        SessionContext.mUser.USERBASIC.nickname = et_nickname.getText().toString().trim();
        SessionContext.mUser.USERBASIC.birthday = tv_birthday.getText().toString();
        SessionContext.mUser.LOCALUSER.residence = tv_address.getText().toString().trim();
        if (tv_sex.getText() != null && tv_sex.getText().equals(sexString[0])) {
            SessionContext.mUser.USERBASIC.sex = "01";
        } else if (tv_sex.getText() != null && tv_sex.getText().equals(sexString[1])) {
            SessionContext.mUser.USERBASIC.sex = "02";
        }
        if (tv_marriage.getText() != null && tv_marriage.getText().equals(marryString[0])) {
            SessionContext.mUser.USERBASIC.marry = "01";
        } else if (tv_marriage.getText() != null && tv_marriage.getText().equals(marryString[1])) {
            SessionContext.mUser.USERBASIC.marry = "02";
        } else if (tv_marriage.getText() != null && tv_marriage.getText().equals(marryString[2])) {
            SessionContext.mUser.USERBASIC.marry = "03";
        }

        // 重新缓存用户数据
        SharedPreferenceUtil.getInstance().setString(AppConst.USER_INFO, JSON.toJSONString(SessionContext.mUser), true);
        this.setResult(RESULT_OK, null);
        this.sendBroadcast(new Intent(Const.UPDATE_USERINFO));
        this.finish();
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        removeProgressDialog();

        String message;
        if (e != null && e instanceof ConnectException) {
            message = getString(R.string.dialog_tip_net_error);
        } else {
            message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
        }
        CustomToast.show(message, Toast.LENGTH_LONG);

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DataLoader.getInstance().clear(requestID);
        removeProgressDialog();
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        DateUtil.getCurDateStr();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int n_year = c.get(Calendar.YEAR);
        int n_month = c.get(Calendar.MONTH);
        int n_day = c.get(Calendar.DAY_OF_MONTH);
        if (mYear > n_year) {
            CustomToast.show(getString(R.string.birthday_check), 0);
            return;
        }
        if (mYear == n_year) {
            if (mMonth > n_month) {
                CustomToast.show(getString(R.string.birthday_check), 0);
                return;
            }
            if (mMonth == n_month && mDay > n_day) {
                CustomToast.show(getString(R.string.birthday_check), 0);
                return;
            }
        }
        // 设置文本的内容：
        // 月份+1，因为从0开始
        if (mMonth < 9) {
            if (mDay < 10) {
                tv_birthday.setText(String.valueOf(mYear) + "-" + "0" + (mMonth + 1) + "-" + "0" + mDay);
            } else {
                tv_birthday.setText(String.valueOf(mYear) + "-" + "0" + (mMonth + 1) + "-" + mDay);
            }
        } else {
            if (mDay < 10) {
                tv_birthday.setText(String.valueOf(mYear) + "-" + (mMonth + 1) + "-" + "0" + mDay);
            } else {
                tv_birthday.setText(String.valueOf(mYear) + "-" + (mMonth + 1) + "-" + mDay);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // switch (checkedId) {
        // case R.id.rb_nan :
        // case R.id.rb_nv :
        // if (!SessionContext.mUser.USERBASIC.level.equals("01")) {
        // if (SessionContext.mUser.USERBASIC != null && "01".equals(SessionContext.mUser.USERBASIC.sex)) {
        // rb_nan.setChecked(true);
        // } else if (SessionContext.mUser.USERBASIC != null && "02".equals(SessionContext.mUser.USERBASIC.sex)) {
        // rb_nv.setChecked(true);
        // }
        // }
        // break;
        // }
    }

    @Override
    public void onAreaWheelInfo(String ProviceName, String CityName, String AreaName) {
        StringBuilder sb = new StringBuilder();
        sb.append(ProviceName).append("-");
        if (ProviceName.equals(CityName)) {
            sb.append(AreaName);
        } else if (CityName.equals(AreaName)) {
            sb.append(AreaName);
        } else {
            sb.append(CityName);
            if (AreaName != null && AreaName.length() > 0) {
                sb.append("-").append(AreaName);
            }
        }
        tv_address.setText(sb);
    }

}
