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

public class RiffChunk_RIFF extends RiffChunk
	{
	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		//System.out.println("Found RIFF identifier chunk of size "+dataLength);
		long payloadSize = readUnsignedInt(fileBuffer);
		parseRiff(fileBuffer);
		}

	@Override
	public void _toData(ByteBuffer buffer)
		{
		//Payload size (not including this)
		buffer.putInt(this.childrenSizeEstimateInBytes());
		}

	@Override
	public int _sizeEstimateInBytes()
		{
		return 4;
		}
	}//end RiffChunk_RIFF
