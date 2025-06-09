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

/**
 * Главный репозиторий сканирования который использует LicensePlateStringUtils для обработки текста
 */
public class CameraRepository {

    private final TextRecognizer textRecognizer;

    private final MutableLiveData<String> _scannedText = new MutableLiveData<>();
    public final MutableLiveData<String> scannedText = _scannedText;

    private final MutableLiveData<String> _scanErrorMessage = new MutableLiveData<>();
    public final MutableLiveData<String> scanErrorMessage = _scanErrorMessage;

    private final String LOG_TAG = "ScanRepository";

    public CameraRepository() {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }


    public void scan(InputImage image, ImageProxy imageProxy) {
        Log.d(LOG_TAG, "Запуск распознавания текста в ScanRepository.");
        _scannedText.postValue(null);

        textRecognizer.process(image)
                .addOnSuccessListener(text -> { // попытка распознавания успешна
                    String rawScannedText = text.getText();
                    Log.d(LOG_TAG, "Необработанный текст" + rawScannedText);

                    String processedText = LicensePlateStringUtils.convert(rawScannedText); // обработка текста в LicensePlateStringUtils
                    Log.d(LOG_TAG, "Обработанный текст: " + processedText);

                    String foundLicensePlate = LicensePlateStringUtils.findLicensePlate(processedText); // поиск автомобильного номера РФ

                    if (foundLicensePlate != null) {
                        _scannedText.postValue(foundLicensePlate);
                        Log.d(LOG_TAG, "Автомобильный номер: " + foundLicensePlate);
                    } else {
                        _scannedText.postValue(null);
                        Log.d(LOG_TAG, "Номер не найден");
                    }

                    imageProxy.close();
                    Log.d(LOG_TAG, "ImageProxy закрыт");
                })
                .addOnFailureListener(e -> {
                    _scanErrorMessage.postValue(Objects.requireNonNull(e.getMessage()));
                    Log.e(LOG_TAG, "Ошибка распознавания текста: " + e.getMessage(), e);
                    _scannedText.postValue(null); // В случае ошибки также сбрасываем текст
                    imageProxy.close();
                    Log.e(LOG_TAG, "ImageProxy закрыт (c ошибкой)");
                });
    }

    public void close() {
        if (textRecognizer != null) {
            textRecognizer.close();
        }
    }
}