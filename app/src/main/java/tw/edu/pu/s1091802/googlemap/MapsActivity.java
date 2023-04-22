package tw.edu.pu.s1091802.googlemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tw.edu.pu.s1091802.googlemap.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , LocationListener , GoogleMap.OnMarkerClickListener , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener
{

    public static final int ROUND = 10;
    private GoogleMap mMap;
    public GoogleApiClient googleApiClient;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    Marker marker;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locMgr;
    float zoom;
    String bestProv;

    @Override
    public void  onLocationChanged(Location location)
    {
        //取得地圖座標值 : 緯度 , 經度
        String x = "緯=" + Double.toString(location.getLatitude());
        String y = "經=" + Double.toString(location.getLongitude());

        LatLng Point = new LatLng(location.getLatitude() , location.getLongitude());
        zoom = 17;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point , zoom));

        if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);  //顯示定位圖示
        }
        Toast.makeText(this , x + "\n" + y , Toast.LENGTH_LONG).show();
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

        ChildEventListener mChildEventListener;
        mUsers= FirebaseDatabase.getInstance().getReference("coordinates");
        mUsers.push().setValue(marker);
        Button nextPageBtn = (Button)findViewById(R.id.button);  //跳轉頁面
        nextPageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this , search.class);  //從主畫面跳到搜尋畫面
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
        bestProv = locMgr.getBestProvider(criteria , true);  //取得最佳定位方式

        //如果GPS或網路定位開啟，更新位置
        if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            //確認 ACCESS_FINE_LOCATION 權限是否授權
            if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locMgr.requestLocationUpdates(bestProv , 1000 , 1 , this);
            }
        }
        else
        {
            Toast.makeText(this , "請開啟定位服務" , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        //確認 ACCESS_FINE_LOCATION 權限是否授權
        if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locMgr.removeUpdates(this);
        }
    }

    @Override
    public void  onStatusChanged(String provider , int status , Bundle extras)
    {
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria , true);
    }

    @Override
    public void  onProviderEnabled(String provider)
    {

    }

    @Override
    public void  onProviderDisabled(String provider)
    {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);

        //setting the size of marker in map by using Bitmap Class
        int height = 80;
        int width = 80;

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.scooter);
        Bitmap b=bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    UserInformation user = s.getValue(UserInformation.class);
                    LatLng location=new LatLng(Double.parseDouble(user.latitude),Double.parseDouble(user.longitude));
                    mMap.addMarker(new MarkerOptions().position(location).title(user.name)).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LatLng taichung = new LatLng(24.163434771541002 , 120.67463672003178);  //設定台中座標
        zoom = 17;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taichung , zoom));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);  //一般地圖

        requestPermission();  //檢查授權

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void requestPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)  //androis 6.0 以上
        {
            //判斷是否已取得授權
            int hasPermission = ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasPermission != PackageManager.PERMISSION_GRANTED)  //未取得授權
            {
                ActivityCompat.requestPermissions(this , new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION} , 1);
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
    public void onRequestPermissionsResult(int requestCode , String[] permissions , int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)  //按允許鈕
            {
                setMyLocation();  //顯示定位圖層
            }
            else  //按拒絕鈕
            {
                Toast.makeText(this , "未取得授權!" , Toast.LENGTH_SHORT).show();
                finish();  //結束應用程式
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode , permissions , grantResults);
        }
    }

    private void setMyLocation() throws SecurityException
    {
        mMap.setMyLocationEnabled(true);  //顯示定位圖層
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}