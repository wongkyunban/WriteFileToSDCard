package com.w.writefiletosdcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    // 输入文件名
    private EditText mEtFileName;
    // 正文内容
    private EditText mEtContent;
    // 显示文件中的内容
    private TextView mTvContent;

    // 记录文件名
    private String fileName;
    // 记录目录路径
    private String fileDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkWriteAndReadPermission();

        initViews();


    }


    private void initViews() {
        mEtFileName = findViewById(R.id.et_file_name);
        mEtContent = findViewById(R.id.et_content);
        mTvContent = findViewById(R.id.tv_content);
        fileDir = Environment.getExternalStorageDirectory() + "/CC_FILE";
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (createDir(fileDir)) {
                    fileName = mEtFileName.getText().toString();
                    File file = createFile(fileName);
                    if (file != null && file.exists()) {
                        String content = mEtContent.getText().toString();
                        write2File(file, content);
                        mEtContent.setText("");

                        showFileContent(fileDir, fileName);
                    }
                }
            }
        });

    }

    // 1、检查是否有读写sdcard的权限
    private void checkWriteAndReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1000);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permission : grantResults) {
            if (permission == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "申请权限失败", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    // 2、创建文件目录
    private boolean createDir(String dir) {
        File fileDir = new File(dir);
        if (fileDir.exists() && fileDir.isDirectory()) {
            return true;
        } else {
            return fileDir.mkdirs();
        }

    }

    // 3、创建文件
    private File createFile(String fileName) {
        File file = new File(fileDir, fileName);
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            try {
                if (file.createNewFile()) {
                    return file;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


    }

    // 4、向文件写入数据
    private void write2File(File file, String data) {

        OutputStream ou = null;
        try {
            ou = new FileOutputStream(file);

            byte[] buffer = data.getBytes();
            ou.write(buffer);
            ou.flush();

            Toast.makeText(this,"写入成功",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ou != null) {
                    ou.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // 5、检查是否写入成功
    private void showFileContent(String dir, String fileName) {
        File file = new File(dir, fileName);
        if (file.exists() && file.isFile()) {
            InputStream in = null;
            try {
                StringBuilder stringBuilder = new StringBuilder();
                in = new FileInputStream(file);
                byte[] buffer = new byte[4 * 1024];
                while ((in.read(buffer)) != -1) {
                    stringBuilder.append(new String(buffer));
                }
                mTvContent.setText(stringBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
