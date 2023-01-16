package comp5216.sydney.edu.au.vennly;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.function.Consumer;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;

public class JoinActivity extends AppCompatActivity {
    ArrayList<String> lobbies;
    ArrayList<String> deviceAddresses;
    JoinListAdapter arrayAdapter;
    VennlyApplication applicationContext;
    BluetoothService bluetoothService;
    EditText playerNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        lobbies = new ArrayList<>();
        deviceAddresses = new ArrayList<>();

        ListView listView = findViewById(R.id.room_list);
        arrayAdapter = new JoinListAdapter(this, lobbies, onListItemClick);
        listView.setAdapter(arrayAdapter);

        applicationContext = (VennlyApplication) this.getApplicationContext();
        applicationContext.setGameMode(Constants.GAMEMODE_MULTI);
        bluetoothService = applicationContext.bluetoothService();

        playerNameView  = findViewById(R.id.player_name);
    }

    @Override
    public void onStart() {
        super.onStart();

        bluetoothService.enableBluetooth(this);
        bluetoothService.enableDeviceDiscovery(this);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    // when discovering devices need to filter out non-vennly devices so that screen
    // does not become cluttered.
    private boolean isVennlyDevice(String deviceName) {
        String[] splitName = deviceName.split(":");
        if (splitName.length < 2) {
            return false;
        }
        return splitName[0].equals(Constants.HOST_CODE);
    }

    // Device names will be of the form <Constants.HOST_CODE>:<lobby name>
    // assumes deviceName belongs to a vennly device.
    private String extractLobbyName(String deviceName) {
        int indexOfDelimiter = deviceName.indexOf(":");
        return deviceName.substring(indexOfDelimiter + 1);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = null;
                String deviceHardwareAddress = null;
                try {
                    deviceName = device.getName();
                    deviceHardwareAddress = device.getAddress();
                } catch (SecurityException securityException) {
                    Log.i("security exception", "permissions not enabled");
                }

                if (deviceName != null && isVennlyDevice(deviceName)) {
                    lobbies.add(extractLobbyName(deviceName));
                    deviceAddresses.add(deviceHardwareAddress);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private final Consumer<Integer> onListItemClick = (position) -> {
        bluetoothService.disableDeviceDiscovery();

        String playerNameText = playerNameView.getText().toString();
        applicationContext.setPlayerName(playerNameText);
        applicationContext.gameState().setLobbyName(lobbies.get(position));

        String address = deviceAddresses.get(position);
        BluetoothDevice device = bluetoothService.getRemoteDevice(address);

        Intent intent = new Intent(JoinActivity.this, ConnectActivity.class);
        intent.putExtra("bluetoothDevice", device);
        startActivity(intent);

        try {
            Log.i("Join activity", "connected");
        } catch (SecurityException e) {
            Log.e("discover", "security exception", e);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_BLUETOOTH_PERM_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bluetoothService.enableDeviceDiscovery(this);
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            }  else {
                Toast.makeText(this, "Need to enable bluetooth to join games", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
