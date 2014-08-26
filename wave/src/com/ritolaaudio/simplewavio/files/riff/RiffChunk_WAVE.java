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
package com.ritolaaudio.simplewavio.files.riff;

import java.nio.ByteBuffer;

import com.ritolaaudio.simplewavio.Utils;
import com.ritolaaudio.simplewavio.files.RiffChunk;
import com.ritolaaudio.simplewavio.files.riff.wave.RiffChunk_data;
import com.ritolaaudio.simplewavio.files.riff.wave.RiffChunk_fmt_;

public class RiffChunk_WAVE extends RiffChunk
	{
	int bytesPerFrame, bytesPerSample;
	RiffChunk_fmt_ format;
	RiffChunk_data data;
	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		parseRiff(fileBuffer);
		format = (RiffChunk_fmt_)this.childMap.get(RiffChunk_fmt_.class);
		data = (RiffChunk_data)this.childMap.get(RiffChunk_data.class);
		bytesPerFrame = format.getAudioChannelCount()+format.getBitsPerSample()/8;
		bytesPerSample = format.getBitsPerSample()/8;
		}
	
	@Override
	public void _toData(ByteBuffer buffer)
		{
		//Nothing to do (simply contains subchunks)
		}

	@Override
	public int _sizeEstimateInBytes()
		{
		return 0;
		}
	
	public RiffChunk_fmt_ getFormatChunk()
		{
		if(format!=null)return format;
		else return (RiffChunk_fmt_)getChildChunk(RiffChunk_fmt_.class);
		}
	
	public RiffChunk_data getDataChunk()
		{
		if(data!=null)return data;
		else return (RiffChunk_data)getChildChunk(RiffChunk_data.class);
		}
	
	public int getNumFrames()
		{
		return data.
				getRawData().
				length/bytesPerFrame;
		}
	
	public float [][] getAudioAsFloats()
		{
		byte [] rawBytes = data.getRawData();
		final int numFrames = getNumFrames();
		final int channelCount=format.getAudioChannelCount();
		final float [][] result = new float[numFrames][];
		int byteIndex=0;
		//System.out.println("numFrames="+numFrames+" numBytes="+rawBytes.length);
		final int emptySpace=64-format.getBitsPerSample();
		for(int frameIndex=0; frameIndex<numFrames; frameIndex++)
			{
			result[frameIndex]= new float[channelCount];
			for(int c=0; c<channelCount; c++)
				{
				long accumulator=0;
				for(int b=0; b<bytesPerSample; b++)
					{
					accumulator+=((long)(rawBytes[byteIndex++]&0xFF))<<(b*8+emptySpace);
					//catch(ArrayIndexOutOfBoundsException e){System.out.println("frame="+frameIndex+" channel="+c);System.exit(0);}
					}
				double sample = ((double)accumulator/(double)Long.MAX_VALUE);
				//if(c==0)System.out.println(sample);
				result[frameIndex][c]=(float)sample;
				}//end for(channel)
			}//end for(frameIndex)
		return result;
		}//end getFloats
	
	public void setAudioFromFloats(float [][]buffer)
		{
		int intValue;
		bytesPerSample=getFormatChunk().getBitsPerSample()/8;
		printChildChunks();
		RiffChunk_data dataChunk=getDataChunk();
		if(dataChunk==null)throw new RuntimeException("data RIFF chunk was null. Make sure to add the data and fmt_ chunks to the WAVE chunk before pushing audio.");
		dataChunk.setRawData(new byte[
		                              buffer.length*
		                              buffer[0].length*
		                              bytesPerSample]);
		byte [] raw = dataChunk.getRawData();
		System.out.println("raw of length "+raw.length);
		int byteIndex=0;
		for(float [] frame:buffer)
			{
			for(float sample:frame)
				{
				intValue=(int)((double)sample*(double)Integer.MAX_VALUE);
				Utils.toByteArray(intValue, bytesPerSample, raw, byteIndex);
				byteIndex+=bytesPerSample;
				}//end for(sample)
			}//end for(frame)
		}//end setAudioFromFloats()

	}//end RiffChunk_WAVE
