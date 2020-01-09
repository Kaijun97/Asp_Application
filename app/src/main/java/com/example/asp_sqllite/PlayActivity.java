package com.example.asp_sqllite;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PlayActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT =1 ;
    private Button btnPlay;
    private Button btnConnect;
    private ListView btList;
    SQLiteDatabase db;

    private Handler handler;

    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter<String> testAdapter;
    private ArrayAdapter<String> deviceAdapter;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private ArrayList<ScanResult> results = new ArrayList<>();
    private ScanSettings settings;

    private Intent intent;


    private ListView bluetoothList;

    private boolean completed = false;

    static final UUID HR_SERVICE_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_play);

        this.handler= new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            this.btList = (ListView) findViewById(R.id.btlist);
            deviceAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
            testAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
            intent = getIntent();

            db = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);
            checkBluetooth();


            this.btnPlay = (Button) findViewById(R.id.btnPlay);

            this.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bleGatt!=null) {
                        final String username = intent.getStringExtra("username");
                        System.out.println(username+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        String sqlStatement = "insert into records (Name) values( '" + username + "')";
                        String result = updateTable(sqlStatement);

                        /*
                         * Run query to get recid to be passed over to the next activity
                         *
                         * */

                        final Cursor cursor = db.rawQuery("SELECT recID From records", null);
                        int num = 0;
                        if (cursor != null) {
                            //                        if (cursor.moveToFirst()) {
////                            num = cursor.getInt(0);
                            cursor.moveToLast();
                            num = cursor.getInt(0);
                            cursor.close();
                            db.close();
                        }
                        Intent intent = new Intent(PlayActivity.this, testPlayActivity.class);
                        intent.putExtra("ID", Integer.toString(num));
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Connect to BLE device", Toast.LENGTH_LONG).show();
                    //finish();
                }
            });

            this.btnConnect = (Button) findViewById(R.id.connect);
            this.btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startScan();
                    Dialog d = new Dialog(PlayActivity.this); //open up dialog box with listview
                    d.setContentView(R.layout.bluetooth_device);
                    d.setTitle("Devices");
                    d.show();

                    //stopScan();

                    Button scanBtn = d.findViewById(R.id.scanBluetooth);
                    bluetoothList = d.findViewById(R.id.bluetoothDeviceList);
                    bluetoothList.setAdapter(deviceAdapter);
                    bluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ScanResult device = results.get(i);
                            Toast.makeText(getApplicationContext(), device.getDevice().getName(), Toast.LENGTH_LONG).show();
                            bleGatt = device.getDevice().connectGatt(getApplicationContext(), false, bleGattCallback);
                            
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@testing 123");
                            //finish();
                            try {
                                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                                Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                                ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

                                if(uuids != null) {
                                    for (ParcelUuid uuid : uuids) {
                                        System.out.println(uuid.getUuid().toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                    }
                                }else{
                                    System.out.println("fail");
                                }

                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    scanBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) { //clear all list and adapters before scanning again

                            deviceList.clear();
                            deviceAdapter.clear();
                            results.clear();
                            startScan();
                            //stopScan();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stopScan();
                                }
                            },5000);
                        }
                    });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopScan();
                        }
                    },5000);
                }
            });

    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }



    private void checkBluetooth()
    {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private String updateTable(String sql) {

        try {

            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {

            System.out.println(e.toString());
            return ("Error");
        }


        Toast.makeText(this, "DB updated", Toast.LENGTH_LONG).show();
        return ("Welcome");

    }
    private void stopScan(){
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        bleScanner.stopScan(scanCallback);
    }
    private void startScan() {
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bleScanner != null) { //setting up of scanner
            final ScanFilter scanFilter =new ScanFilter.Builder().build();
            settings =new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bleScanner.startScan(Arrays.asList(scanFilter), settings, scanCallback);
            //stopScan();
        }
        else
            checkBluetooth();
    }

    private ScanCallback scanCallback = new ScanCallback() { //scan and return device results
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            System.out.println("@@@@@@@@@ "+callbackType + result);
            if (bleScanner != null && !deviceList.contains(result.getDevice().getName())) {
                deviceList.add(result.getDevice().getName());
                String device = result.getDevice().getName() + "\n" + result.getDevice().getAddress();
                deviceAdapter.add(device); //Store device name and address
                results.add(result); //records found devices as ScanResult
            }

        }
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("TAG","onScanFailed");
        }

    };

    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState== BluetoothProfile.STATE_CONNECTED){
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Connected");


            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                //Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Not Connected");

            }
            gatt.discoverServices();
            super.onConnectionStateChange(gatt, status, newState);
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService service = gatt.getService(HR_SERVICE_UUID);
            System.out.println(service+"!!!!!!!!!!!!!!!!!!!!!!");
            BluetoothGattCharacteristic temperatureCharacteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID);
            gatt.readCharacteristic(temperatureCharacteristic);
            super.onServicesDiscovered(gatt, status);
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            final String value = characteristic.getStringValue(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                        //Toast.makeText(getApplicationContext(), "Correct Bluetooth: " + value, Toast.LENGTH_LONG).show();
//                        testAdapter.add(value);
//                        btList.setAdapter(testAdapter);
                          System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@correct");

                    } else {
                        //Toast.makeText(getApplicationContext(), "Wrong Bluetooth", Toast.LENGTH_LONG).show();
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@wrong");
                    }
                }
            });
            BluetoothGattService service = gatt.getService(HR_SERVICE_UUID);
            //readNextCharacteristic(gatt, characteristic);
            super.onCharacteristicRead(gatt, characteristic, status);
        }
    };
}


