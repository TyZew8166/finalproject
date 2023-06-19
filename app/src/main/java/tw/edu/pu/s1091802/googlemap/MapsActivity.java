package tw.edu.pu.s1091802.googlemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import tw.edu.pu.s1091802.googlemap.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , LocationListener
{
    private GoogleMap mMap;
    private LocationManager locMgr;
    float zoom;
    String bestProv;

    @Override
    public void onLocationChanged(Location location)
    {
        LatLng Point = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);  //顯示定位圖示
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button nextPageBtn = (Button) findViewById(R.id.button);  //跳轉頁面
        nextPageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            Intent intent = new Intent();
            intent.setClass(MapsActivity.this, search.class);  //從主畫面跳到搜尋畫面
            startActivity(intent);
            }
        }
        );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true);  //取得最佳定位方式

        //如果GPS或網路定位開啟，更新位置
        if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            //確認 ACCESS_FINE_LOCATION 權限是否授權
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
            }
        }
        else
        {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        //確認 ACCESS_FINE_LOCATION 權限是否授權
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locMgr.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true);
    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        LatLng taichung = new LatLng(24.163434771541002, 120.67463672003178);  //設定台中座標
        zoom = 17;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taichung, zoom));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);  //一般地圖

        requestPermission();  //檢查授權

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void requestPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)  //android 6.0 以上
        {
            //判斷是否已取得授權
            int hasPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasPermission != PackageManager.PERMISSION_GRANTED)  //未取得授權
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }
        setMyLocation();  //如果版本為 6.0 以下，或版本為 6.0 以上但使用者已授權，顯示定位圖層
    }

    //使用者完成授權的選擇以後，會呼叫 onRequestPermissionsResult 方法
    //第一個參數 : 請求授權代碼
    //第二個參數 : 請求的授權名稱
    //第三個參數 : 使用者選擇授權的結果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)  //按允許鈕
            {
                setMyLocation();  //顯示定位圖層
            }
            else  //按拒絕鈕
            {
                Toast.makeText(this, "未取得授權!", Toast.LENGTH_SHORT).show();
                finish();  //結束應用程式
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setMyLocation() throws SecurityException
    {
        mMap.setMyLocationEnabled(true);  //顯示定位圖層
    }
}