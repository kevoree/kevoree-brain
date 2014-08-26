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
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.ritolaaudio.simplewavio.files.RiffChunk;

public class RiffChunk_data extends RiffChunk
	{
	byte [] rawData;
	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		long audioDataSizeInBytes=readUnsignedInt(fileBuffer);
		System.out.println("audio data size in bytes: "+audioDataSizeInBytes);
		rawData = new byte[(int)audioDataSizeInBytes];
		fileBuffer.get(rawData);
		}//end initialize(...)
	
	@Override
	public void _toData(ByteBuffer buffer)
		{
		buffer.putInt(rawData.length);
		try{buffer.put(rawData);}
		catch(BufferOverflowException e)
			{
			e.printStackTrace();
			System.out.println("tried to put "+rawData.length+" into buffer of remaining "+buffer.remaining());
			}
		}//end _toData
	@Override
	public int _sizeEstimateInBytes()
		{
		return /*4+*/rawData.length;//data size indicator plus data
		}
	
	/**
	 * @return the rawData
	 */
	public byte[] getRawData()
		{
		return rawData;
		}
	/**
	 * @param rawData the rawData to set
	 */
	public void setRawData(byte[] rawData)
		{
		this.rawData = rawData;
		System.out.println("Set raw data to an array of length "+rawData.length);
		}
	}//end RiffChunk_data
