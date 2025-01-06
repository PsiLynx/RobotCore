package org.ftc3825.cv

import android.provider.ContactsContract.CommonDataKinds.Im
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.RotatedRect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

class GamePiecePipeLine: OpenCvPipeline() {
    var samples = arrayListOf<RotatedRect>()
    override fun processFrame(input: Mat): Mat {
        samples = arrayListOf()
//        val minHue = 20.0
//        val maxHue = 340.0
//
//        val minSaturation = 150.0
//        val maxSaturation = 255.0
//
//        val minValue = 70.0
//        val maxValue = 255.0
//
//        val lowerRed1 = Scalar(0.0, minSaturation, minValue)
//        val upperRed1 = Scalar(minHue, maxSaturation, maxValue)
//        val lowerRed2 = Scalar(maxHue, minSaturation, minValue)
//        val upperRed2 = Scalar(360.0, maxSaturation, maxValue)
//
//        val hsvMat = Mat()
//        val redMat1 = Mat()
//        val redMat2 = Mat()
//        val allRedMat = Mat()

        val lightMat = Mat()
        val channelMat = Mat()
        val binaryMat = Mat()
        val redMat = Mat()

        Core.multiply(input, Scalar(2.0, 2.0, 2.0), lightMat)

        Imgproc.cvtColor(lightMat, channelMat, Imgproc.COLOR_RGB2YCrCb)
        Core.extractChannel(channelMat, redMat, 1)
        Core.inRange(redMat, Scalar(150.0), Scalar(255.0), binaryMat)

//        Imgproc.cvtColor(lightMat, hsvMat, Imgproc.COLOR_RGB2HSV)
//        Core.inRange(lightMat, lowerRed1, upperRed1, redMat1)
//        Core.inRange(lightMat, lowerRed2, upperRed2, redMat2)
//
//        Core.bitwise_or(redMat1, redMat2, allRedMat)

//        Imgproc.dilate(
//            allRedMat,
//            allRedMat,
//            Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
//        )

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(binaryMat, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > 500) { // Filter blobs by area
                val boundingBox = Imgproc.minAreaRect(MatOfPoint2f(*contour.toArray()))

                val boundingBoxPoints = arrayOf(Point(), Point(), Point(), Point())
                boundingBox.points(boundingBoxPoints) // puts the points into the array ????

                boundingBoxPoints.indices.forEach { i ->
                    Imgproc.line(
                        lightMat,
                        boundingBoxPoints[i],
                        boundingBoxPoints[(i + 1) % boundingBoxPoints.size],
                        Scalar(255.0, 0.0, 0.0) // color
                    )
                }

                samples.add(boundingBox)

                //Imgproc.drawContours(lightMat, listOf(contour), -1, Scalar(0.0, 255.0, 0.0), 2) // Draw green outline
            }
        }

        // Release resources
//        hsvMat.release()
//        redMat1.release()
//        redMat2.release()
//        allRedMat.release()

        // Return the frame with red blobs highlighted
        return lightMat
    }
}
