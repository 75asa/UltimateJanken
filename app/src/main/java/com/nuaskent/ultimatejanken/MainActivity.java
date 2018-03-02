package com.nuaskent.ultimatejanken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 起動時にデータをクリアに
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public void onJankenButtonTapped(View view) {
        // アクティビティを開くために、開きたいアクティビティを指定し、Intentクラスを生成
        Intent intent = new Intent(this, ResultActivity.class);
        // どのボタンを押下したのかをIntentにもたせる　→　画像のID
        intent.putExtra("MY_HAND", view.getId());
        startActivity(intent);
    }
}
