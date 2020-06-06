package com.example.emoplayer.Music;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.Arrays;
import java.util.List;

public class FaceAndEmotion1 {

    private static final String TAG = "FaceAndEmotion1";
    private static final int width = 64;
    private static final int  height = 64;

    FirebaseCustomLocalModel localModel;
    FirebaseModelInterpreter firebaseInterpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;

    private Bitmap bmpGrayscale;
    private Bitmap croppedBmp;
    public String emotion = null;
    public Rect bounds;
    public static List<String> label = Arrays.asList("angry", "disgust", "scared", "happy", "sad", "surprised", "neutral");

    public FaceAndEmotion1(){
        localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("converted_model.tflite").build();
        try {
            firebaseInterpreter = createInterpreter();
        } catch (FirebaseMLException e) {
            Log.d(TAG, "FaceAndEmotion1: Interpreter exception: " + e.getMessage());
        }
        try {
            inputOutputOptions = createInputOutputOptions();
        } catch (FirebaseMLException e) {
            Log.d(TAG, "FaceAndEmotion1: Input output exception: " + e.getMessage());
        }
    }

    private FirebaseModelInterpreter createInterpreter() throws FirebaseMLException {

        FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
        FirebaseModelInterpreter interpreter = FirebaseModelInterpreter.getInstance(options);
        return interpreter;
    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {

        FirebaseModelInputOutputOptions inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 64, 64, 1})
                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 7})
                .build();
        return inputOutputOptions;
    }

    public String runModel(final Bitmap bitmap) throws FirebaseMLException{

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setMinFaceSize(0.15f)
                .enableTracking()
                .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> faces) {
                        // Task completed successfully
                        for (FirebaseVisionFace face : faces) {
                            Log.d(TAG, "runModel: Successful");
                            bounds = face.getBoundingBox();
                            croppedBmp = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.right, bounds.bottom);
                        }
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Log.d(TAG, "runModel: Failed: " + e.getMessage());
                        // ...
                    }
                });

        croppedBmp = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.right, bounds.bottom);

        if (croppedBmp == null) {
            Log.d(TAG, "runModel: croppedBmp is null");
        }

        Bitmap myBitmap = Bitmap.createScaledBitmap(croppedBmp, width, height, true);
        if (myBitmap != null) {
            Log.d(TAG, "runModel: myBitmap is not null");
            bmpGrayscale = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        }
        int batchNum = 0;
        float[][][][] input = new float[1][64][64][1];
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int pixel = bmpGrayscale.getPixel(x, y);
                input[batchNum][x][y][0] = (pixel - 127) / 128.0f;
            }
        }

        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                .add(input)
                .build();

        firebaseInterpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(new OnSuccessListener<FirebaseModelOutputs>() {
                    @Override
                    public void onSuccess(FirebaseModelOutputs result) {
                        // [START_EXCLUDE]
                        // [START mlkit_read_result]
                        Log.d(TAG,"runModel: interpreter run successful");
                        float[][] output = result.getOutput(0);
                        float[] probabilities = output[0];
                        emotion =  useInferenceResult(probabilities);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Log.d(TAG, "runModel: interpreter failed : " + e.getMessage());
                        // ...
                    }
                });

        return emotion;
    }

    public String useInferenceResult(float[] probabilities) {

        int max = 0;
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[max]) max = i;
        }
        return label.get(max);
    }

}
