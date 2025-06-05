package com.dkaverznev.carcamera.data;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.dkaverznev.carcamera.utils.LicensePlateStringUtils;
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

    public void scan(InputImage image, ImageProxy imageProxy) {
        Log.d("ScanRepository", "Запуск распознавания текста в ScanRepository.");
        _scanErrorMessage.postValue(null);
        _scannedText.postValue(null);

        textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    String rawScannedText = text.getText();
                    Log.d("ScanRepository", "Текст распознан (сырой): " + rawScannedText);

                    String processedText = LicensePlateStringUtils.convert(rawScannedText);
                    Log.d("ScanRepository", "Текст распознан (обработанный): " + processedText);

                    String foundLicensePlate = LicensePlateStringUtils.findLicensePlate(processedText);

                    if (foundLicensePlate != null) {
                        _scannedText.postValue(foundLicensePlate);
                        Log.d("ScanRepository", "Найден строгий номерной знак: " + foundLicensePlate);
                    } else {
                        Log.d("ScanRepository", "Строгий номерной знак НЕ найден, сохраняем сырой текст: (" + rawScannedText + ")");
                    }

                    imageProxy.close();
                    Log.d("ScanRepository", "ImageProxy успешно закрыт после распознавания.");
                })
                .addOnFailureListener(e -> {
                    _scanErrorMessage.postValue(Objects.requireNonNull(e.getMessage()));
                    Log.e("ScanRepository", "Ошибка распознавания текста: " + e.getMessage(), e);
                    _scannedText.postValue(null);
                    imageProxy.close();
                    Log.e("ScanRepository", "ImageProxy закрыт после ошибки распознавания.");
                });
    }

    public void close() {
        if (textRecognizer != null) {
            textRecognizer.close();
        }
    }
}