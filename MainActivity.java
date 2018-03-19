package com.example.win7.exlogin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity
{
    private String[] login;
    private File filename;

    private EditText edtID,edtPW,edtShow;
    private Button btnOK,btnReset,btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtID = (EditText)findViewById(R.id.edtID);
        edtPW = (EditText)findViewById(R.id.edtPW);
        btnOK = (Button)findViewById(R.id.btnOK) ;
        btnReset = (Button)findViewById(R.id.btnReset) ;
        btnEnd = (Button)findViewById(R.id.btnEnd) ;

        edtShow = (EditText)findViewById(R.id.edtShow) ;

        btnOK.setOnClickListener(myListener);
        btnReset.setOnClickListener(myListener);
        btnEnd.setOnClickListener(myListener);

        requestStoragePermission(); //  撿查驗證
    }
    //  檢查驗證
    private void requestStoragePermission()
    {
        if(Build.VERSION.SDK_INT >=23)  //  Android 6.0以上
        {
            //  判斷是否取得憑證
            int hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            //  如果未取得執行時授權就執行 requestPermissions 讓使用者授權
            if(hasPermission != PackageManager.PERMISSION_GRANTED)  // 未取得驗證
            {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }
        readFile(); //  已取得憑證 (Android5.1以下 就直接讀取帳密檔案)
    }

    //  requestPermissions 觸發的事件
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[]grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)   //  按允許鈕
            {
                readFile();
            }
            else
            {
                Toast.makeText(this,"未取得權限! ",Toast.LENGTH_LONG).show();
                finish();   //  結束應用程式
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
    //  讀取檔案
    private void readFile()
    {   //  取得密碼檔案實體路徑
        filename = new File(Environment.getExternalStorageDirectory(), "login.txt");  // SD卡根目錄
        try
        {
            FileInputStream fin = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            String line = "", wholedata = "";
            int i = 0;
            while ((line = reader.readLine()) != null)
            {
                wholedata = wholedata + line + "\n";
                i++;
            }
            login = wholedata.split("\n");
            reader.close();
            fin.close();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_LONG) .show();
            e.printStackTrace();
        }
    }

    private Button.OnClickListener myListener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.btnOK:    //登入
                    //  撿查帳號及密碼是否都有輸入
                    if(edtID.getText().toString().equals("")||edtPW.getText().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"帳號及密碼都必須輸入 ! ",Toast.LENGTH_LONG).show();
                        break;
                    }
                    //  flag 記錄輸入的帳號是否存在
                    Boolean flag = false;
                    for(int i=0;i<login.length;i+=2)
                    {
                        if(edtID.getText().toString().equals(login[i])) //  帳號存在
                        {
                            flag=true;
                            if(edtPW.getText().toString().equals(login[i+1]))   //  密碼正確
                            {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(" 登入 ")
                                        .setMessage(" 登入成功 ! \n 歡迎使用本程式 !")
                                        .setPositiveButton(" 確定 ", new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {
                                                //  轉換到應用程式啟始頁面
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext()," 密碼不正確 ! ",Toast.LENGTH_LONG).show();
                                edtPW.setText("");
                                break;
                            }
                        }
                    }
                    //  如果flag的值 是flase 就顯示帳號不正確
                    if(!flag)
                    {
                        Toast.makeText(getApplicationContext()," 帳號不正確 ! ",Toast.LENGTH_LONG).show();
                        edtID.setText("");
                        edtPW.setText("");
                    }
                        break;
                case R.id.btnReset:
                    edtID.setText("");
                    edtPW.setText("");
                    edtShow.setText(login[0]+"\n"+login[1]+"\n"+login[2]);
                    break;
                case R.id.btnEnd:
                    finish();
                    break;

            }
        }
    };
}
