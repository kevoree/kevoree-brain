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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class RiffRoot extends RiffChunk
	{

	@Override
	public void fromData(ByteBuffer fileBuffer)
		{
		// TODO Auto-generated method stub

		}

	public static List<RiffChunk> readRIFF(File input) throws FileNotFoundException,IOException
		{
		FileChannel fc = new FileInputStream(input).getChannel();
		ByteBuffer fileBuffer = ByteBuffer.allocateDirect((int)fc.size());//Limited to 3.5GB files
		fc.read(fileBuffer);
		fileBuffer.rewind();
		//fc.close();
		return RiffChunk.ParseRiff(fileBuffer,RiffRoot.class);
		}

	@Override
	public void _toData(ByteBuffer buffer)
		{
		//Nothing to do
		}//end toData

	@Override
	public int _sizeEstimateInBytes()
		{
		// TODO Auto-generated method stub
		return 0;
		}
	}//end RiffRoot
