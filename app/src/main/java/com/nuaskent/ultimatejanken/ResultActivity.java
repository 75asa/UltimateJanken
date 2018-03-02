package com.nuaskent.ultimatejanken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    // じゃんけんで用意した画像に対応した変数に値をセット
    final int JANKEN_GU = 0;
    final int JANKEN_CHOKI = 1;
    final int JANKEN_PA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Userの手を初期化
        int myHand = 0;
        // インテントを取得
        Intent intent = getIntent();
        // Stringに取り出したい変数のKeyを
        // 第二引数にはKeyが取得できなかったときのデフォルト値を
        int id = intent.getIntExtra("MY_HAND", 0);

        // グー・チョキ・パー各々のImageViewを取得する
        ImageView myHandImageView = (ImageView) findViewById(R.id.my_hand_image);
        // Switchでジャンケンの手を分岐
        // ImageViewに画像を設定するにはsetImageResourceを使用、引数に画像のリソースID
        switch (id) {
            case R.id.gu:
                myHandImageView.setImageResource(R.drawable.gu);
                myHand = JANKEN_GU;
                break;
            case R.id.choki:
                myHandImageView.setImageResource(R.drawable.choki);
                myHand = JANKEN_CHOKI;
                break;
            case R.id.pa:
                myHandImageView.setImageResource(R.drawable.pa);
                myHand = JANKEN_PA;
                break;
            default:
                myHand = JANKEN_GU;
                break;
        }

        // コンピュータの手を決める
//        int comHand = (int) (Math.random() * 3);
        int comHand = getHand();

        ImageView comHandImageView = (ImageView) findViewById(R.id.com_hand_image);
        switch (comHand) {
            case JANKEN_GU:
                comHandImageView.setImageResource(R.drawable.com_gu);
                break;
            case JANKEN_CHOKI:
                comHandImageView.setImageResource(R.drawable.com_choki);
                break;
            case JANKEN_PA:
                comHandImageView.setImageResource(R.drawable.com_pa);
                break;
        }

        // 勝利判定
        TextView resultLabel = (TextView) findViewById(R.id.result_label);
        int gameResult = (comHand - myHand + 3) % 3;

        switch (gameResult) {
            case 0:
                // あいこ
                resultLabel.setText(R.string.result_draw);
                break;
            case 1:
                // 勝利
                resultLabel.setText(R.string.result_win);
                break;
            case 2:
                // LOSE
                resultLabel.setText(R.string.result_lose);
                break;
        }

        // ジャンケンの結果を保存
        saveData(myHand, comHand, gameResult);
    }

    // lets back 2 previous capture
    public void onBackButtonTapped(View view) {
        finish();
    }

    // 共有プリファレンスを使用し、簡易的なデータ保存メソッド
    public void saveData(int myHand, int comHand, int gameResult) {
        // 共有プリファレンスのインスタンス取得
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        // プリファレンスの編集インスタンス取得
        SharedPreferences.Editor editor = pref.edit();

        // 勝負回数
        int gameCount = pref.getInt("GAME_COUNT", 0);
        // 連勝回数
        int winnnigStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0);
        // コンピュータの前回の手
        int lastComHand = pref.getInt("LAST_COM_HAND", 0);
        // 前回の勝敗
        int lastGameResult = pref.getInt("LAST_GAME_RESULT", -1);

        editor.putInt("GAME_COUNT", gameCount + 1);

        if (lastGameResult == 2 && gameResult == 2) {
            // コンピュータが勝利した場合
            editor.putInt("WINNING_STREAK_COUNT", winnnigStreakCount + 1);
        } else {
            editor.putInt("WINNING_STREAK_COUNT", 0);
        }
        editor.putInt("LAST_MY_HAND", myHand);
        editor.putInt("LAST_COM_HAND", comHand);
        editor.putInt("BEFORE_LAST_COM_HAND", lastComHand);
        editor.putInt("GAME_RESULT", gameResult);

        // 変更を保存
        editor.commit();
    }

    // 心理学の一説に基づいたじゃんけん必勝ロジックの実装
    private int getHand() {
        int hand = (int) (Math.random() * 3);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        // 勝負回数
        int gameCount = pref.getInt("GAME_COUNT", 0);
        // 連勝回数
        int winnnigStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0);
        // Userの前回の手
        int lastMyHand = pref.getInt("LAST_MY_HAND", 0);
        // コンピュータの前回の手
        int lastComHand = pref.getInt("LAST_COM_HAND", 0);
        // コンピュータの前前回の手
        int beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND", 0);
        // 前回の勝敗
        int gameResult = pref.getInt("GAME_RESULT", -1);

        if (gameCount == 1) {
            if (gameResult == 2) {
                // 前回の勝負が一回目で、コンピュータが勝利した場合
                // コンピュータは次に出す手を変更

                while (lastComHand == hand) {
                    hand = (int) (Math.random() * 3);
                }
            } else if (gameResult == 1) {
                // 前回の勝負が一回目で、コンピュータが敗北した場合
                // 相手の出した手に勝つ手をだす
                hand = (lastMyHand -1 + 3) % 3;
            }
        } else if (winnnigStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                // 同じ手で連勝した場合は手を変更
                while (lastComHand == hand) {
                    hand = (int) (Math.random() * 3);
                }
            }
        }

        return hand;

    }
}
