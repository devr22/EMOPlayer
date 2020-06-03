package com.example.emoplayer.Music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emoplayer.Adapter.AdapterEmotionSong;
import com.example.emoplayer.Adapter.AdapterRecommendedSong;
import com.example.emoplayer.Model.Model_Songs;
import com.example.emoplayer.Model.Model_Users;
import com.example.emoplayer.R;
import com.example.emoplayer.Utils.AppPermission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

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


public class FaceAndEmotion extends HomeFragment {

    public static Bitmap croppedBmp;
    public static String emotion;
    public static Rect bounds;
    public static List<String> label = Arrays.asList("angry" ,"disgust","scared", "happy", "sad", "surprised",
            "neutral");
    // Face Model
    private void imageFromBitmap() {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

    }

    private void detectFaces(FirebaseVisionImage image) {
        // [START set_detector_options]
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();


        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        for (FirebaseVisionFace face : faces) {
                                            bounds = face.getBoundingBox();
                                        }
                                        // ...
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

            croppedBmp = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.right, bounds.bottom);


        }

    public Bitmap getImage() {
        return croppedBmp;
    }


    // Emotion Model
    private void configureLocalModelSource() {
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("converted_model.tflite")
                .build();

    }


    private FirebaseModelInterpreter createInterpreter(FirebaseCustomLocalModel localModel) throws FirebaseMLException {

        FirebaseModelInterpreter interpreter = null;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            // ...
        }
        return interpreter;
    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {

        FirebaseModelInputOutputOptions inputOutputOptions =
                new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 64, 64, 1})
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 7})
                        .build();
        return inputOutputOptions;
    }

    private float[][][][] bitmapToInputArray() {

        Bitmap bitmap = getImage();
        bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true);
        Bitmap bmpGrayscale = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int batchNum = 0;
        float[][][][] input = new float[1][64][64][1];
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int pixel = bmpGrayscale.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (pixel - 127)/ 128.0f;
            }
        }
        return input;
    }

    private void runInference() throws FirebaseMLException {
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder().build();
        FirebaseModelInterpreter firebaseInterpreter = createInterpreter(localModel);
        float[][][][] input = bitmapToInputArray();
        FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();


        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                .add(input)  // add() as many input arrays as your model requires
                .build();
        firebaseInterpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>() {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result) {
                                // [START_EXCLUDE]
                                // [START mlkit_read_result]
                                float[][] output = result.getOutput(0);
                                float[] probabilities = output[0];

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
    }


    private void useInferenceResult(float[] probabilities)  {

            int max = 0;
            for ( int i = 1; i < probabilities.length; i++ )
            {
                if ( probabilities[i] > probabilities[max] ) max = i;
            }
            emotion = label.get(max);
        }








}

