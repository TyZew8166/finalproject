package tw.edu.pu.s1091802.googlemap;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class search extends AppCompatActivity
{
    private TextView txtResult;
    private Spinner spnPrefer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtResult = (TextView) findViewById(R.id.txtResult);
        spnPrefer = (Spinner) findViewById(R.id.spnPrefer);

        // 建立 ArrayAdapter
        ArrayAdapter<CharSequence> adaptervenue = ArrayAdapter.createFromResource(this , R.array.venue , android.R.layout.simple_spinner_item);
        // 設定 Spinner 顯示格式
        adaptervenue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 設定 Spinner 的資料來源
        spnPrefer.setAdapter(adaptervenue);
        // 設定 spnPrefer 元件 ItemSelected 事件的 listener 為 spnPreferListener
        spnPrefer.setOnItemSelectedListener(spnPreferListener);
    }

    // 定義 onItemSelected 方法
    private Spinner.OnItemSelectedListener spnPreferListener = new Spinner.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent , View v , int position , long id)
        {
            String sel = parent.getSelectedItem().toString();
            txtResult.setText(sel);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}