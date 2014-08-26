/*******************************************************************************
 * SimpleWAVIO - A straightforward library for loading and saving .WAV (RIFF) files as numerical arrays.
 * Copyright (C) 2012,2013  Chuck Ritola
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.ritolaaudio.simplewavio;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.ritolaaudio.simplewavio.files.RiffChunk;
import com.ritolaaudio.simplewavio.files.RiffChunk_RIFF;
import com.ritolaaudio.simplewavio.files.RiffRoot;
import com.ritolaaudio.simplewavio.files.riff.RiffChunk_WAVE;
import com.ritolaaudio.simplewavio.files.riff.wave.RiffChunk_data;
import com.ritolaaudio.simplewavio.files.riff.wave.RiffChunk_fmt_;

/**
 * General utilities for reading and writing WAV (RIFF) files.
 * @author chuck
 *
 */
public class Utils
	{
	
	/*
	public static void floatsToWAV(float [][] buffer, final File destFile, float sampleRate)
		{
		try
			{
			final int numChannels = buffer[0].length;
			final long lengthInSamples = buffer.length;
			final int sampleSizeInBytes=2;
			//For re-routing our byte-writing to appear as an InputStream
			final PipedOutputStream pos = new PipedOutputStream();
			final PipedInputStream pis = new PipedInputStream(pos);
			// 44.1khz, 16bit, 1 channel (mono), signed, !bigEndian
			final AudioInputStream audioInputStream = new AudioInputStream(pis, new AudioFormat(sampleRate, 16, numChannels, true, false), lengthInSamples);
			
			//This is the second thread needed for the PipedInputStream/AudioInputStream
			new Thread(){public void run(){try
			//Forwards the AudioInputStream to the WAV file, printing how many bytes were written after exiting.
				{System.out.println("Secondary Thread: AudioSystem.write() Wrote "+
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, destFile)
						 +" bytes and exited. AudioSystem.write() thread exiting.");}
			catch(Exception e){e.printStackTrace();}}}.start();
			
			final double scaler = (Math.pow(2, sampleSizeInBytes*8-1));
			
			//Write 1 second worth of silence.
			for(int s=0; s<lengthInSamples; s++)
				{
				if(buffer[s].length!=numChannels)throw new RuntimeException("Found a sample at position "+s+" which has "+buffer[s].length+" channels. Expected "+numChannels+".");
				for(int c=0; c<numChannels; c++)
					{
					if(buffer[s][c]>=1)buffer[s][c]=.999999f;
					else if(buffer[s][c]<=-1)buffer[s][c]=-.999999f;
					final int rawValue = (int)(scaler*((double)buffer[s][c]));
					pos.write(toByteArray(rawValue,sampleSizeInBytes));
					}//end for(channels)
				}//end for(samples)
			}//end try{}
		catch(IOException e){if(e.getMessage().contains("dead")){throw new RuntimeException("PipedInputStream's read end is dead. This is a bizarre, known bug. Wait a few minutes and try again.");}else{throw new RuntimeException("PipedOutputStream  threw an IOException: "+e.getMessage());}}
		}//end floatsToWAV(...)
	*/
	
/*	
	public static final byte[] toByteArray(int value,int numBytes)
		{
		byte [] result = new byte[numBytes];
		for(int i=0; i<numBytes; i++)
			{
			result[i]=(byte)(value >>> (i*8) & 0xff);
			}
		return result;
		}//end toByteArray(...)
*/
	/**
	 * Converts a signed int into an array of bytes and writes it to a byte array t the given offset.
	 * @param value		The integral value to byte-ify.
	 * @param numBytes	The number of bytes desired to describe this value (up to 4)
	 * @param dest		The byte array to which to write said bytes.
	 * @param off		The offset index within said byte array from which to start writing said bytes.
	 * @since Jul 14, 2012
	 */
	public static final void toByteArray(int value,int numBytes, byte [] dest, int off)
		{
		for(int i=0; i<numBytes; i++)
			{
			dest[i+off]=(byte)(value >>> ((i+2)*8) & 0xff);
			}
		}//end toByteArray(...)
		
	/*
	public static final void toByteArray(int value,int numBytes, byte [] dest, int off)
		{
		for(int i=off; i<numBytes+off; i++)
			{
			dest[i]=(byte)(value >>> (i*8) & 0xff);
			}
		}//end toByteArray(...)
	*/
	
	/*
	public static final float [][] WAVToFloats(File srcFile) throws FileNotFoundException, UnsupportedAudioFileException, IOException
		{
		AudioFormat format = AudioSystem.getAudioFileFormat(srcFile).getFormat();
		AudioInputStream ais = AudioSystem.getAudioInputStream(srcFile);
		
		final long numFrames = ais.getFrameLength();
		final int numChannels = format.getChannels();
		final float [][] result = new float[(int)numFrames][numChannels];
		final int sampleSizeInBytes = format.getSampleSizeInBits()/8;
		final byte [] workFrame = new byte[sampleSizeInBytes*numChannels];
		final byte [] workSample = new byte[sampleSizeInBytes];
		final byte [] workSample2 = new byte[sampleSizeInBytes];//for endian conversion
		//System.out.println("work array size: "+workSample.length);
		final double scaler = (Math.pow(2, sampleSizeInBytes*8-1));
		
		if(ais.getFormat().isBigEndian())
			{
			for(int f=0; f<numFrames; f++)
				{
				ais.read(workFrame);
				for(int c=0; c<numChannels; c++)
					{
					System.arraycopy(workFrame, c*sampleSizeInBytes, workSample, 0, sampleSizeInBytes);
					result[f][c]=(float)(((double)(new BigInteger(workSample).intValue()))/scaler);
					}//end for()
				}//end for(numFrames)
			}
		else
			{
			for(int f=0; f<numFrames; f++)
				{
				ais.read(workFrame);
				for(int c=0; c<numChannels; c++)
					{
					System.arraycopy(workFrame, c*sampleSizeInBytes, workSample, 0, sampleSizeInBytes);
					//flip endian
					flipEndian(workSample,workSample2);
					result[f][c]=(float)(((double)(new BigInteger(workSample2).intValue()))/scaler);
					//System.out.println(workSample[0]);
					}//end for()
				}//end for(numFrames)			
			}//end littleEndian
		ais.close();
		return result;
		}//end WAVToFloats(...)
	*/
	
	/**
	 * Reverse the order of a byte array and write that reordered array to the given output. Intra-byte bit order is unchanged.
	 * @param original	input array to reverse (does not get changed)
	 * @param output	output array for results of byte order reversal.
	 * @since Jul 14, 2012
	 */
	public static final void flipEndian(byte [] original, byte [] output)
		{
		for(int i=0; i<original.length; i++)
			{output[(original.length-1)-i]=original[i];}
		}//end flipEndian()
	
	/**
	 * Open a .WAV (RIFF) file and return its contents as a 2-dimensional array of floats.
	 * @param srcFile
	 * @return	A 2-dimensional float array, where the first dimension represents single-sample-wide frames, and the second dimension 
	 * representing a single-sample channel of that frame. i.e. a mono clip 44000 samples long would have the dimensions [44000][1]
	 * @throws java.io.FileNotFoundException	Failure to find the file specified; read aborted.
	 * @throws javax.sound.sampled.UnsupportedAudioFileException	Java Media Framework could not recognize the format of the specified file.
	 * @throws java.io.IOException		Some general IO error occurred while trying to process the file. Probably a bug in SimpleWAVIO.
	 * @since Jul 11, 2012
	 */
	public static float [][] WAVToFloats(File input) throws IOException
		{
		RiffChunk riff = RiffRoot.readRIFF(input).get(0);
		return ((RiffChunk_WAVE)riff.getChildChunk(RiffChunk_WAVE.class)).getAudioAsFloats();
		}
	
	
	/**
	 * Write an array of float[] frames as a RIFF (.WAV) file, expecting floats within the range [-1,1]. If they are outside of this range they will be subjected
	 * to a floor or ceiling (hard clipped) to fit within the range [-1,1].
	 * @param buffer		2-dimensional float array, where the first dimension represents single sample-wide frames, and the second dimension 
	 * representing a single-sample channel of that frame. i.e. a mono clip 44000 samples long would have the dimensions [44000][1]
	 * @param destFile		Destination File. Does not need to exist beforehand.
	 * @param sampleRate
	 * @since Jul 11, 2012
	 */
	public static void floatsToWAV(float [][] buffer, File destFile, float sampleRate) throws IOException
		{
		RiffRoot root = new RiffRoot();
		RiffChunk_RIFF riffChunk = new RiffChunk_RIFF();
		RiffChunk_WAVE waveChunk = new RiffChunk_WAVE();
		RiffChunk_data dataChunk = new RiffChunk_data();
		RiffChunk_fmt_ fmtChunk = new RiffChunk_fmt_();
		fmtChunk.setAudioChannelCount(buffer[0].length);
		fmtChunk.setAudioFormatCode(1);// 1 for PCM
		fmtChunk.setBitsPerSample(16);
		fmtChunk.setBlockAlign(2*buffer[0].length);
		fmtChunk.setByteRate((long)sampleRate*2*fmtChunk.getAudioChannelCount());
		fmtChunk.setSampleRate((long)sampleRate);
		fmtChunk.setSize(16);// 16 for PCM
		
		waveChunk.addChildChunk(dataChunk);
		waveChunk.addChildChunk(fmtChunk);
		riffChunk.addChildChunk(waveChunk);
		waveChunk.setAudioFromFloats(buffer);
		root.addChildChunk(riffChunk);
		FileOutputStream os = new FileOutputStream(destFile);
		FileChannel fc = os.getChannel();
		ByteBuffer fileBuffer = ByteBuffer.allocateDirect(root.sizeEstimateInBytes());//Limited to 3.5GB files
		fileBuffer.order(ByteOrder.LITTLE_ENDIAN);
		fileBuffer.rewind();
		try {
			root.toData(fileBuffer);
			}catch(Exception e){e.printStackTrace();}
		fileBuffer.rewind();
		fc.write(fileBuffer);
		fc.close();
		os.flush();
		os.close();
		System.out.println("Done.");
		}//end floatsToWAV
	}//end Utils
