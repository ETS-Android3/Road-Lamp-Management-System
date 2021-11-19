package com.whugra.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.amap.api.maps2d.*;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.whugra.R;
import permissions.dispatcher.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//运行时权限注解
@RuntimePermissions
public class HomeFragment extends Fragment {
    //经纬度
    private List<Map<String, Double>> addresses = null;
    private Bundle savedInstanceState;
    private MapView mMapView;
    private AMap aMap;
    private CameraUpdate cameraUpdate;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        this.savedInstanceState=savedInstanceState;
        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //当android系统小于5.0的时候直接定位显示，不用动态申请权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initMap();
        } else {
            HomeFragmentPermissionsDispatcher.ApplySuccessWithCheck(this);
        }
    }

    /**************************************地图***************************************/

    private void initMap()
    {
        //获取地图控件引用
        mMapView = getActivity().findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap=mMapView.getMap();
        //初始化地图数据
        addresses = new ArrayList<>();
        Map<String,Double> map1 = new HashMap();
        map1.put("lng",114.448577);
        map1.put("lat",30.506242);
        Map<String,Double> map2 = new HashMap<>();
        map2.put("lng",114.446544);
        map2.put("lat",30.509446);
        Map<String,Double> map3 = new HashMap<>();
        map3.put("lng",114.441595);
        map3.put("lat",30.506928);
        addresses.add(map1);
        addresses.add(map2);
        addresses.add(map3);
        //添加地图标点
        for(int i = 0;i<3;i++)
        {
            //设置标点数据
            double lng = addresses.get(i).get("lng");
            double lat = addresses.get(i).get("lat");
            LatLng latLng = new LatLng(lat,lng);
            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("设备编号："+(i+1))
                    .snippet("经度:"+lng+"\r\n纬度："+lat));
        }
        LatLng latLng = new LatLng(30.45,114.51);
        cameraUpdate= CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,11,0,30));
        aMap.moveCamera(cameraUpdate);//地图移向指定区域
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    /************************************权限申请***********************************/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 申请权限告诉用户原因时
     * @param request
     */
    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showRationaleForMap(PermissionRequest request) {
        showRationaleDialog("使用此功能需要打开定位的权限", request);
    }

    /**
     * 申请权限被拒绝时
     *
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapDenied() {
        Toast.makeText(getActivity(),"你拒绝了权限，该功能不可用",Toast.LENGTH_LONG).show();
    }

    /**
     * 申请权限被拒绝并勾选不再提醒时
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapNeverAskAgain() {
        AskForPermission();
    }

    /**
     * 告知用户具体需要权限的原因
     * @param messageResId
     * @param request
     */
    private void showRationaleDialog(String messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();//请求权限
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    /**
     * 被拒绝并且不再提醒,提示用户去设置界面重新打开权限
     */
    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("当前应用缺少定位权限,请去设置界面打开\n打开之后按两次返回键可回到该应用哦");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    /**
     * 申请权限成功时
     */
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void ApplySuccess() {
        initMap();
    }


}
