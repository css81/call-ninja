package com.sschoi.callninja.ui.activity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sschoi.callninja.R;
import com.sschoi.callninja.data.db.BlockLogDBHelper;
import com.sschoi.callninja.data.model.BlockLog;
import com.sschoi.callninja.ui.adapter.BlockLogAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Switch switchFavoritesOnly;
    private RecyclerView rvBlockLogs;
    private BlockLogAdapter adapter;
    private BlockLogDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchFavoritesOnly = findViewById(R.id.switchFavoritesOnly);
        rvBlockLogs = findViewById(R.id.rvBlockLogs);

        dbHelper = new BlockLogDBHelper(this);

        adapter = new BlockLogAdapter(dbHelper.getAllLogs());
        rvBlockLogs.setLayoutManager(new LinearLayoutManager(this));
        rvBlockLogs.setAdapter(adapter);

        switchFavoritesOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: 즐겨찾기만 허용 로직 구현
            refreshBlockLogs();
        });
    }

    private void refreshBlockLogs() {
        List<BlockLog> logs = dbHelper.getAllLogs();
        adapter.update(logs);
    }
}
