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
package com.ritolaaudio.simplewavio.files.riff.wave;
import java.nio.ByteBuffer;

import com.ritolaaudio.simplewavio.files.RiffChunk;

public class RiffChunk_fmt_ extends RiffChunk
	{
	private long size=-1, sampleRate=44100, byteRate=2*2*44100;
	private int audioFormatCode=1, audioChannelCount=2,blockAlign=4,bitsPerSample=16;
	
	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		size=RiffChunk.readUnsignedInt(fileBuffer);
		System.out.println("size returned "+size);
		
		audioFormatCode = RiffChunk.readUnsignedShort(fileBuffer);
		System.out.println("audioFormatCode returned "+audioFormatCode);
		
		audioChannelCount = RiffChunk.readUnsignedShort(fileBuffer);
		System.out.println("audioChannelCount returned "+audioChannelCount);
		
		sampleRate = RiffChunk.readUnsignedInt(fileBuffer);
		byteRate = RiffChunk.readUnsignedInt(fileBuffer);
		
		System.out.println("sampleRate "+sampleRate+" byterate="+byteRate);
		
		blockAlign = RiffChunk.readUnsignedShort(fileBuffer);
		bitsPerSample = RiffChunk.readUnsignedShort(fileBuffer);
		
		System.out.println("blockAlign="+blockAlign+" bitsPerSample="+bitsPerSample);
		//TODO: (2 bytes) extraparamSize + extra params (not present in PCM)
		}//end initialize(...)
	
	@Override
	public void _toData(ByteBuffer buffer)
		{
		buffer.putInt((int)size);
		buffer.putShort((short)audioFormatCode);
		buffer.putShort((short)audioChannelCount);
		buffer.putInt((int)sampleRate);
		buffer.putInt((int)byteRate);
		buffer.putShort((short)blockAlign);
		buffer.putShort((short)bitsPerSample);
		}

	@Override
	public int getOrderID()
		{
		return 0;
		}
	
	@Override
	public int _sizeEstimateInBytes()
		{
		return 4+2+2+4+4+2+2;
		}
	
	/**
	 * @return the size of this subchunk (internal use) minus the 4 bytes used for this variable
	 */
	public long getChunkSize()
		{
		return size;
		}

	/**
	 * Not audio-related: Refers to the RIFF subchunk data size.
	 * @param set the size of this RIFF subchunk, not including the 4 bytes used for this variable.
	 */
	public void setSize(long size)
		{
		this.size = size;
		}

	/**
	 * @return the sampleRate
	 */
	public long getSampleRate()
		{
		return sampleRate;
		}

	/**
	 * @param sampleRate the sampleRate to set
	 */
	public void setSampleRate(long sampleRate)
		{
		this.sampleRate = sampleRate;
		}

	/**
	 * @return the byteRate (bytes per second)
	 */
	public long getByteRate()
		{
		return byteRate;
		}

	/**
	 * @param byteRate the byteRate to set
	 */
	public void setByteRate(long byteRate)
		{
		this.byteRate = byteRate;
		}

	/**
	 * @return the audioFormatCode
	 */
	public int getAudioFormatCode()
		{
		return audioFormatCode;
		}

	/**
	 * @param audioFormatCode the audioFormatCode to set
	 */
	public void setAudioFormatCode(int audioFormatCode)
		{
		this.audioFormatCode = audioFormatCode;
		}

	/**
	 * @return the audioChannelCount
	 */
	public int getAudioChannelCount()
		{
		return audioChannelCount;
		}

	/**
	 * @param audioChannelCount the audioChannelCount to set
	 */
	public void setAudioChannelCount(int audioChannelCount)
		{
		this.audioChannelCount = audioChannelCount;
		}

	/**
	 * @return the blockAlign
	 */
	public int getBlockAlign()
		{
		return blockAlign;
		}

	/**
	 * @param blockAlign the blockAlign to set
	 */
	public void setBlockAlign(int blockAlign)
		{
		this.blockAlign = blockAlign;
		}

	/**
	 * @return the bitsPerSample
	 */
	public int getBitsPerSample()
		{
		return bitsPerSample;
		}

	/**
	 * @param bitsPerSample the bitsPerSample to set
	 */
	public void setBitsPerSample(int bitsPerSample)
		{
		this.bitsPerSample = bitsPerSample;
		}
	}//end RiffChunk_fmt_
