import com.ritolaaudio.simplewavio.Utils;
import org.kevoree.brain.util.PolynomialCompressor;
import org.kevoree.brain.util.Prioritization;
import org.kevoree.brain.util.polynomialRepresentation.PolynomialModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by duke on 8/21/14.
 */
public class HelloWave2 {

    public static void main(String[] args) throws IOException {
        double maxerr = 0;

        double temp = 0;

        int timeOrigine = 0;
        int degradeFactor = 100;
        double toleratedError = 0.4;
        int maxDegree = 10;
        PolynomialModel pm = new PolynomialModel(degradeFactor,toleratedError,maxDegree);
        float[][] inputAudio = new float[0][];
        try {
            inputAudio = Utils.WAVToFloats(new File("D:\\workspace\\Github\\kevoree-brain\\wave\\Mogo.wav"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int sizetoRead = inputAudio.length;
        int time = 0;
        for (int i = 0; i < sizetoRead; i++) {
            float[] frame = inputAudio[i];
            if(frame.length > 1){
                frame[1] = 0;
            }
            pm.feed(time, frame[0]);
            time++;
        }//end for(frames)
        pm.finalSave();

        float[][] cloned = new float[sizetoRead][inputAudio[0].length];
        float[][] original = new float[sizetoRead][inputAudio[0].length];
        float[][] error = new float[sizetoRead][inputAudio[0].length];
        time = 0;
        int ind = 0;



        for (int i = 0; i < sizetoRead; i++) {
            float[] frame = inputAudio[i];
            float[] errfile= new float[inputAudio[i].length];

            for(int j=0; j<frame.length;j++){
                original[i][j] = frame[j];
            }


            if(frame.length > 1){
                frame[1] = 0;
            }
            try {

                double h = pm.reconstruct(time);
                double err = Math.abs(h - frame[0]);
                if (err > maxerr) {
                    maxerr = err;
                }
                temp = temp + err;

                frame[0] = new Float(h);
                errfile[0]=new Float(err);

                cloned[i] = frame;
                error[i]=errfile;
            } catch (Exception e) {
                e.printStackTrace();
            }
            time++;
        }
        Utils.floatsToWAV(cloned, new File("output.wav"), 44100);
        Utils.floatsToWAV(original, new File("original.wav"), 44100);
        Utils.floatsToWAV(error, new File("error.wav"), 44100);
          pm.displayStatistics();



    }

}
