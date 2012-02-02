
/**
 * simple class for reading and outputting a file to STDOUT
 *
 * went with DataOutputStream as it is made for dumping raw binary data does
 * not screw up with LOCALE information
 */
import java.io.DataOutputStream;
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
    protected static DataOutputStream myDataOutputStream = null;

    public ImageGen() throws FileNotFoundException {
        myFileInputStream = new FileInputStream(myFileResourcePath);
        myDataOutputStream = new DataOutputStream(System.out);
    }

    public void bufferedWriteToStdOut() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        do {
            bytesRead = myFileInputStream.read(buffer, 0, buffer.length);
            myDataOutputStream.write(buffer, 0, bytesRead);
        } while (bytesRead == buffer.length);

        myDataOutputStream.flush();

        if (myFileInputStream != null) {
            myFileInputStream.close();
        }

        if (myDataOutputStream != null) {
            myDataOutputStream.close();
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
