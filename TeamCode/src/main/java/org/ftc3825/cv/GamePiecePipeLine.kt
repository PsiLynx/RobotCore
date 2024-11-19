package org.ftc3825.cv

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

class GamePiecePipeLine: OpenCvPipeline() {
    override fun processFrame(input: Mat): Mat {
        val lowerRed1 = Scalar(0.0, 150.0, 50.0)
        val upperRed1 = Scalar(10.0, 255.0, 255.0)
        val lowerRed2 = Scalar(160.0, 150.0, 50.0)
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
                // Optionally, draw bounding box
                val rect = Imgproc.boundingRect(contour)
                Imgproc.rectangle(input, rect, Scalar(255.0, 0.0, 0.0), 2) // Draw red bounding box
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
