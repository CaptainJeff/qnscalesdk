package com.qingniu.qnble.demo.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qingniu.qnble.demo.R;
import com.qingniu.qnble.demo.bean.User;
import com.qingniu.qnble.demo.util.ToastMaker;
import com.qingniu.qnble.demo.util.UserConst;
import com.yolanda.health.qnblesdk.constant.UserGoal;
import com.yolanda.health.qnblesdk.constant.UserShape;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleApi;
import com.yolanda.health.qnblesdk.out.QNBleDevice;
import com.yolanda.health.qnblesdk.out.QNUser;
import com.yolanda.health.qnblesdk.out.QNWiFiConfig;
import com.yolanda.health.qnblesdk.out.QNWspConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WspConfigActivity extends AppCompatActivity {

    @BindView(R.id.RegisterRb)
    RadioButton RegisterRb;
    @BindView(R.id.changeUserInfoRb)
    RadioButton changeUserInfoRb;
    @BindView(R.id.visitorCheckBox)
    CheckBox visitorCheckBox;
    @BindView(R.id.ssidEdit)
    EditText ssidEdit;
    @BindView(R.id.wifiPwdEd)
    EditText wifiPwdEd;
    @BindView(R.id.serverUrlEd)
    EditText serverUrlEd;
    @BindView(R.id.OTAUrlEd)
    EditText OTAUrlEd;
    @BindView(R.id.secretKeyEd)
    EditText secretKeyEd;
    @BindView(R.id.swpConfigBtn)
    Button swpConfigBtn;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.deleteEt)
    EditText deleteEt;
    @BindView(R.id.userIndexEt)
    EditText userIndexEt;
    @BindView(R.id.userSecretEt)
    EditText userSecretEt;
    private User mUser;

    private QNBleDevice qnDevice;
    private QNWspConfig qnWspConfig;
    private QNBleApi mQNBleApi;


    public static Intent getIntent(Context context, User user, QNBleDevice device) {
        return new Intent(context, WspConfigActivity.class)
                .putExtra(UserConst.DEVICE, device)
                .putExtra(UserConst.USER, user);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsp_config);
        ButterKnife.bind(this);
        initIntent();
        initData();
    }

    private void initData() {
        mQNBleApi = QNBleApi.getInstance(this);
        qnWspConfig = new QNWspConfig();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.RegisterRb:
                        qnWspConfig.setRegist(true);
                        qnWspConfig.setChange(false);
                        userIndexEt.setVisibility(View.GONE);
                        userSecretEt.setVisibility(View.VISIBLE);
                        break;
                    case R.id.changeUserInfoRb:
                        qnWspConfig.setChange(true);
                        qnWspConfig.setRegist(false);
                        userIndexEt.setVisibility(View.VISIBLE);
                        userSecretEt.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        visitorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                qnWspConfig.setVisitor(isChecked);
            }
        });
    }



    private void initIntent() {
        mUser = getIntent().getParcelableExtra(UserConst.USER);
        qnDevice = getIntent().getParcelableExtra(UserConst.DEVICE);
    }


    private QNUser createQNUser() {
        UserShape userShape;
        switch (mUser.getChoseShape()) {
            case 0:
                userShape = UserShape.SHAPE_NONE;
                break;
            case 1:
                userShape = UserShape.SHAPE_SLIM;
                break;
            case 2:
                userShape = UserShape.SHAPE_NORMAL;
                break;
            case 3:
                userShape = UserShape.SHAPE_STRONG;
                break;
            case 4:
                userShape = UserShape.SHAPE_PLIM;
                break;
            default:
                userShape = UserShape.SHAPE_NONE;
                break;
        }

        UserGoal userGoal;
        switch (mUser.getChoseGoal()) {
            case 0:
                userGoal = UserGoal.GOAL_NONE;
                break;
            case 1:
                userGoal = UserGoal.GOAL_LOSE_FAT;
                break;
            case 2:
                userGoal = UserGoal.GOAL_STAY_HEALTH;
                break;
            case 3:
                userGoal = UserGoal.GOAL_GAIN_MUSCLE;
                break;
            case 4:
                userGoal = UserGoal.POWER_OFTEN_EXERCISE;
                break;
            case 5:
                userGoal = UserGoal.POWER_LITTLE_EXERCISE;
                break;
            case 6:
                userGoal = UserGoal.POWER_OFTEN_RUN;
                break;
            default:
                userGoal = UserGoal.GOAL_NONE;
                break;
        }
        int userIndex = 0;
        int userSercret = 0;
        if (!TextUtils.isEmpty(userIndexEt.getText().toString())) {
            try {
                userIndex = Integer.parseInt(userIndexEt.getText().toString());
            } catch (Exception e) {
                ToastMaker.show(this, getResources().getString(R.string.user_index_error));
            }

        }
        if (!TextUtils.isEmpty(userSecretEt.getText().toString())) {
            try {
                userSercret = Integer.parseInt(userSecretEt.getText().toString());
            } catch (Exception e) {
                ToastMaker.show(this, getResources().getString(R.string.user_secret_error));
            }
        }

        return mQNBleApi.buildUser(mUser.getUserId(),
                mUser.getHeight(), mUser.getGender(), mUser.getBirthDay(), mUser.getAthleteType(),
                userShape, userGoal, mUser.getClothesWeight(), userIndex, userSercret,

                new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        Log.d("ConnectActivity", "创建用户信息返回:" + msg);
                    }
                });
    }

    @OnClick(R.id.swpConfigBtn)
    public void onViewClicked() {
        //非访客模式
        if (!visitorCheckBox.isChecked()) {


            if (changeUserInfoRb.isChecked()) {   //修改用户
                if (TextUtils.isEmpty(userIndexEt.getText().toString())) {
                    ToastMaker.show(this, getResources().getString(R.string.input_user_index));
                    return;
                }
                if (TextUtils.isEmpty(userSecretEt.getText().toString())) {
                    ToastMaker.show(this, getResources().getString(R.string.input_user_secret));
                    return;
                }

            } else if (RegisterRb.isChecked()) {    //注册用户
                if (TextUtils.isEmpty(userSecretEt.getText().toString())) {
                    ToastMaker.show(this, getResources().getString(R.string.input_user_secret));
                    return;
                }
            }
            //删除用户
            if (!TextUtils.isEmpty(deleteEt.getText().toString())) {
                String[] delete = deleteEt.getText().toString().split(" ");
                List<Integer> deletList = new ArrayList<>();
                for (int i = 0; i < delete.length; i++) {
                    try {
                        int index = Integer.parseInt(delete[i]);
                        deletList.add(index);
                    } catch (Exception e) {
                        ToastMaker.show(this, getResources().getString(R.string.user_index_error));
                    }

                }
                qnWspConfig.setDeleteUsers(deletList);
            }

        }
        //配网
        if (!TextUtils.isEmpty(ssidEdit.getText().toString())) {
            if (TextUtils.isEmpty(serverUrlEd.getText().toString())) {
                ToastMaker.show(this, getResources().getString(R.string.wifi_config_request_server));
                return;
            }
            if (TextUtils.isEmpty(OTAUrlEd.getText().toString())) {
                ToastMaker.show(this, getResources().getString(R.string.wifi_config_request_ota));
                return;
            }
            if (TextUtils.isEmpty(secretKeyEd.getText().toString())) {
                ToastMaker.show(this, getResources().getString(R.string.wifi_config_request_secret));
                return;
            }
            QNWiFiConfig qnWiFiConfig = new QNWiFiConfig();
            qnWiFiConfig.setSsid(ssidEdit.getText().toString());
            qnWiFiConfig.setPwd(wifiPwdEd.getText().toString());
            qnWspConfig.setWifiConfig(qnWiFiConfig);
            qnWspConfig.setDataUrl(serverUrlEd.getText().toString());
            qnWspConfig.setOtaUrl(OTAUrlEd.getText().toString());
            qnWspConfig.setEncryption(secretKeyEd.getText().toString());
        }

        qnWspConfig.setCurUser(createQNUser());

        startActivity(WspScaleActivity.getCallIntent(this, qnDevice, qnWspConfig));
    }
}
