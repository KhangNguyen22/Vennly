package comp5216.sydney.edu.au.vennly;

public interface Constants {
    int ANDROID_VERSION =  android.os.Build.VERSION.SDK_INT;
    String NAME = "VENNLY";

    int REQUEST_BLUETOOTH_PERM_CODE = 0;

    int DISCOVERABLE_TIME_LIMIT = 600;

    int MESSAGE_NEW_CONNECTION = 0;
    int MESSAGE_HOST_START = 1;
    int MESSAGE_GAME_STATE = 2;
    // This code is sent by server to client when connection is successful
    int MESSAGE_RECV_CONNECTION = 5;
    int MESSAGE_REQUEST_DATA = 6;
    int MESSAGE_SEND_DATA = 7;
    int MESSAGE_CATEGORY = 8;
    int MESSAGE_CONTINUE = 9;
    int MESSAGE_GAME_FINISHED = 10;

    int DEVICE_TYPE_NONE = 0;
    int DEVICE_TYPE_CLIENT = 1;
    int DEVICE_TYPE_SERVER = 2;

    String SERVER_ID = "ebdde58d-8da2-4610-85b7-d7530e99e5bb";
    String HOST_CODE = "pliOkoOyLm"; // Use this code to identify Vennly devices

    int GAMEMODE_SINGLE = 0;
    int GAMEMODE_MULTI = 1;

    //int GAMEPLAY_TIME_LIMIT = 30000;
    int GAMEPLAY_TIME_LIMIT = 15000;
}
