/***************************************************************************
 *   Copyright (C) 2010 by                                                 *
 *   	Matej Jakop <matej@jakop.si>                                       *
 *      Gregor Kališnik <gregor@unimatrix-one.org>                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License version 3        *
 *   as published by the Free Software Foundation.                         *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 ***************************************************************************/

package lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class MAIRInput extends MAIRObject {
	
	public abstract MAIRInputMessage get() throws IOException ;
	public abstract boolean prepare() throws MAIRExceptionPrepare;
	public abstract boolean connect() throws IOException;
	public abstract boolean disconnect() throws IOException;
	public abstract boolean cleanup();
	public abstract InputStream getInputStream() throws IOException;
	public abstract OutputStream getOutputStream() throws IOException;
}
