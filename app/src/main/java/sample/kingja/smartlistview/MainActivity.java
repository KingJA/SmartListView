package sample.kingja.smartlistview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.kingja.smartlistview.SmartListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SmartListView slv = findViewById(R.id.slv);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("item" + i);
        }
        slv.setAdapter(new SmartAdapter(this, list));
        slv.setOnRefreshListener(new SmartListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slv.refreshComplete();
                    }
                }, 1500);
            }
        });

    }
}
