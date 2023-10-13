package org.merakilearn.arduinohexupload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.merakilearn.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ArduinoUploader.ArduinoSketchUploader;
import ArduinoUploader.ArduinoUploaderException;
import ArduinoUploader.Config.Arduino;
import ArduinoUploader.Config.McuIdentifier;
import ArduinoUploader.Config.Protocol;
import ArduinoUploader.IArduinoUploaderLogger;
import CSharpStyle.IProgress;

public class ArduinoHexUploadActivity extends AppCompatActivity {
    public static final String TAG = ArduinoHexUploadActivity.class.getSimpleName();
    private UsbSerialManager usbSerialManager;
    private Bundle bundle;
    private Gson gson;
    public enum UsbConnectState {
        DISCONNECTED,
        CONNECT
    }

    private final BroadcastReceiver mUsbNotifyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //Get intent
                case UsbSerialManager.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB permission granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbSerialManager.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission denied", Toast.LENGTH_SHORT).show();
                    break;
                case UsbSerialManager.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbSerialManager.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    usbConnectChange(UsbConnectState.DISCONNECTED);
                    break;
                case UsbSerialManager.ACTION_USB_CONNECT: // USB DISCONNECTED
                    Toast.makeText(context, "USB connected", Toast.LENGTH_SHORT).show();
                    usbConnectChange(UsbConnectState.CONNECT);
                    break;
                case UsbSerialManager.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
                case UsbSerialManager.ACTION_USB_READY:
                    Toast.makeText(context, "Usb device ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbSerialManager.ACTION_USB_DEVICE_NOT_WORKING:
                    Toast.makeText(context, "USB device not working", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private final BroadcastReceiver mUsbHardwareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UsbSerialManager.ACTION_USB_PERMISSION_REQUEST)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) // User accepted our USB connection. Try to open the device as a serial port
                {
                    UsbDevice grantedDevice = intent.getExtras().getParcelable(UsbManager.EXTRA_DEVICE);
                    usbPermissionGranted(grantedDevice.getDeviceName());
                    Intent it = new Intent(UsbSerialManager.ACTION_USB_PERMISSION_GRANTED);
                    context.sendBroadcast(it);

                } else // User not accepted our USB connection. Send an Intent to the Main Activity
                {
                    Intent it = new Intent(UsbSerialManager.ACTION_USB_PERMISSION_NOT_GRANTED);
                    context.sendBroadcast(it);
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                Intent it = new Intent(UsbSerialManager.ACTION_USB_CONNECT);
                context.sendBroadcast(it);

            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                // Usb device was disconnected. send an intent to the Main Activity
                Intent it = new Intent(UsbSerialManager.ACTION_USB_DISCONNECTED);
                context.sendBroadcast(it);

            }
        }
    };

    private void setUsbFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbSerialManager.ACTION_USB_PERMISSION_REQUEST);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbHardwareReceiver, filter);
    }


    private TextView display;
    private TextView portSelect;
    private String deviceKeyName;
    private FloatingActionButton fab;
    private Button requestButton;
    private Collection<String> parseHexDataString;

    public void usbConnectChange(UsbConnectState state) {
        if (state == UsbConnectState.DISCONNECTED) {
            if (requestButton != null) requestButton.setVisibility(View.INVISIBLE);
            if (fab != null) fab.hide();
        } else if (state == UsbConnectState.CONNECT) {
            if (requestButton != null) requestButton.setVisibility(View.VISIBLE);

        }

    }


    public void usbPermissionGranted(String usbKey) {
        Toast.makeText(this, "UsbPermissionGranted:" + usbKey, Toast.LENGTH_SHORT).show();
        portSelect.setText(usbKey);
        deviceKeyName = usbKey;
        if (fab != null) fab.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Check USB Connectivity");
        builder.setMessage("Please check your phone connectivity with Arduino board via USB should be established for code burn ");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Positive button click action
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        bundle = getIntent().getExtras();
        usbSerialManager = new UsbSerialManager(this);
        setUsbFilter();
        setContentView(R.layout.activity_arduino_hex_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        portSelect = (TextView) findViewById(R.id.textViewTitle);
        display = (TextView) findViewById(R.id.textView1);
        fab = findViewById(R.id.fab);
        requestButton = (Button) findViewById(R.id.buttonRequest);
        requestButton.setOnClickListener(view -> {
            Map.Entry<String, UsbDevice> entry = usbSerialManager.getUsbDeviceList().entrySet().iterator().next();
            String keySelect = entry.getKey();
            boolean hasPem = checkDevicePermission(keySelect);
            if (hasPem) {
                portSelect.setText(keySelect);
                deviceKeyName = keySelect;
                if (fab != null) fab.show();
            } else {
                requestDevicePermission(keySelect);
            }
        });

        // Retrieve the data from the Bundle
        String hexDataFromSketch = bundle.getString("HexDataFromSketch1", null);

        if (hexDataFromSketch != null) {
            //Set the values if reading data as string to  Arroy List

            String[] lines = hexDataFromSketch.split("\n");
            parseHexDataString = new ArrayList<>();

            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    parseHexDataString.add(trimmedLine);
                }
            }
            Log.d("Arduino","Getting array buffer directly"+ parseHexDataString);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   uploadHex();
                new Thread(new UploadRunnable()).start();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbHardwareReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbSerialManager.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbSerialManager.ACTION_NO_USB);
        filter.addAction(UsbSerialManager.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbSerialManager.ACTION_USB_CONNECT);
        filter.addAction(UsbSerialManager.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbSerialManager.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbNotifyReceiver, filter);
    }

    public void requestDevicePermission(String key) {
        usbSerialManager.getDevicePermission(key);

    }

    public boolean checkDevicePermission(String key) {
        return usbSerialManager.checkDevicePermission(key);
    }

    /* public UsbSerialDevice getUsbSerialDevice(String key) {
        return usbSerialManager.tryGetDevice(key);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        setFilters();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbNotifyReceiver);
    }

    public void uploadHex(Collection<String> hexFileContents) {
        Log.d("uploaderHex",hexFileContents.toString());
        Log.d("uploaderHex",hexFileContents.toString());
        Log.d("length", String.valueOf(hexFileContents.toString().length()));

        Boards board = Boards.ARDUINO_UNO;

        Arduino arduinoBoard = new Arduino(board.name, board.chipType, board.uploadBaudrate, board.uploadProtocol);

        Protocol protocol = Protocol.valueOf(arduinoBoard.getProtocol().name());
        McuIdentifier mcu = McuIdentifier.valueOf(arduinoBoard.getMcu().name());
        String preOpenRst = arduinoBoard.getPreOpenResetBehavior();
        String preOpenStr = preOpenRst;
        if (preOpenRst == null) preOpenStr = "";
        else if (preOpenStr.equalsIgnoreCase("none")) preOpenStr = "";

        String postOpenRst = arduinoBoard.getPostOpenResetBehavior();
        String postOpenStr = postOpenRst;
        if (postOpenRst == null) postOpenStr = "";
        else if (postOpenStr.equalsIgnoreCase("none")) postOpenStr = "";

        String closeRst = arduinoBoard.getCloseResetBehavior();
        String closeStr = closeRst;
        if (closeRst == null) closeStr = "";
        else if (closeStr.equalsIgnoreCase("none")) closeStr = "";

        Arduino customArduino = new Arduino("Custom", mcu, arduinoBoard.getBaudRate(), protocol);
        if (!TextUtils.isEmpty(preOpenStr))
            customArduino.setPreOpenResetBehavior(preOpenStr);
        if (!TextUtils.isEmpty(postOpenStr))
            customArduino.setPostOpenResetBehavior(postOpenStr);
        if (!TextUtils.isEmpty(closeStr))
            customArduino.setCloseResetBehavior(closeStr);
        if (protocol == Protocol.Avr109) customArduino.setSleepAfterOpen(0);
        else customArduino.setSleepAfterOpen(250);

        IArduinoUploaderLogger logger = new IArduinoUploaderLogger() {
            @Override
            public void Error(String message, Exception exception) {
                Log.e(TAG, "Error:" + message);
                logUI("Error:" + message);
            }

            @Override
            public void Warn(String message) {
                Log.w(TAG, "Warn:" + message);
                logUI("Warn:" + message);
            }

            @Override
            public void Info(String message) {
                Log.i(TAG, "Info:" + message);
                logUI("Info:" + message);
            }

            @Override
            public void Debug(String message) {
                Log.d(TAG, "Debug:" + message);
                logUI("Debug:" + message);
            }

            @Override
            public void Trace(String message) {
                Log.d(TAG, "Trace:" + message);
                logUI("Trace:" + message);
            }
        };

        IProgress progress = new IProgress<Double>() {
            @Override
            public void Report(Double value) {
                String result = String.format("Upload progress: %1$,3.2f%%", value * 100);
                Log.d(TAG, result);
                logUI("Process:" + result);

            }
        };

        try {
            //Collection<String> hexFileContents = new LineReader(reader).readLines();
            ArduinoSketchUploader<SerialPortStreamImpl> uploader = new ArduinoSketchUploader<SerialPortStreamImpl>(this, SerialPortStreamImpl.class, null, logger, progress);
//            ArduinoSketchUploader<SerialPortStreamImpl> uploader = new ArduinoSketchUploader<SerialPortStreamImpl>(this, null, logger, progress) {
//                //some code
//            };
            uploader.UploadSketch(hexFileContents, customArduino, deviceKeyName);
        } catch (ArduinoUploaderException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void logUI(String text) {
        runOnUiThread(() -> display.append(text + "\n"));
    }

    private class UploadRunnable implements Runnable {
        @Override
        public void run() {
           //  Log.d("ArduinoHex","Read Data from bundle intent ArrayList"+ parseHexDataString);
            if( parseHexDataString != null) {
                uploadHex(parseHexDataString);
            } else {
                Log.d("ArduinoHex","failed to parsedata conversion");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
