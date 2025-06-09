// CameraRepository.java
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
import java.util.concurrent.atomic.AtomicBoolean;

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

    // Используем AtomicBoolean для проверки и установки состояния обработки.
    // Это гарантирует, что одновременно будет обрабатываться только один кадр.
    private final AtomicBoolean isProcessingImage = new AtomicBoolean(false);


    public CameraRepository() {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }


    public void scan(InputImage image, ImageProxy imageProxy) {
        // Если уже идет обработка изображения, просто закрываем текущий ImageProxy
        // и не начинаем новую обработку, чтобы избежать перегрузки.
        if (!isProcessingImage.compareAndSet(false, true)) {
            Log.d(LOG_TAG, "Изображение уже обрабатывается, пропуск текущего кадра.");
            imageProxy.close();
            return;
        }

        _scannedText.postValue(null); // Сбрасываем предыдущий результат, так как начинаем новую обработку.

        textRecognizer.process(image)
                .addOnSuccessListener(text -> { // попытка распознавания успешна
                    String rawScannedText = text.getText();
                    String processedText = LicensePlateStringUtils.convert(rawScannedText); // обработка текста в LicensePlateStringUtils
                    String foundLicensePlate = LicensePlateStringUtils.findLicensePlate(processedText); // поиск автомобильного номера РФ
                    _scannedText.postValue(foundLicensePlate);
                    imageProxy.close();
                    isProcessingImage.set(false);
                })
                .addOnFailureListener(e -> {
                    _scanErrorMessage.postValue(Objects.requireNonNull(e.getMessage()));
                    Log.e(LOG_TAG, "Ошибка распознавания текста: " + e.getMessage(), e);
                    _scannedText.postValue(null); // В случае ошибки также сбрасываем текст
                    imageProxy.close();
                    Log.e(LOG_TAG, "ImageProxy закрыт (c ошибкой)");
                    isProcessingImage.set(false); // Снимаем флаг, разрешая обработку следующего кадра.
                });
    }

    public void close() {
        if (textRecognizer != null) {
            textRecognizer.close();
        }
    }
}