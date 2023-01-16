package comp5216.sydney.edu.au.vennly;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothMessage;
import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;

public class ConnectActivity extends AppCompatActivity {
    AppCompatActivity activity = this;
    CountDownTimer timer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Intent data = getIntent();
        BluetoothDevice device = (BluetoothDevice) data.getParcelableExtra("bluetoothDevice");

        VennlyApplication applicationContext = (VennlyApplication) this.getApplicationContext();
        BluetoothService bluetoothService = applicationContext.bluetoothService();

        bluetoothService.setHandler(handler);
        bluetoothService.startClient(device, applicationContext.playerName());

        // Timeout if can't connect in 30 seconds
        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                Toast.makeText(activity, "Connection timed out...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            BluetoothMessage bluetoothMessage = (BluetoothMessage) message.obj;
            // host has acknowledged connection
            if (message.what == Constants.MESSAGE_RECV_CONNECTION) {
                String icon = bluetoothMessage.getMessage();
                timer.cancel();
                Intent intent = new Intent(ConnectActivity.this, LobbyActivity.class);
                intent.putExtra("icon", icon);
                startActivity(intent);
            }
        }
    };

}
