package org.ftc3825.cv

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.RotatedRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

class GamePiecePipeLine: OpenCvPipeline() {
    var samples = arrayListOf<RotatedRect>()
    override fun processFrame(input: Mat): Mat {
        samples = arrayListOf()

        val lowerRed1 = Scalar(0.0, 150.0, 70.0)
        val upperRed1 = Scalar(15.0, 255.0, 255.0)
        val lowerRed2 = Scalar(155.0, 150.0, 70.0)
        val upperRed2 = Scalar(180.0, 255.0, 255.0)

        val hsvMat = Mat()
        val redMat1 = Mat()
        val redMat2 = Mat()
        val allRedMat = Mat()

        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV)

        Core.inRange(hsvMat, lowerRed1, upperRed1, redMat1)
        Core.inRange(hsvMat, lowerRed2, upperRed2, redMat2)

        Core.bitwise_or(redMat1, redMat2, allRedMat)

        Imgproc.dilate(
            allRedMat,
            allRedMat,
            Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
        )

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(allRedMat, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > 100) { // Filter blobs by area
                Imgproc.drawContours(input, listOf(contour), -1, Scalar(0.0, 255.0, 0.0), 2) // Draw green outline
                val boundingBox = Imgproc.minAreaRect(MatOfPoint2f(*contour.toArray()))

                val boundingBoxPoints = arrayOf(Point(), Point(), Point(), Point())
                boundingBox.points(boundingBoxPoints) // puts the points into the array ????

                boundingBoxPoints.indices.forEach { i ->
                    Imgproc.line(
                        input,
                        boundingBoxPoints[i],
                        boundingBoxPoints[(i + 1) % boundingBoxPoints.size],
                        Scalar(255.0, 0.0, 0.0) // color
                    )
                }

                samples.add(boundingBox)
            }
        }

        // Release resources
        hsvMat.release()
        redMat1.release()
        redMat2.release()
        allRedMat.release()

        // Return the frame with red blobs highlighted
        return input
    }
}
