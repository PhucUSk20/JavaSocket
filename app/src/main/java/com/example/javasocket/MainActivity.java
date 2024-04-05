package com.example.javasocket;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private Button clickable;
    private ConnectionManager connectionManager;
    private Button buttonForward, buttonLeft, buttonRight, buttonForwardLeft, buttonForwardRight, buttonReverse, buttonReverseLeft, buttonReverseRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonForward = findViewById(R.id.buttonForward);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);
        buttonForwardLeft = findViewById(R.id.buttonForwardLeft);
        buttonForwardRight = findViewById(R.id.buttonForwardRight);
        buttonReverse = findViewById(R.id.buttonReverse);
        buttonReverseRight = findViewById(R.id.buttonReverseRight);
        buttonReverseLeft = findViewById(R.id.buttonReverseLeft);
        connectionManager = new ConnectionManager();

        setupButton(buttonForward, "forward", "stop");
        setupButton(buttonLeft, "left", "stop");
        setupButton(buttonRight, "right", "stop");
        setupButton(buttonForwardLeft, "forward_left","stop");
        setupButton(buttonForwardRight, "forward_right","stop");
        setupButton(buttonReverse, "reverse","stop");
        setupButton(buttonReverseRight,"reverse_right","stop");
        setupButton(buttonReverseLeft,"reverse_left","stop");

        // Kết nối đến server khi khởi tạo
        connectionManager.startConnection("192.168.1.12", 5000);
    }

    private void setupButton(Button button, String startCommand, String stopCommand) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Người dùng nhấn giữ nút
                    connectionManager.sendMessage(startCommand);
                    return true; // Sự kiện đã được xử lý
                case MotionEvent.ACTION_UP:
                    // Người dùng thả nút
                    connectionManager.sendMessage(stopCommand);
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionManager.stopConnection(); // Đóng kết nối khi ứng dụng bị hủy
    }

    // Lớp ConnectionManager sẽ quản lý kết nối
    class ConnectionManager {
        private Socket socket;
        private PrintWriter printWriter;

        public void startConnection(String ip, int port) {
            new Thread(() -> {
                try {
                    socket = new Socket(ip, port);
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        public void sendMessage(String message) {
            new Thread(() -> {
                if (printWriter != null) {
                    printWriter.println(message);
                }
            }).start();
        }

        public void stopConnection() {
            new Thread(() -> {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (printWriter != null) {
                        printWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
