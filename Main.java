import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Main {
    public static void main(String[] args) {
        Tesseract tesseract = new Tesseract();
        try {
            tesseract.setDatapath("D:/Tesseract/tessdata");
            String text = tesseract.doOCR(new File("D:/Tesseract/Image.tiff"));
            System.out.print(text);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }

}