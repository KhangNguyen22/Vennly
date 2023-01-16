package comp5216.sydney.edu.au.vennly.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.vennly.Constants;

public class BluetoothService {
    private BluetoothAdapter bluetoothAdapter;

    // Set a special tempDeviceName so that when searching for nearby devices, app only
    // shows bluetooth devices that are hosting a Vennly server.
    private String actualDeviceName;
    private String tempDeviceName;

    private Handler handler;

    private int deviceType = Constants.DEVICE_TYPE_NONE;
    private BluetoothServer bluetoothServer;
    private BluetoothClient bluetoothClient;

    private static String NAME = "VennlyChat";

    public BluetoothService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            actualDeviceName = bluetoothAdapter.getName();
        } catch (SecurityException securityException) {
            Log.i("Security exception", "invalid perms | line 33 Bluetooth service");
        }
        tempDeviceName = actualDeviceName + "|Vennly";
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void getConnectPermissions(AppCompatActivity activity) {
        List<String> requiredPermissions = new ArrayList<>();

        if (Constants.ANDROID_VERSION <= Build.VERSION_CODES.R) {
            if (!permissionAvailable(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else {
            if (!permissionAvailable(activity, Manifest.permission.BLUETOOTH_SCAN)) {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
            }

            if (!permissionAvailable(activity, Manifest.permission.BLUETOOTH_CONNECT)) {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        if (requiredPermissions.size() > 0) {
            String[] requiredPermArray = new String[requiredPermissions.size()];
            requiredPermArray = requiredPermissions.toArray(requiredPermArray);
            activity.requestPermissions(requiredPermArray, Constants.REQUEST_BLUETOOTH_PERM_CODE);
        }
    }

    public boolean hasHostPermissions(AppCompatActivity activity) {
        if (Constants.ANDROID_VERSION > Build.VERSION_CODES.R) {
            return permissionAvailable(activity, Manifest.permission.BLUETOOTH_CONNECT);
        }

        return true;
    }

    public void getHostPermissions(AppCompatActivity activity) {
        List<String> requiredPermissions = new ArrayList<>();

        if (Constants.ANDROID_VERSION > Build.VERSION_CODES.R) {
            /*
            if (!permissionAvailable(activity, Manifest.permission.BLUETOOTH_ADVERTISE)) {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            }
             */

            if (!permissionAvailable(activity, Manifest.permission.BLUETOOTH_CONNECT)) {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

        }

        if (requiredPermissions.size() > 0) {
            String[] requiredPermArray = new String[requiredPermissions.size()];
            requiredPermArray = requiredPermissions.toArray(requiredPermArray);
            activity.requestPermissions(requiredPermArray, Constants.REQUEST_BLUETOOTH_PERM_CODE);
        }
    }

    public void enableBluetooth(AppCompatActivity activity) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ActivityResultLauncher<Intent> requestBluetoothLauncher = activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    (result) -> {
                        if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED) {
                            // do something to handle this.
                        } else if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        }
                    }
            );
            requestBluetoothLauncher.launch(enableBtIntent);
            return;
        }
    }

    private boolean permissionAvailable(AppCompatActivity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void makeDeviceDiscoverable(AppCompatActivity activity, Runnable onCancel) {
        //enableBluetooth(activity);
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, Constants.DISCOVERABLE_TIME_LIMIT);
        ActivityResultLauncher<Intent> makeDiscoverableLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED) {
                        onCancel.run();
                    }
                }
        );
        makeDiscoverableLauncher.launch(discoverableIntent);

    }

    public void enableDeviceDiscovery(AppCompatActivity activity) {
        getConnectPermissions(activity);

        try {
            bluetoothAdapter.startDiscovery();
        } catch (SecurityException securityException) {
            // handle this
            Log.i("Security exception", "exception at BluetoothService line 99", securityException);
        }
    }

    public void disableDeviceDiscovery() {
        try {
            bluetoothAdapter.cancelDiscovery();
        } catch (SecurityException securityException) {
            Log.i("Security exception", "exception at BluetoothService line 106");
        }
    }

    // Set bluetooth name of device so that other Vennly apps can filter out non vennly applications.
    public void setBluetoothName(String lobbyName) {
        try {
            String tempName = Constants.HOST_CODE + ":" + lobbyName;
            bluetoothAdapter.setName(tempName);
        } catch (SecurityException securityException) {
            Log.i("security exception", "invalid perms");
        }
    }

    public void resetBluetoothName() {
        try {
            bluetoothAdapter.setName(actualDeviceName);
        } catch (SecurityException securityException) {
            Log.i("security exception", "invalid perms");
        }
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return bluetoothAdapter.getRemoteDevice(address);
    }

    public void startServer() {
        bluetoothServer = new BluetoothServer(bluetoothAdapter, tempDeviceName, this::readMessage);
        bluetoothServer.startServerThreads();
        deviceType = Constants.DEVICE_TYPE_SERVER;
    }


    public void stopServerThreads() {
        if (bluetoothServer == null) {
            Log.i("Start new server thread", "Bluetooth server is null");
            return;
        }
        bluetoothServer.stopServerThreads();
    }

    public void startClient(BluetoothDevice device, String clientName) {
        bluetoothClient = new BluetoothClient(device, clientName, this::readMessage);
        //bluetoothClient.connectToServer();
        bluetoothClient.startClient();
        deviceType = Constants.DEVICE_TYPE_CLIENT;
    }

    public void stopClientThreads() {
        if (bluetoothClient == null) {
            Log.i("Start new server thread", "Bluetooth server is null");
            return;
        }
        bluetoothClient.stopClientThreads();
    }

    private void readMessage(Pair<Integer, Object> message) {
        handler.obtainMessage(message.first, message.second).sendToTarget();
    }


    /**********************************************************************************************
     *  SERVER METHODS
     *  --------------
     *  The following methods will only be executed if the device is hosting the server
     **********************************************************************************************/
    public void writeMessageToAll(int code, BluetoothMessage message) {
        if (deviceType != Constants.DEVICE_TYPE_SERVER) {
            throw new RuntimeException("Trying to access server method as client");
        }

        bluetoothServer.writeMessageToAll(code, message);
    }

    public void writeMessageToTarget(int code, BluetoothMessage message, String target) {
        if (deviceType != Constants.DEVICE_TYPE_SERVER) {
            throw new RuntimeException("Trying to access server method as client");
        }

        bluetoothServer.writeMessageToTarget(code, message, target);
    }

    public void writeMessageExcludeTarget(int code, BluetoothMessage message, String target) {
        if (deviceType != Constants.DEVICE_TYPE_SERVER) {
            throw new RuntimeException("Trying to access server method as client");
        }

        bluetoothServer.writeMessageExcludeTarget(code, message, target);
    }

    /**********************************************************************************************
     *  CLIENT METHODS
     *  --------------
     *  The following methods will only be executed if the device is a client
     **********************************************************************************************/
    public void writeMessageToServer(int code, BluetoothMessage message) {
        if (deviceType != Constants.DEVICE_TYPE_CLIENT) {
            throw new RuntimeException("Trying to access client method on non-client device");
        }

        bluetoothClient.writeMessage(code, message);
    }
}

