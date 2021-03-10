package com.gmrz.cloud.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.gmrz.cloud.R;
import com.gmrz.fido2.Fido2DirectManager;
import com.gmrz.fido2.Fido2DirectReInfo;
import com.gmrz.fido2.Fido2DirectStatus;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String USERNAME = "user11111";
    private static final String RPID = "www.nationauth.cn";

    private Button btn_fido2reg;
    private Button btn_fido2Auth;
    private Button btn_fido2Dereg;

    // PinWheelDialog pinWheelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_fido2reg = findViewById(R.id.btn_fido2reg);
        btn_fido2Auth = findViewById(R.id.btn_fido2auth);
        btn_fido2Dereg = findViewById(R.id.btn_fido2dereg);

        // tv_title = findViewById(R.id.tv_title);

        // pinWheelDialog = new PinWheelDialog(MainActivity.this);

        init();

        btn_fido2reg.setOnClickListener(v -> {
            // pinWheelDialog.show();
            new Thread(() -> {
                final Fido2DirectReInfo uacReInfo = Fido2DirectManager.getInstance().register(MainActivity.this, USERNAME, genTrans());
                runOnUiThread(() -> {
                    // pinWheelDialog.dismiss();
                    if (uacReInfo.status == Fido2DirectStatus.SUCCESS) {
                        // token = uacReInfo.token;
                    }
                    Toast.makeText(MainActivity.this, uacReInfo.status.toString(), Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        btn_fido2Auth.setOnClickListener(v -> {
            // pinWheelDialog.show();
            new Thread(() -> {
                final Fido2DirectReInfo uacReInfo = Fido2DirectManager.getInstance().auth(MainActivity.this, USERNAME, genTrans());
                runOnUiThread(() -> {
                    // pinWheelDialog.dismiss();
                    if (uacReInfo.status == Fido2DirectStatus.SUCCESS) {
                        // token = uacReInfo.token;
                    }
                    Toast.makeText(MainActivity.this, uacReInfo.status.toString(), Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        btn_fido2Dereg.setOnClickListener(v -> {
            // pinWheelDialog.show();
            new Thread(() -> {
                final Fido2DirectReInfo uacReInfo = Fido2DirectManager.getInstance().dereg(MainActivity.this, USERNAME, genTrans(), null);
                runOnUiThread(() -> {
                    // pinWheelDialog.dismiss();
                    if (uacReInfo.status == Fido2DirectStatus.SUCCESS) {
                        //token = uacReInfo.token;
                    }
                    Toast.makeText(MainActivity.this, uacReInfo.status.toString(), Toast.LENGTH_SHORT).show();
                });
            }).start();
        });


    }

    private void init() {
        new Thread(() -> {
            //初始化

            Fido2DirectReInfo fido2DirectReInfo = Fido2DirectManager.getInstance().init(MainActivity.this);
            if (fido2DirectReInfo.status == Fido2DirectStatus.SUCCESS) {
                Fido2DirectManager.getInstance().initAppInfo(null, RPID);
                // fido2Support = true;
            }
        }).start();

    }

    private String transNum;

    private String genTrans() {
        transNum = UUID.randomUUID().toString();
        return transNum;
    }


}
