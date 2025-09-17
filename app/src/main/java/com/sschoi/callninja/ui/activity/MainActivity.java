package com.sschoi.callninja.ui.activity;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sschoi.callninja.R;
import com.sschoi.callninja.data.db.BlockLogDBHelper;
import com.sschoi.callninja.data.model.BlockLog;
import com.sschoi.callninja.ui.adapter.BlockLogAdapter;
import com.sschoi.callninja.util.CallPolicy;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBlockLogs;
    private BlockLogAdapter adapter;
    private BlockLogDBHelper dbHelper;
    private RadioGroup radioGroupPolicy;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        // 배지 숫자 초기화
        resetBadgeCount();

        dbHelper = new BlockLogDBHelper(this);
        rvBlockLogs = findViewById(R.id.rvBlockLogs);
        adapter = new BlockLogAdapter(dbHelper.getAllLogs());
        rvBlockLogs.setLayoutManager(new LinearLayoutManager(this));
        rvBlockLogs.setAdapter(adapter);

        radioGroupPolicy = findViewById(R.id.rgReceiveSettings);


        // SharedPreferences 에서 정책 불러오기 (기본값: ALL)
        int policy = prefs.getInt("call_policy", CallPolicy.ALL);
        switch (policy) {
            case CallPolicy.ALL:
                radioGroupPolicy.check(R.id.rbAllNumbers);
                break;
            case CallPolicy.CONTACTS_ONLY:
                radioGroupPolicy.check(R.id.rbContactsOnly);
                break;
            case CallPolicy.FAVORITES_ONLY:
                radioGroupPolicy.check(R.id.rbFavoritesOnly);
                break;
            case CallPolicy.CONTACTS_FAVORITES:
                radioGroupPolicy.check(R.id.rbContactsAndFavorites);
                break;
        }

        // 라디오 버튼 변경 시 정책 저장
        radioGroupPolicy.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedPolicy = CallPolicy.ALL;
            String message = "";

            if (checkedId == R.id.rbAllNumbers) {
                selectedPolicy = CallPolicy.ALL;
                message = "모든 번호 수신";
            } else if (checkedId == R.id.rbContactsOnly) {
                selectedPolicy = CallPolicy.CONTACTS_ONLY;
                message = "연락처만 수신";
            } else if (checkedId == R.id.rbFavoritesOnly) {
                selectedPolicy = CallPolicy.FAVORITES_ONLY;
                message = "즐겨찾기만 수신";
            } else if (checkedId == R.id.rbContactsAndFavorites) {
                selectedPolicy = CallPolicy.CONTACTS_FAVORITES;
                message = "연락처 + 즐겨찾기 수신";
            }

            // SharedPreferences 저장
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            prefs.edit().putInt("call_policy", selectedPolicy).apply();

            // Toast 메시지 표시
            Toast.makeText(MainActivity.this, message + " 정책이 적용되었습니다.", Toast.LENGTH_SHORT).show();

            refreshBlockLogs();
        });



    }

    private void resetBadgeCount() {
        prefs.edit().putInt("badge_count", 0).apply();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
    }

    private void refreshBlockLogs() {
        List<BlockLog> logs = dbHelper.getAllLogs();
        adapter.update(logs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetBadgeCount();
    }
}
