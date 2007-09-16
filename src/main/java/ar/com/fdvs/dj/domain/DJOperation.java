/*
 * Dynamic Jasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2007  FDV Solutions (http://www.fdvsolutions.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 *
 * License as published by the Free Software Foundation; either
 *
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 */

package ar.com.fdvs.dj.domain;

/**
 * Operations that can be shown as a group variable.</br>
 * </br>
 * @see DJOperationColumn
 */
public class DJOperation {

	public static DJOperation AVERAGE = new DJOperation( (byte) 3);
	public static DJOperation COUNT = new DJOperation( (byte) 1);
	public static DJOperation FIRST = new DJOperation((byte) 9);
	public static DJOperation HIGHEST = new DJOperation((byte) 5);
	public static DJOperation LOWEST = new DJOperation((byte) 4);
	public static DJOperation NOTHING = new DJOperation((byte) 0);
	public static DJOperation STANDARD_DEVIATION = new DJOperation((byte) 6);
	public static DJOperation SUM = new DJOperation((byte)2);
	public static DJOperation SYSTEM = new DJOperation((byte) 8);
	public static DJOperation VARIANCE = new DJOperation((byte) 7);

	private byte value;

	private DJOperation(byte value){
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

}
