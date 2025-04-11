import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.*;
import org.openftc.easyopencv.OpenCvPipeline;

public class blue extends OpenCvPipeline {

	private Mat ycrcbMat = new Mat();

	public Scalar lowerYCrCb = new Scalar(0.0, 0.0, 142.0, 0.0);
	public Scalar upperYCrCb = new Scalar(255.0, 255.0, 255.0, 0.0);
	private Mat ycrcbBinaryMat = new Mat();

	public int erodeValue = ((int) (4));
	public int dilateValue = ((int) (4));
	private Mat element = null;
	private Mat ycrcbBinaryMatErodedDilated = new Mat();

	private ArrayList<MatOfPoint> contours = new ArrayList<>();
	private Mat hierarchy = new Mat();

	private MatOfPoint2f contours2f = new MatOfPoint2f();
	private ArrayList<RotatedRect> contoursRotRects = new ArrayList<>();

	public Scalar lineColor = new Scalar(0.0, 255.0, 0.0, 0.0);
	public int lineThickness = 3;

	private Mat inputRotRects = new Mat();

	@Override
	public Mat processFrame(Mat input) {
		Imgproc.cvtColor(input, ycrcbMat, Imgproc.COLOR_RGB2YCrCb);

		Core.inRange(ycrcbMat, lowerYCrCb, upperYCrCb, ycrcbBinaryMat);

		ycrcbBinaryMat.copyTo(ycrcbBinaryMatErodedDilated);
		if(erodeValue > 0) {
			this.element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeValue, erodeValue));
			Imgproc.erode(ycrcbBinaryMatErodedDilated, ycrcbBinaryMatErodedDilated, element);

			element.release();
		}

		if(dilateValue > 0) {
			this.element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilateValue, dilateValue));
			Imgproc.dilate(ycrcbBinaryMatErodedDilated, ycrcbBinaryMatErodedDilated, element);

			element.release();
		}

		contours.clear();
		hierarchy.release();
		Imgproc.findContours(ycrcbBinaryMatErodedDilated, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		contoursRotRects.clear();
		for(MatOfPoint points : contours) {
			contours2f.release();
			points.convertTo(contours2f, CvType.CV_32F);

			contoursRotRects.add(Imgproc.minAreaRect(contours2f));
		}

		input.copyTo(inputRotRects);
		for(RotatedRect rect : contoursRotRects) {
			if(rect != null) {
				Point[] rectPoints = new Point[4];
				rect.points(rectPoints);
				MatOfPoint matOfPoint = new MatOfPoint(rectPoints);

				Imgproc.polylines(inputRotRects, Collections.singletonList(matOfPoint), true, lineColor, lineThickness);
			}
		}

		return inputRotRects;
	}
}

