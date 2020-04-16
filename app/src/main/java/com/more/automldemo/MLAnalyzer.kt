package com.more.automldemo

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions

class MLAnalyzer(val activity: CameraActivity) : ImageAnalysis.Analyzer {
    private val labeler : FirebaseVisionImageLabeler
    private val objectDetector: FirebaseVisionObjectDetector

    init {
        val localModel = FirebaseAutoMLLocalModel.Builder()
            .setAssetFilePath("swmodel/manifest.json")
            .build()

        val options = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(.5f)
            .build()

        val objectDetectorOptions = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
            .enableClassification()  // Optional
            .build()

        objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(objectDetectorOptions)
        labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)
    }

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        Log.i(MLAnalyzer::class.java.simpleName, "Analyzing")
        val mediaImage = image?.image
        val imageRotation = degreesToFirebaseRotation(rotationDegrees)
        if (mediaImage != null) {
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            labeler.processImage(image).addOnSuccessListener {
                val label = it.maxBy { it.confidence }
                Log.i(MLAnalyzer::class.java.simpleName, it.toString())
                Log.i(MLAnalyzer::class.java.simpleName,"Label is ${label?.text} :: ${label?.confidence}")

            }

            //Uncomment for object detection.
            /*
            objectDetector.processImage(image).addOnSuccessListener { detectedObjects ->
                for (obj in detectedObjects) {
                    val id = obj.trackingId       // A number that identifies the object across images
                    val bounds = obj.boundingBox  // The object's position in the image

                    // If classification was enabled:
                    val category = obj.classificationCategory
                    val confidence = obj.classificationConfidence
                    Log.i(MLAnalyzer::class.java.simpleName, "ID:${id}, B:${bounds}, CAT:${category}, CO:${confidence}, IS:${image.bitmap.width}, ${image.bitmap.height}")

                    activity.update(image, detectedObjects)
                }
            }
            */
        }
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

}