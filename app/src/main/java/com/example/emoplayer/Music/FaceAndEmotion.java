//package com.example.emoplayer.Music;
//
//import android.graphics.Bitmap;
//import android.graphics.Rect;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.ml.common.FirebaseMLException;
//import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
//import com.google.firebase.ml.custom.FirebaseModelDataType;
//import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
//import com.google.firebase.ml.custom.FirebaseModelInputs;
//import com.google.firebase.ml.custom.FirebaseModelInterpreter;
//import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
//import com.google.firebase.ml.custom.FirebaseModelOutputs;
//import com.google.firebase.ml.vision.FirebaseVision;
//import com.google.firebase.ml.vision.common.FirebaseVisionImage;
//import com.google.firebase.ml.vision.face.FirebaseVisionFace;
//import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
//import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
//
//import java.util.Arrays;
//import java.util.List;
//
//
//public class FaceAndEmotion extends HomeFragment {
//
//    private static final String TAG = "FaceAndEmotion";
//
//    FirebaseCustomLocalModel localModel;
//    FirebaseModelInterpreter firebaseInterpreter;
//    FirebaseModelInputOutputOptions inputOutputOptions;
//
//    public static Bitmap croppedBmp;
//    public String emotion;
//    public static Rect bounds;
//    public static List<String> label = Arrays.asList("angry", "disgust", "scared", "happy", "sad", "surprised", "neutral");
//
//    public FaceAndEmotion() {
////        imageFromBitmap();
////        configureLocalModelSource();
//
//        localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("converted_model.tflite").build();
//        try {
//            firebaseInterpreter = createInterpreter(localModel);
//        } catch (FirebaseMLException e) {
//            e.printStackTrace();
//        }
//        try {
//            inputOutputOptions = createInputOutputOptions();
//        } catch (FirebaseMLException e) {
//            e.printStackTrace();
//        }
//
//        imageFromBitmap();
//    }
//
//    // Face Model
//    private void imageFromBitmap() {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
//        detectFaces(image);
//    }
//
//    private void detectFaces(FirebaseVisionImage image) {
//        // [START set_detector_options]
//        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
//                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
//                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
//                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
//                .setMinFaceSize(0.15f)
//                .enableTracking()
//                .build();
//
//
//        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
//                .getVisionFaceDetector(options);
//
//        Task<List<FirebaseVisionFace>> result = detector.detectInImage(image)
//                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
//                    @Override
//                    public void onSuccess(List<FirebaseVisionFace> faces) {
//                        // Task completed successfully
//                        for (FirebaseVisionFace face : faces) {
//                            bounds = face.getBoundingBox();
//                            croppedBmp = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.right, bounds.bottom);
//                            return croppedBmp;
//                        }
//                        // ...
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Task failed with an exception
//                        Log.d(TAG, "detectFaces: Failed: " + e.getMessage());
//                        // ...
//                    }
//                });
//
////        croppedBmp = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.right, bounds.bottom);
//
//    }
//
//    public Bitmap getImage() {
//        return croppedBmp;
//    }
//
//    // Emotion Model
//    private void configureLocalModelSource() {
//        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
//                .setAssetFilePath("converted_model.tflite")
//                .build();
//
//    }
//
//
//    private FirebaseModelInterpreter createInterpreter(FirebaseCustomLocalModel localModel) throws FirebaseMLException {
//
//        FirebaseModelInterpreter interpreter = null;
//        try {
//            FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
//            interpreter = FirebaseModelInterpreter.getInstance(options);
//        } catch (FirebaseMLException e) {
//            // ...
//            Log.d(TAG, "createInterpreter: exception: " + e.getMessage());
//        }
//        return interpreter;
//    }
//
//    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {
//
//        FirebaseModelInputOutputOptions inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
//                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 64, 64, 1})
//                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 7})
//                .build();
//        return inputOutputOptions;
//    }
//
//    private float[][][][] bitmapToInputArray() {
//
//        Bitmap bitmap = getImage();
//        bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true);
//        Bitmap bmpGrayscale = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        int batchNum = 0;
//        float[][][][] input = new float[1][64][64][1];
//        for (int x = 0; x < 64; x++) {
//            for (int y = 0; y < 64; y++) {
//                int pixel = bmpGrayscale.getPixel(x, y);
//                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
//                // model. For example, some models might require values to be normalized
//                // to the range [0.0, 1.0] instead.
//                input[batchNum][x][y][0] = (pixel - 127) / 128.0f;
//            }
//        }
//        return input;
//    }
//
//    public void runInference() throws FirebaseMLException {
////        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder().setAssetFilePath("converted_model.tflite").build();
////        FirebaseModelInterpreter firebaseInterpreter = createInterpreter(localModel);
////        imageFromBitmap();
//        float[][][][] input = bitmapToInputArray();
////        FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();
//
//
//        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
//                .add(input)  // add() as many input arrays as your model requires
//                .build();
//
//        firebaseInterpreter.run(inputs, inputOutputOptions)
//                .addOnSuccessListener(new OnSuccessListener<FirebaseModelOutputs>() {
//                    @Override
//                    public void onSuccess(FirebaseModelOutputs result) {
//                        // [START_EXCLUDE]
//                        // [START mlkit_read_result]
//                        float[][] output = result.getOutput(0);
//                        float[] probabilities = output[0];
//                        emotion = useInferenceResult(probabilities);
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Task failed with an exception
//                        Log.d(TAG, "runInference: failed : " + e.getMessage());
//                        // ...
//                    }
//                });
//    }
//
//
//    public String useInferenceResult(float[] probabilities) {
//
//        int max = 0;
//        for (int i = 1; i < probabilities.length; i++) {
//            if (probabilities[i] > probabilities[max]) max = i;
//        }
//        return label.get(max);
//    }
//}
//
