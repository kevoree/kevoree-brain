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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sun.org.apache.bcel.internal.classfile.Unknown;

/**
 * General form of a RIFF chunk, the standard block of data contained within a RIFF file.<br>
 * Also contains utilities for parsing and writing RIFF chunks, which may be moved at a later date for neatness.<br>
 * <br><img src="RiffChunkParsing.png"/>
 * @author chuck
 *
 */
public abstract class RiffChunk implements Comparable<RiffChunk>
	{
	/**
	 * Simple utility byte array to avoid re-creating byte arrays.<br>
	 * <b>Warning:</b> Not thread-safe.
	 */
	protected static byte [] workChunk4 = new byte[4];
	/**
	 * Simple utility byte array to avoid re-creating byte arrays.<br>
	 * <b>Warning:</b> Not thread-safe.
	 */
	protected static byte [] anotherWorkChunk4 = new byte[4];
	/**
	 * Simple utility byte array to avoid re-creating byte arrays.<br>
	 * <b>Warning:</b> Not thread-safe.
	 */
	protected static byte [] workChunk2 = new byte[2];
	
	protected HashMap<Class,RiffChunk> childMap = new HashMap<Class,RiffChunk>();
	public RiffChunk getChildChunk(Class c){System.out.println(this.getClass().getName()+".getChildChunk("+c.getName()+") returned "+childMap.get(c).getClass().getName());return childMap.get(c);}
	
	/**
	 * Parse the given buffer and output all contained RIFF chunks, which may themselves contain RIFF chunks.
	 * @param fileBuffer	The buffer containing the raw RIFF file bytes
	 * @param parentClass	The class of the RIFFChunk which contains the given byte payload (at least as far as is specified by this chunks 'length' field)
	 * @return	A list of all the child chunks found within the confines of the chunk pointed to by this buffer, which may themselves contain child chunks.
	 * @since Jul 14, 2012
	 */
	public static List<RiffChunk> ParseRiff(ByteBuffer fileBuffer, Class<? extends RiffChunk> parentClass)
		{
		String packageName=parentClass.getPackage().getName();
		String parentClassSimpleName=parentClass.getSimpleName();
		String parentTag=parentClassSimpleName.substring(parentClassSimpleName.length()-4);
		String parentTagSeparator=".";
		
		//The exception: If this is the root of the file.
		if(parentClass==RiffRoot.class)
			{
			parentTag="";
			parentTagSeparator="";
			}
		
		//System.out.println("Parent tag: "+parentTag);
		//System.out.println("package: "+packageName);
		LinkedList<RiffChunk>subChunks = new LinkedList<RiffChunk>();
		//System.out.println("position: "+fileBuffer.position());
		while(fileBuffer.position()<fileBuffer.capacity())
			{
			//Read first 4 bytes
			fileBuffer.get(workChunk4);
			String tag = new String(workChunk4);
			tag=tag.replace(' ', '_');//Replace spaces in tags with underscore
			//Convert tag sequence to String
			System.out.println("Looking for class to match tag: '"+tag+"'");
			final String className=packageName+parentTagSeparator+parentTag.toLowerCase()+".RiffChunk_"+tag;
			try
				{
				//Find class to decode that tag
				Class<RiffChunk> chunkClass = (Class<RiffChunk>)Class.forName(className);
				RiffChunk decoder = (RiffChunk)chunkClass.newInstance();
				decoder.fromData(fileBuffer);
				subChunks.add(decoder);
				}
			catch(ClassNotFoundException e)
				{System.out.println("Failed to find class to decode riff tag: '"+tag+"'");
				System.out.println("was looking for '"+className+"'.\nInserting 'Unrecognized' placeholder.");
				System.out.println("Byte position is "+Integer.toHexString(fileBuffer.position()-4));
				RiffChunk unknown = new Unrecognized();
				unknown.fromData(fileBuffer);
				subChunks.add(unknown);
				}
			catch(IllegalAccessException e){e.printStackTrace();}//TODO Better
			catch(InstantiationException e){e.printStackTrace();}//TODO Better
			}
		//System.out.println("RiffChunk.ParseRiff returning...");
		return subChunks;
		}//end RiffChunk
	
	/**
	 * Parse the given buffer (minding its position) and build this RIFF chunk and field its child chunk hierarchy.<br>
	 * @param fileBuffer	The buffer containing the raw RIFF data.
	 * @since Jul 15, 2012
	 */
	protected final void parseRiff(ByteBuffer fileBuffer)
		{
		List<RiffChunk> children = ParseRiff(fileBuffer,this.getClass());
		for(RiffChunk c:children)
			{childMap.put(c.getClass(), c);}
		}
	
	public static long readUnsignedInt(ByteBuffer fileBuffer)
		{
		fileBuffer.get(workChunk4);
		//Utils.flipEndian(workChunk, anotherWorkChunk);
		long result=0;
		for(int i=0; i<4; i++)
			{result|=((long)(workChunk4[i]&0xFF))<<i*8;}
		return result;
		}
	
	public static int readUnsignedShort(ByteBuffer fileBuffer)
		{
		fileBuffer.get(workChunk2);
		//Utils.flipEndian(workChunk, anotherWorkChunk);
		int result=0;
		for(int i=0; i<2; i++)
			{result|=((int)(workChunk2[i]&0xFF))<<i*8;}
		return result;
		}
	/**
	 * Used for ensuring proper tag order when writing RIFF files because some utilities expect a specific tag order. (lower is earlier)
	 */
	@Override
	public int compareTo(RiffChunk other)
		{
		if(this.getOrderID()>other.getOrderID())return 1;
		else if(this.getOrderID()==other.getOrderID())return 0;
		else return -1;
		}
	/**
	 * Used for ensuring proper tag order when writing RIFF files because some utilities expect a specific tag order. (lower is earlier)
	 */
	public int getOrderID(){return 5;}
	
	
	public abstract void fromData(ByteBuffer fileBuffer);
	/**
	 * DO NOT INVOKE OUTSIDE RIFF CHUNK OBJECTS<br>
	 * Called internally when recursively building a chunk hierarchy from RIFF data.
	 * @param buffer
	 * @since Jul 15, 2012
	 */
	public abstract void _toData(ByteBuffer buffer);
	/**
	 * Called externally when recursively building RIFF data from a hierarchy.
	 * @param buffer
	 * @since Jul 15, 2012
	 */
	public final void toData(ByteBuffer buffer)
		{
		//Write the RIFF name
		String className=this.getClass().getSimpleName();
		String RIFFName = className.substring(className.length()-4).replace('_', ' ');
		System.out.println("RiffChunk.toData Wrote RIFF name: "+RIFFName);
		//System.out.println("Writing RIFF tag to buffer: "+RIFFName);
		try{if(!RIFFName.contentEquals("Root") && !className.contentEquals("Unrecognized"))buffer.put(RIFFName.getBytes());}
		catch(BufferOverflowException e){throw new RuntimeException("Buffer Overflow Exception while pushing RIFF name into buffer of size "+buffer.capacity());}
		_toData(buffer);
		childrenToData(buffer);
		}
	protected void childrenToData(ByteBuffer buffer)
		{
		//System.out.println(this.getClass().getName()+".childrenToData()");
		List<RiffChunk>children = new ArrayList<RiffChunk>(childMap.values());
		Collections.sort(children);
		for(RiffChunk child:children)
			{System.out.println("child: "+child.getClass().getName());child.toData(buffer);}
		}//end childrenToData()
	
	/**
	 * DO NOT INVOKE OUTSIDE RIFFCHUNK OBJECTS<br>
	 * Called internally when pre-calculating the size of a RIFF file. This portion is developer-specified.
	 * @return
	 * @since Jul 15, 2012
	 */
	public abstract int _sizeEstimateInBytes();
	/**
	 * Called externally and recursively when pre-calculating the size of a RIFF file.<br>
	 * @return	The cumulative size estimate of all child chunks and their children, etc. plus the size of this chunk+4 for the RIFF nametag (i.e. RIFF, WAVE, fmt, etc).
	 * @since Jul 15, 2012
	 */
	public final int sizeEstimateInBytes()
		{return childrenSizeEstimateInBytes()+_sizeEstimateInBytes()+4;}//4 is for the tag id
	int childrenSizeEstimateInBytes()
		{
		int accumulator=0;
		for(RiffChunk c:childMap.values()){accumulator+=c.sizeEstimateInBytes();}
		return accumulator;
		}
	
	public void addChildChunk(RiffChunk chunkToAdd)
		{childMap.put(chunkToAdd.getClass(), chunkToAdd);System.out.println(this.getClass().getName()+".addChildChunk("+chunkToAdd.getClass().getName()+")");}
	
	/**
	 *  Prints the class names of all child chunks of this RiffChunk non-recursively.
	 * 
	 * @since Jul 15, 2012
	 */
	public void printChildChunks()
		{
		//System.out.println(this.getClass()+".printChildChunks():");
		if(childMap.isEmpty())System.out.println("	(No child chunks in this object)");
		for(RiffChunk c:childMap.values())
			{System.out.println("		"+c.getClass().getName());}
		}//end printChildChunks()
	}//end RiffChunk
