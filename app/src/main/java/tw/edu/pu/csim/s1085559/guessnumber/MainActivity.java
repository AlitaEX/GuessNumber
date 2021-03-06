package tw.edu.pu.csim.s1085559.guessnumber;


import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {



    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView tips;
    private EditText input;
    private Button submit;
    private TextView times;
    private TextView record;
    private int rec = 9999999;
    private int ranNum = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tips = (TextView) findViewById(R.id.tips);
        input = (EditText) findViewById(R.id.input);
        submit = (Button) findViewById(R.id.submit);
        times = (TextView) findViewById(R.id.times);
        record = (TextView) findViewById(R.id.record);

        class Game implements Runnable {

            // 變數區
            int in = 0; // 輸入值
            int min = 1; // 最小值
            int max = 99; // 最大值
            int time = 0; // 猜測次數




            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 初始值
                        in = 0; // 輸入值
                        min = 1; // 最小值
                        max = 99; // 最大值
                        time = 0; // 猜測次數
                        input.setText(""); // 清空輸入

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                tips.setText("提示訊息：請輸入 " + min + "～" + max + " 的數字");
                                times.setText("猜測次數：" + time);
                                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                submit.setEnabled(true); // 啟用按鈕

                            }
                        });

                        // 讀取歷史最佳記錄
                        String getRecord = getSharedPreferences("record", MODE_PRIVATE)
                                .getString("times", "");
                        if (getRecord.equals("")) {
                            record.setText("歷史最佳記錄：無");
                        } else {
                            record.setText("歷史最佳記錄：" + getRecord + " 次");
                            rec = Integer.parseInt(getRecord);
                        }

                        // 隨機生成 1~99 數字作為遊戲目標猜測值
                        ranNum = (int) (Math.random() * 99 + 1);
                        Log.v("ANS", "答案：" + ranNum);

                        // 送出按鈕監聽
                        submit.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (input.getText().toString().matches("")) {

                                            tips.setText("提示訊息：請勿空白！請輸入 " + min + "～" + max + " 的數字");

                                        } else {

                                            // 輸入值載入
                                            in = Integer.parseInt(input.getText().toString());

                                            // 清空輸入
                                            input.setText("");

                                            // 判斷區
                                            if (in <= max && in >= min) { // 輸入值介於最大至最小可能值內
                                                if (in > ranNum) {
                                                    max = in;
                                                    tips.setText("提示訊息：請輸入 " + min + "～" + max + " 的數字");
                                                    time++;
                                                } else if (in < ranNum) {
                                                    min = in;
                                                    tips.setText("提示訊息：請輸入 " + min + "～" + max + " 的數字");
                                                    time++;
                                                } else {
                                                    time++;
                                                    tips.setText("恭喜猜中數字「" + ranNum + "」！您只花了 " + time + " 次就完成了！");
                                                    submit.setEnabled(false);
                                                    // 判斷是否寫入歷史最佳記錄
                                                    if (time < rec) {
                                                        rec = time;
                                                        SharedPreferences editRecord = getSharedPreferences("record", MODE_PRIVATE);
                                                        editRecord.edit()
                                                                .putString("times", String.valueOf(rec))
                                                                .apply();
                                                        record.setText("歷史最佳記錄：" + rec + " 次");
                                                    }
                                                }
                                            } else {
                                                tips.setText("提示訊息：請輸入 " + min + "～" + max + " 的數字，別亂輸入啦！");
                                                time++;
                                            }

                                            // 寫入次數
                                            times.setText("猜測次數：" + time);

                                        }

                                    }
                                }
                        );

                        //


                    }
                }).start();
            }
        }

        Game game = new Game();
        game.run();

        // 下拉重新載入
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                Game game = new Game();
                game.run();
            }
        });

    }

    // 右上選單實作

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /* 作者：萌芽站長 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 按下「刪除記錄」後的動作
        if (id == R.id.record_delete) {
            // 更新畫面顯示記錄
            record.setText("歷史最佳記錄：無");
            // 清空記錄值
            SharedPreferences editRecord = getSharedPreferences("record", MODE_PRIVATE);
            editRecord.edit()
                    .putString("times", "")
                    .apply();
            // 建立提示訊息
            Toast.makeText(this, "記錄已刪除", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.give_up) {
            tips.setText("放棄遊戲！答案是「"+ ranNum +"」");
            submit.setEnabled(false);
            times.setText("猜測次數：0");
        } else if (id == R.id.restart) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else if (id == R.id.exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}