package com.example.jejufarmreceiptproject.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jejufarmreceiptproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import Adapter.BasketListViewAdapter;
import Adapter.CactusListViewAdapter;
import BluetoothService.BluetoothService;
import Entity.BasketForm;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;
    //region define
    /////////////////////////////////////////////////
    //          CounterListView Text 구현          //
    /////////////////////////////////////////////////
    private TextView counterText;
    private ListView counterListView;

    private ArrayList<String> counterList;
    private ArrayAdapter<String> CntListVIewAdapter;
    /////////////////////////////////////////////////
    //          CactusListView Text 구현           //
    /////////////////////////////////////////////////
    private TextView cactusText;
    private ListView cactusListView;
    CactusListViewAdapter cactusListViewAdapter;
    /////////////////////////////////////////////////
    //             basketListView 구현             //
    /////////////////////////////////////////////////
    private ListView basketListView;
    BasketListViewAdapter basketListViewAdapter;
    /////////////////////////////////////////////////
    //              Total Text  구현               //
    /////////////////////////////////////////////////
    private TextView totalCountText;
    private TextView totalSumText;

    /////////////////////////////////////////////////
    //           Bluetooth  Reciver 구현           //
    /////////////////////////////////////////////////
    private BroadcastReceiver bluetoothRecvier;

    /////////////////////////////////////////////////
    //              Func Button 구현               //
    /////////////////////////////////////////////////
//    private Button addButton;
//    private Button resetButton;
//    private Button printButton;
//    private Button connectButton;
//    private Button cactusEditButton;
    /////////////////////////////////////////////////
    //endregion

    //region General Func
    public void BasketListViewChagned() {
        int count = 0, sum = 0;
        for (BasketForm item : basketListViewAdapter.GetInstance()) {
            count += item.getCount();
            sum += item.getTotal();
        }
        DecimalFormat df = new DecimalFormat("#,###");
        totalCountText.setText(df.format(count));
        totalSumText.setText(df.format(sum));
    }

    public void toastSend(String text, float textsize, int showtime, int postition, int offsetX, int offsetY) {
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(textsize), 0, text.length(), 0);
        Toast toast = Toast.makeText(getApplicationContext(), biggerText, showtime);
        toast.setGravity(postition, offsetX, offsetY);
        toast.show();
    }
    //endregion

    //region Permission Func
    //////////////////////////////////////////////////////////////////////////////////////////////////.////////////////////////////////
    //                                                        Permission 설정                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////.//////////////////////////
    private void setPermission() {
        int permissionInfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionInfo != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                toastSend("SD Card 쓰기권한 승인", 1.25f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            else
                toastSend("SD Card 쓰기권한 거부", 1.25f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //endregion

    //region Init
    // CounterText와 CounterListView를 한번에 처리해주는 Init
    private void Init_CounterListView() {
        counterList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            counterList.add(Integer.toString(i));

        counterListView = findViewById(R.id.counterListView);
        counterText = findViewById(R.id.counterText);

        CntListVIewAdapter = new ArrayAdapter<>(this, R.layout.control_counterlistview, counterList);
        counterListView.setAdapter(CntListVIewAdapter);
        counterListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        counterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                final String inputText = counterText.getText().toString();
                if (counterText.getText().length() > 2) {
                    if (vo.toString().compareTo("0") != 0)
                        counterText.setText(vo.toString());
                } else {
                    if (((counterText.getText().length() == 0 || counterText.getText().length() == 3) && vo.toString().compareTo("0") != 0) || counterText.getText().length() > 0)
                        counterText.setText(inputText + vo.toString());
                }
            }
        });
    }

    // CactusText와 CactusListView를 한번에 처리해주는 Init
    private void Init_CactusListVIew() {
        cactusListViewAdapter = new CactusListViewAdapter();
        cactusText = findViewById(R.id.cactusText);
        cactusListView = findViewById(R.id.cactusListView);
        cactusListView.setAdapter(cactusListViewAdapter);

        cactusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                cactusText.setText(vo.toString());
            }
        });

        cactusListViewAdapter.append("선인장1", 5000);
        cactusListViewAdapter.append("선인장2", 6000);
        cactusListViewAdapter.append("선인장3", 7000);
        cactusListViewAdapter.append("선인장4", 8000);
        cactusListViewAdapter.append("선인장5", 9000);
        cactusListViewAdapter.append("선인장6", 10000);
        cactusListViewAdapter.append("선인장7", 51000);
        cactusListViewAdapter.append("선인장8", 52000);
        cactusListViewAdapter.append("선인장9", 53000);
        cactusListViewAdapter.append("선인장10", 14000);
        cactusListViewAdapter.append("선인장11", 15000);
        cactusListViewAdapter.append("선인장12", 15000);
        cactusListViewAdapter.append("선인장13", 15000);
        cactusListViewAdapter.append("선인장14", 15000);
        cactusListViewAdapter.append("선인장15", 25000);
        cactusListViewAdapter.append("선인장16", 25000);
        cactusListViewAdapter.append("선인장17", 25000);
        cactusListViewAdapter.append("선인장18", 25000);

        cactusListViewAdapter.notifyDataSetChanged();
    }

    // BasketListView를 한번에 처리해주는 Init
    private void Init_BasketListView() {
        basketListViewAdapter = new BasketListViewAdapter();
        basketListView = findViewById(R.id.basketListView);
        basketListView.setAdapter(basketListViewAdapter);
    }

    private void Init_TextView() {
        totalCountText = findViewById(R.id.totalCountText);
        totalSumText = findViewById(R.id.totalSumText);
    }

    private void InitAll() {
        Init_CounterListView();
        Init_CactusListVIew();
        Init_BasketListView();
        Init_TextView();
    }
    //endregion
    @Override
    protected void onResume(){
        super.onResume();

        if(bluetoothRecvier == null){
            bluetoothRecvier = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String text = intent.getStringExtra("toast_message");
                    if(text.equals("connected")) {
                        setTitle("제주농원 for android 연결 완료");
                    }else if(text.equals("disconnected")){
                        setTitle("제주농원 for android 연결 끊김");
                    }
                }
            };
        }

        registerReceiver(bluetoothRecvier, new IntentFilter("bluetoothService"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setContentView(R.layout.activity_main);
        setPermission();
        mContext = this;
        setTitle("테스트용");
        InitAll();

        startService(new Intent(MainActivity.this, BluetoothService.class));

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(bluetoothRecvier != null){
            unregisterReceiver(bluetoothRecvier);
        }
    }

    //region Button Events
    public void addButton_onClick(View view) {
        if (basketListViewAdapter.getCount() > 20) {
            return;
        }
        if (cactusText.getText().toString().compareTo("품목을 입력하세요") == 0) {
            return;
        }
        if (counterText.getText().toString().compareTo("") == 0) {
            return;
        }

        String title;
        int price, count;
        title = cactusText.getText().toString().split("    ")[0];
        price = Integer.parseInt(cactusText.getText().toString().split("    ")[1]);
        count = Integer.parseInt(counterText.getText().toString());
        basketListViewAdapter.append(title, count, price, (price * count));
        basketListView.setSelection(basketListViewAdapter.getCount() - 1);
        cactusText.setText("품목을 입력하세요");
        counterText.setText("");
        BasketListViewChagned();
    }

    public void resetButton_onClick(View view) {
        basketListViewAdapter.clear();
        BasketListViewChagned();
    }

    public void printButton_onClick(View view) {
        try {
            String send_msg = "";
            for(BasketForm item : basketListViewAdapter.GetInstance()){
                send_msg += (item.getTitle() + " " + item.getCount() + " " + item.getPrice() + "\\");
            }
            BluetoothService.sendData(send_msg +"\r\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connectButton_onClick(View view) {
        stopService(new Intent(MainActivity.this, BluetoothService.class));
        startService(new Intent(MainActivity.this, BluetoothService.class));
    }

    public void cactusEditButton_onClick(View view) {

    }
    //endregion

}