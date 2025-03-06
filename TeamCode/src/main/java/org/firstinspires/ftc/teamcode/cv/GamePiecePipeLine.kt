package org.firstinspires.ftc.teamcode.cv

import com.acmerobotics.dashboard.config.Config
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.RotatedRect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline
import org.firstinspires.ftc.teamcode.cv.piplineConfig.brightnessMultiplier
import org.firstinspires.ftc.teamcode.cv.piplineConfig.matToShow
import org.firstinspires.ftc.teamcode.cv.piplineConfig.mats.*
import org.firstinspires.ftc.teamcode.cv.piplineConfig.minRed
import org.firstinspires.ftc.teamcode.cv.piplineConfig.maxRed
import org.firstinspires.ftc.teamcode.cv.piplineConfig.speedy
@Config object piplineConfig{
    @JvmField var brightnessMultiplier = 3.0
    @JvmField var matToShow = light
    @JvmField var minRed = 200.0
    @JvmField var maxRed = 255.0
    @JvmField var speedy = false;
    enum class mats {
        light, crcb, binary, red
    }
}

class GamePiecePipeLine: OpenCvPipeline() {
    var samples = listOf<RotatedRect>()
    override fun processFrame(input: Mat): Mat {
        val lightMat = Mat()
        val crcbMat = Mat()
        val binaryMat = Mat()
        val redMat = Mat()
        val outputMat = Mat()

        Core.multiply(
            input,
            Scalar(
                brightnessMultiplier,
                brightnessMultiplier,
                brightnessMultiplier,
            ),
            lightMat
        )

        Imgproc.cvtColor(lightMat, crcbMat, Imgproc.COLOR_RGB2YCrCb)
        Core.extractChannel(crcbMat, redMat, 1)
        Core.inRange(redMat, Scalar(minRed), Scalar(maxRed), binaryMat)

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            binaryMat,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        val newSamples = arrayListOf<RotatedRect>()
        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > 500) { // Filter blobs by area
                val boundingBox = Imgproc.minAreaRect(MatOfPoint2f(*contour.toArray()))

                val boundingBoxPoints = arrayOf(Point(), Point(), Point(), Point())
                if(!speedy) {
                    boundingBox.points(boundingBoxPoints) // puts the points into the array ????

                    boundingBoxPoints.indices.forEach { i ->
                        Imgproc.line(
                            lightMat,
                            boundingBoxPoints[i],
                            boundingBoxPoints[(i + 1) % boundingBoxPoints.size],
                            Scalar(255.0, 0.0, 0.0) // color
                        )
                    }
                }

                newSamples.add(boundingBox)

                //Imgproc.drawContours(lightMat, listOf(contour), -1, Scalar(0.0, 255.0, 0.0), 2) // Draw green outline
            }
            samples = newSamples.map { it }
        }

        when(matToShow){
            light -> lightMat.copyTo(outputMat)
            crcb -> crcbMat.copyTo(outputMat)
            binary -> binaryMat.copyTo(outputMat)
            red -> redMat.copyTo(outputMat)
        }

        lightMat.release()
        crcbMat.release()
        binaryMat.release()
        redMat.release()

        return outputMat
    }
}
