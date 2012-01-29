
/**
 * simple class for reading and outputting a file to STDOUT
 */
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jason Zerbe
 */
public class ImageGen {

    protected static String myFileResourcePath = "webdir/input.jpeg";
    protected static FileInputStream myFileInputStream = null;
    protected static BufferedOutputStream myBufferedOutputStream = null;

    public ImageGen() throws FileNotFoundException {
        myFileInputStream = new FileInputStream(myFileResourcePath);
        myBufferedOutputStream = new BufferedOutputStream(System.out);
    }

    public void bufferedWriteToStdOut() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        do {
            bytesRead = myFileInputStream.read(buffer, 0, buffer.length);
            myBufferedOutputStream.write(buffer, 0, bytesRead);
        } while (bytesRead == buffer.length);

        myBufferedOutputStream.flush();

        if (myFileInputStream != null) {
            myFileInputStream.close();
        }

        if (myBufferedOutputStream != null) {
            myBufferedOutputStream.close();
        }
    }

    public static void main(String args[]) {
        ImageGen gen = null;
        try {
            gen = new ImageGen();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            gen.bufferedWriteToStdOut();
        } catch (IOException ex) {
            Logger.getLogger(ImageGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
