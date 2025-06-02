package com.dkaverznev.carcamera.data;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScanRepository {

    private final Context context;
    private final TextRecognizer textRecognizer;

    public ScanRepository(Context context) {
        this.context = context;
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void scan(InputImage image) {
        Log.d("ScanRepository", "Запуск распознавания текста в ScanRepository.");

        textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    String resultText = text.getText();
                });
    }

    public void close() {
        if (textRecognizer != null) {
            textRecognizer.close();
        }
    }
}