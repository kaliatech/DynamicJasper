/*
 * DynamicJasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2008  FDV Solutions (http://www.fdvsolutions.com)
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

package ar.com.fdvs.dj.core.layout;

import java.awt.Color;

public abstract class CrossTabColorShema {

	protected Color[][] colors;

	public static Color[][] createSchema(int schema, int numCols, int numRows){

		CrossTabColorShema generator = null;

		if (schema == 0)
			generator = new Schema0();
		else if (schema == 1)
			generator = new Schema1();
		else if (schema == 2)
			generator = new Schema2();
		else if (schema == 3)
			generator = new Schema3();
		else if (schema == 4)
			generator = new Schema4();
		else if (schema == 5)
			generator = new Schema5();
		else if (schema == 6)
			generator = new Schema6();
		else
			generator = new Schema1();

		generator.colors = new Color[numCols+1][numRows+1];

		generator.create(numCols+1, numRows+1);
		return generator.colors;
	}

	public abstract void create(int numCols, int numRows);
}

class Schema0 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		for (int i = numCols-1; i >= 0; i--) {
			for (int j =  numRows-1; j >= 0; j--) {
				colors[i][j] =  Color.WHITE;
			}
		}
	}
}

/**
 * Violet
 * @author Juan Manuel
 *
 */
class Schema1 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		int base = 220;
		int base2 = 150;

		int coli =(255-base) / (numCols);
		int colj = (255-base2) / (numRows);
		for (int i = numCols-1; i >= 0; i--) {
			int auxi = base + coli * i;
			for (int j =  numRows-1; j >= 0; j--) {
				int auxj = base2 + colj * j;
				colors[i][j] = new Color(auxj,(auxj*auxi)/255,auxi);
			}
		}
	}
}

/**
 * Pink
 * @author Juan Manuel
 *
 */
class Schema2 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		int base = 220;
		int base2 = 150;

		int coli =(255-base) / (numCols);
		int colj = (255-base2) / (numRows);
		for (int i = numCols-1; i >= 0; i--) {
			int auxi = base + coli * i;
			for (int j =  numRows-1; j >= 0; j--) {
				int auxj = base2 + colj * j;
				colors[i][j] = new Color(auxi,(auxj*auxi)/255,auxj);
			}
		}
	}
}

/**
 * Light pink/brown
 * @author Juan Manuel
 *
 */
class Schema3 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		int base = 220;
		int base2 = 150;

		int coli =(255-base) / (numCols);
		int colj = (255-base2) / (numRows);
		for (int i = numCols-1; i >= 0; i--) {
			int auxi = base + coli * i;
			for (int j =  numRows-1; j >= 0; j--) {
				int auxj = base2 + colj * j;
				colors[i][j] = new Color(auxi,auxj,(auxj*auxi)/255);
			}
		}
	}
}

/**
 * light green
 * @author Juan Manuel
 *
 */
class Schema4 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		int base = 220;
		int base2 = 150;

		int coli =(255-base) / (numCols);
		int colj = (255-base2) / (numRows);
		for (int i = numCols-1; i >= 0; i--) {
			int auxi = base + coli * i;
			for (int j =  numRows-1; j >= 0; j--) {
				int auxj = base2 + colj * j;
				colors[i][j] = new Color((auxj*auxi)/255,auxi,auxj);
			}
		}
	}
}

/**
 * blue
 * @author Juan Manuel
 *
 */
class Schema5 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		int base2 = 220;
		int base = 150;

		int coli =(255-base) / (numCols);
		int colj = (255-base2) / (numRows);
		for (int i = numCols-1; i >= 0; i--) {
			int auxi = base + coli * i;
			for (int j =  numRows-1; j >= 0; j--) {
				int auxj = base2 + colj * j;
				colors[i][j] = new Color((auxj*auxi)/255,auxi,auxj);
			}
		}
	}
}

/**
 * gray
 * @author Juan Manuel
 *
 */
class Schema6 extends CrossTabColorShema{

	public void create(int numCols, int numRows) {
		int base = 200;

		int coli =(255-base) / (numCols + numRows);
		for (int i = numCols-1; i >= 0; i--) {
			for (int j =  numRows-1; j >= 0; j--) {
				int auxi = base + coli * (j + i);
				colors[i][j] = new Color(auxi,auxi,auxi);
			}
		}
	}
}