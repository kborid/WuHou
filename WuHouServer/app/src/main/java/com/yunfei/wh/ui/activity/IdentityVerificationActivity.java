package com.yunfei.wh.ui.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.util.Base64;
import com.prj.sdk.util.IdCardUtils;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.ThumbnailUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.bean.UserInfo;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.dialog.CustomDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;

/**
 * 实名认证
 *
 * @author LiaoBo
 */
public class IdentityVerificationActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {
    private EditText et_userName, et_idcardcode;
    private Button btn_submit;
    private ImageView iv_front_certificate_pic, iv_back_certificate_pic;
    private CustomDialog mDialog;
    // 正反面标记
    private String mTag;
    public String FPICTURE, BPICTURE;
    private TextView tv_agreement, tv_tip;
    private LinearLayout ll_photo_info;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_identity_verification_act);

        initViews();
        initParams();
        initListeners();

    }

    @Override
    public void initViews() {
        super.initViews();
        tv_center_title.setText(R.string.uc_authentication);
        tv_right_title.setVisibility(View.GONE);

        et_userName = (EditText) findViewById(R.id.et_userName);
        et_idcardcode = (EditText) findViewById(R.id.et_idcardcode);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        iv_front_certificate_pic = (ImageView) findViewById(R.id.iv_front_certificate_pic);
        iv_back_certificate_pic = (ImageView) findViewById(R.id.iv_back_certificate_pic);
        tv_agreement = (TextView) findViewById(R.id.tv_agreement);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        ll_photo_info = (LinearLayout) findViewById(R.id.ll_photo_info);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    @Override
    public void initParams() {
        super.initParams();
        try {
            mDialog = new CustomDialog(this);
            if (SessionContext.isLogin()) {
                UserInfo.UserBasic user = SessionContext.mUser.USERBASIC;
                String level = user.levelstatus;
                if ("02".equals(level)) {
                    tv_tip.setText(R.string.auditing);
                    tv_tip.setVisibility(View.VISIBLE);
                    reviewTip();
                    setUserInfo(user);
                } else if ("03".equals(level)) {
                    tv_tip.setText(R.string.audited);
                    tv_tip.setVisibility(View.VISIBLE);
                    ll_photo_info.setVisibility(View.GONE);
                    // 隐藏名字
                    String realname = user.realname.substring(0, user.realname.length() - 1);
                    String name = user.realname.replace(realname, "**");
                    et_userName.setText(name);
                    // 隐藏身份证年月日
                    String str = user.idcardcode.substring(6, 14);
                    String card = user.idcardcode.replace(str, "********");
                    et_idcardcode.setText(card);

                    et_userName.setEnabled(false);
                    et_idcardcode.setEnabled(false);
                    iv_front_certificate_pic.setEnabled(false);
                    iv_back_certificate_pic.setEnabled(false);
                    btn_submit.setEnabled(false);
                    btn_submit.setBackgroundResource(R.drawable.grey_btn);
                } else {// 未通过认证

                }

                FPICTURE = SharedPreferenceUtil.getInstance().getString("FPICTURE", "", false);
                loadBitmap(iv_front_certificate_pic, FPICTURE);
                BPICTURE = SharedPreferenceUtil.getInstance().getString("BPICTURE", "", false);
                loadBitmap(iv_back_certificate_pic, BPICTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置用户信息
     */
    public void setUserInfo(UserInfo.UserBasic user) {
        et_userName.setText(user.realname);// user.realname.length() < 1 ? "" : user.realname.substring(1, user.realname.length())
        et_idcardcode.setText(user.idcardcode);
        loadBitmap(iv_front_certificate_pic, user.idcardphotofront);
        loadBitmap(iv_back_certificate_pic, user.idcardphotoback);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btn_submit.setOnClickListener(this);
        iv_front_certificate_pic.setOnClickListener(this);
        iv_back_certificate_pic.setOnClickListener(this);
        tv_agreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                loadData();
                break;
            case R.id.iv_front_certificate_pic:
                mTag = "FPICTURE";
                dealImg();
                break;
            case R.id.iv_back_certificate_pic:
                mTag = "BPICTURE";
                dealImg();
                break;
            case R.id.tv_agreement:
                Intent mIntent = new Intent(this, WebViewActivity.class);
                mIntent.putExtra("title", getString(R.string.auth_deal));
                mIntent.putExtra("path", NetURL.IDENTITY_PROTOCOL);
                startActivity(mIntent);
                break;

            default:
                break;
        }

    }

    /**
     * 处理图片
     */
    public void dealImg() {
        if (!SessionContext.isLogin()) {
            sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
            return;
        }
        if (mDialog == null) {
            mDialog = new CustomDialog(this);
        }
        mDialog.setMessage("请选择图片获取方式");
        mDialog.setPositiveButton("图库", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isSDCardEnable()) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(i, AppConst.ACTIVITY_GET_IMAGE);
                } else {
                    CustomToast.show("内存卡不可用，请检测内存卡", Toast.LENGTH_LONG);
                }
                dialog.dismiss();
            }
        });
        mDialog.setNegativeButton("相机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isSDCardEnable()) {
                    File file = new File(Utils.getFolderDir("pic"), mTag + "_temp.jpg");
                    file.deleteOnExit();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    intent.putExtra("android.intent.extra.screenOrientation", false);
                    startActivityForResult(intent, AppConst.ACTIVITY_IMAGE_CAPTURE);
                } else {
                    CustomToast.show("内存卡不可用，请检测内存卡", Toast.LENGTH_LONG);
                }
                dialog.dismiss();
            }
        });

        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        String filePath = null;
        switch (requestCode) {
            case AppConst.ACTIVITY_GET_IMAGE:
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    filePath = ThumbnailUtil.getPicPath(this, imageUri);
                    ContentResolver resolver = getContentResolver();
                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, imageUri);
                        // if (mTag.equals("FPICTURE")) {
                        // mFrontCertificatePath = filePath;
                        // iv_front_certificate_pic.setImageBitmap();
                        // } else {
                        // mBackCertificatePath = filePath;
                        // iv_back_certificate_pic.setImageBitmap(ThumbnailUtil.getImageThumbnail(bm, 240, 320));
                        // }
                        uploadImg(ThumbnailUtil.getImageThumbnail(bm, 480, 800));
                        bm.recycle();
                        bm = null;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomToast.show("获取图片失败", 0);
                }
                break;
            case AppConst.ACTIVITY_IMAGE_CAPTURE:
                filePath = Utils.getFolderDir("pic") + mTag + "_temp.jpg";
                ContentResolver resolver = getContentResolver();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, Uri.fromFile(new File(filePath)));
                    // if (mTag.equals("FPICTURE")) {
                    // mFrontCertificatePath = filePath;
                    // // if(bm.getHeight() >bm.getWidth()){
                    // // bm = ThumbnailUtil.getImageRotation(bm,90);// 旋转90
                    // // }
                    // iv_front_certificate_pic.setImageBitmap(ThumbnailUtil.getImageThumbnail(bm, 240, 320));
                    // } else {
                    // mBackCertificatePath = filePath;
                    // iv_back_certificate_pic.setImageBitmap(ThumbnailUtil.getImageThumbnail(bm, 240, 320));
                    // }
                    uploadImg(ThumbnailUtil.getImageThumbnail(bm, 480, 800));
                    bm.recycle();
                    bm = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            default:
                break;
        }

    }

    /**
     * 上传图片 。图片缩略图最大为480x800的80%精度质量
     */
    private void uploadImg(Bitmap bm) {

        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        try {
            builder.addBody("IMGSTR", Base64.encodeToString(ThumbnailUtil.getImageThumbnailBytes(bm, 80)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.UPLOAD_IMG;
        data.flag = 1;

        if (!isProgressShowing()) {
            showProgressDialog("正在上传图片，请稍候...", true);
        }
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        String name = et_userName.getText().toString().trim();
        String number = et_idcardcode.getText().toString().trim();
        if (StringUtil.isEmpty(name)) {
            CustomToast.show("请输入你的真实姓名", 0);
            return;
        }
        if (StringUtil.isEmpty(number)) {
            CustomToast.show("请输入身份证号码", 0);
            return;
        }
        if (!checkBox.isChecked()) {
            CustomToast.show("请先阅读并同意《实名认证协议》", 0);
            return;
        }
        IdCardUtils idcard = new IdCardUtils();
        try {
            String ti = idcard.IDCardValidate(number);
            if (!"".equals(ti)) {
                CustomToast.show(ti, 0);
                return;
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        if (FPICTURE == null) {
            CustomToast.show("请添加身份证正面照", 0);
            return;
        }
        if (BPICTURE == null) {
            CustomToast.show("请添加身份证背面照", 0);
            return;
        }

        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        builder.addBody("NAME", name);
        builder.addBody("IDCARDCODE", number);
        try {
            builder.addBody("FPICTURE", FPICTURE);
            builder.addBody("BPICTURE", BPICTURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.IDENTITY_VERIFICATION;
        data.flag = 2;

        if (!isProgressShowing()) {
            showProgressDialog("正在提交，请稍候...", true);
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
            JSONObject mJson = JSON.parseObject(response.body.toString());
            String fileName = mJson.getString("fileName");
            if (mTag.equals("FPICTURE")) { // TODO 后期判断是否需要拼接url
                FPICTURE = fileName;
                SharedPreferenceUtil.getInstance().setString("FPICTURE", FPICTURE, false);
                loadBitmap(iv_front_certificate_pic, FPICTURE);
            } else {
                BPICTURE = fileName;
                SharedPreferenceUtil.getInstance().setString("BPICTURE", BPICTURE, false);
                loadBitmap(iv_back_certificate_pic, BPICTURE);
            }
            CustomToast.show("上传成功", 0);
        } else {
            SessionContext.mUser.USERBASIC.levelstatus = "02";// 更改本地信息
            reviewTip();
            CustomToast.show("提交成功", 0);
        }

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

    /**
     * 加载图片
     *
     * @param v
     * @param url
     */
    public void loadBitmap(final ImageView v, String url) {
        if (url != null && url.length() > 0) {
            if (!url.contains("http://")) {// 如果返回数据不是全路径，就拼接
                url = NetURL.API_LINK + url;
            }

            v.setImageResource(R.drawable.ic_logo_placeholder);
            ImageLoader.getInstance().loadBitmap(new ImageCallback() {
                @Override
                public void imageCallback(Bitmap bm, String url, String imageTag) {
                    if (bm != null) {
                        v.setImageBitmap(bm);
                    }
                }

            }, url);
        }
    }

    /**
     * 审核提示
     */
    public void reviewTip() {
        tv_tip.setVisibility(View.VISIBLE);
        CustomDialog dialog = new CustomDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("您的实名认证申请已提交，预计7个工作日内通过认证，请耐心等候！");
        dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DataLoader.getInstance().clear(requestID);
        removeProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
