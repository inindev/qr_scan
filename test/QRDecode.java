import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRDecode {
    static class SimpleLuminanceSource extends LuminanceSource {
        private final byte[] luminances;

        public SimpleLuminanceSource(BufferedImage image) {
            super(image.getWidth(), image.getHeight());
            this.luminances = new byte[image.getWidth() * image.getHeight()];

            // convert rgb to luminance (grayscale)
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;
                    // simple luminance formula: 0.3R + 0.59G + 0.11B
                    luminances[y * getWidth() + x] = (byte) ((r * 77 + g * 150 + b * 28) >> 8);
                }
            }
        }

        @Override
        public byte[] getRow(int y, byte[] row) {
            if (row == null || row.length < getWidth()) {
                row = new byte[getWidth()];
            }
            System.arraycopy(luminances, y * getWidth(), row, 0, getWidth());
            return row;
        }

        @Override
        public byte[] getMatrix() {
            return luminances;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java QRDecode <image_path>");
            System.exit(1);
        }

        String imagePath = args[0];
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            if (image == null) {
                System.err.println("Could not read image: " + imagePath);
                System.exit(1);
            }

            LuminanceSource source = new SimpleLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(bitmap);

            String decodedText = result.getText();
            System.out.println("Decoded text: " + decodedText);

        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error decoding QR: " + e.getMessage());
        }
    }
}
