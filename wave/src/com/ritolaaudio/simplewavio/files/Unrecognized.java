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
package com.ritolaaudio.simplewavio.files;
import java.nio.ByteBuffer;

public class Unrecognized extends RiffChunk
	{
	byte [] unrecognizedRawData;
	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		//Get the block size
		int blockSize=(int)RiffChunk.readUnsignedInt(fileBuffer);
		System.out.println("Unrecognized chunk is of size "+blockSize);
		unrecognizedRawData = new byte[(int)blockSize];
		fileBuffer.get(unrecognizedRawData);
		}

	@Override
	public void _toData(ByteBuffer buffer)
		{
		//Do nothing
		}

	@Override
	public int _sizeEstimateInBytes()
		{
		// Not relevant
		return 0;
		}

	/**
	 * @return the unrecognizedRawData
	 */
	public byte[] getUnrecognizedRawData()
		{
		return unrecognizedRawData;
		}

	/**
	 * @param unrecognizedRawData the unrecognizedRawData to set
	 */
	public void setUnrecognizedRawData(byte[] unrecognizedRawData)
		{
		this.unrecognizedRawData = unrecognizedRawData;
		}

	}
