package com.github.se2einzelphase;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    EditText textInput;
    TextView responseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = findViewById(R.id.textInput);
        responseView = findViewById(R.id.responseView);
    }

    private static boolean isPrime(int n) {

        if (n <= 1) return false;

        return IntStream.rangeClosed(2, n/2).noneMatch(i -> n%i == 0);
    }
    public void sortFilterPrime(View view) {
        String content = textInput.getText()
                .chars()
                .filter(n -> !isPrime(Character.getNumericValue(n)))
                .sorted()
                .collect(StringBuilder::new,
                         StringBuilder::appendCodePoint,
                         StringBuilder::append)
                .toString();
        runOnUiThread(() -> responseView.setText(content));
    }

    public void sendNumber(View view) {
        new Thread(() -> {
            try {
                Socket client = new Socket("se2-isys.aau.at", 53212);
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8),
                        true);

                output.println(textInput.getText().toString());
                String response = input.readLine();

                runOnUiThread(() -> responseView.setText(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}