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

import com.ritolaaudio.simplewavio.files.RiffChunk;

/**
 * Whatcha gonna do with all that JUNK, all that JUNK inside your chunk?<br>
 * The JUNK chunk is typically non-data i.e. all-zeroes, used for block alignment in some files, like with 2048 blocksize in CDs.<br>
 * In case it is not empty, its contents can be read. Who knows what fun little goodies could be in there...
 * @author chuck
 *
 */

public class RiffChunk_JUNK extends RiffChunk
	{
	byte [] junkData;
	
	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		int length = (int)RiffChunk.readUnsignedInt(fileBuffer);
		junkData = new byte[length];
		fileBuffer.get(junkData);
		}

	@Override
	public void _toData(ByteBuffer buffer)
		{
		buffer.putInt(junkData.length);
		buffer.put(junkData);
		}

	@Override
	public int _sizeEstimateInBytes()
		{
		return 4+junkData.length;
		}

	/**
	 * JUNK data should be non-data but it doesn't have to be.
	 * @return the junkData
	 */
	public byte[] getJunkData()
		{
		return junkData;
		}

	/**
	 * JUNK data should be non-data but it doesn't have to be.
	 * @param junkData the junkData to set
	 */
	public void setJunkData(byte[] junkData)
		{
		this.junkData = junkData;
		}

	}//end RiffChunk_JUNK
