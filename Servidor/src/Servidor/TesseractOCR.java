package Servidor;

import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

public class TesseractOCR {

    private Tesseract tesseract;

    public TesseractOCR() {  // Corregido el constructor
        tesseract = new Tesseract();
        tesseract.setDatapath("D:\\Semestre II\\Algoritmos Y Estructuras De Datos I GR 2\\Tess4J-3.4.8-src\\Tess4J\\tessdata\\");  // Ruta para Windows
    }

    public String convertImageToText(Mat image) {
        try {
            MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);
            byte[] byteArray = buf.toArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

            String result = tesseract.doOCR(ImageIO.read(inputStream));
            return result.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
