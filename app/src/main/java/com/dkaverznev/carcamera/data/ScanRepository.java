package com.dkaverznev.carcamera.data;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.Objects;

public class ScanRepository {

    private final TextRecognizer textRecognizer;

    private final MutableLiveData<String> _scannedText = new MutableLiveData<>();
    public final MutableLiveData<String> scannedText = _scannedText;

    private final MutableLiveData<String> _scanErrorMessage = new MutableLiveData<>();
    public final MutableLiveData<String> scanErrorMessage = _scanErrorMessage;

    public ScanRepository() {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public void scan(InputImage image) {
        Log.d("ScanRepository", "Запуск распознавания текста в ScanRepository.");

        _scanErrorMessage.setValue(null);
        _scannedText.setValue(null);

        textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    _scannedText.setValue(text.getText());
                    Log.d("ScanRepository", "Текст распознан: " + text.getText());
                })
                .addOnFailureListener(e -> {
                    _scanErrorMessage.setValue(Objects.requireNonNull(e.getMessage()));
                    Log.e("ScanRepository", "Ошибка распознавания текста: " + e.getMessage(), e);
                    _scannedText.setValue(null);
                });
    }

    public void close() {
        if (textRecognizer != null) {
            textRecognizer.close();
        }
    }
}