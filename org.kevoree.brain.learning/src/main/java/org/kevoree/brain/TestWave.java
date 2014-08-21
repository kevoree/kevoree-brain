package org.kevoree.brain;

import org.kevoree.brain.util.PolynomialCompressor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

/**
 * Created by assaa_000 on 8/21/2014.
 */
public class TestWave {
    public static void main(String[] arg) {
        int totalFramesRead = 0;
        File fileIn = new File("D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\track.wav");
        int timeOrigine=0;
        int degradeFactor=100;
        double toleratedError=10;
        int maxDegree=5;
        PolynomialCompressor pt= new PolynomialCompressor(timeOrigine,degradeFactor,toleratedError,maxDegree);

        try {

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(fileIn);
            int bytesPerFrame =
                    audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }
            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                // Try to read numBytes bytes from the file.
                while ((numBytesRead =
                        audioInputStream.read(audioBytes)) != -1) {
                    // Calculate the number of frames actually read.
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;
                    int i=0;
                    // Here, do something useful with the audio data that's
                    // now in the audioBytes array...
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
