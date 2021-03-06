/*
 * Copyright (C) 2016 Timo Vesalainen <timo.vesalainen@iki.fi>
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
 */
package org.vesalainen.dev;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.vesalainen.loader.LibraryLoader;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class FileIO implements AutoCloseable
{
    protected static boolean debug;
    protected byte[] path;
    protected int fd;

    static
    {                                       
        try
        {
            LibraryLoader.loadLibrary(FileIO.class, "Dev");
        }
        catch (IOException | UnsatisfiedLinkError ex)
        {
            throw new UnsatisfiedLinkError("Can't load either x86_64 or arm6vl .so \n"+ex.getMessage());
        }
    }

    protected FileIO(int fd)
    {
        this.fd = fd;
    }

    protected FileIO(String path)
    {
        this.path = path.getBytes(StandardCharsets.US_ASCII);
    }

    public static FileIO open(String path) throws IOException
    {
        FileIO fio = new FileIO(path);
        fio.open();
        return fio;
    }

    public int getFd()
    {
        return fd;
    }
    
    protected void open() throws IOException
    {
        fd = open(path);
    }
    
    private native int open(byte[] path) throws IOException;

    @Override
    public void close() throws IOException
    {
        close(fd);
    }

    public static void setDebug(boolean debug)
    {
        FileIO.debug = debug;
        debug(debug);
    }
    private static native void debug(boolean debug);
    
    private native void close(int fd) throws IOException;
    /**
     * Reads one byte
     * @return One byte
     * @throws IOException 
     */
    public byte read() throws IOException
    {
        return read(fd);
    }
    private native byte read(int fd) throws IOException;
    /**
     * Writes one byte
     * @param b Byte
     * @throws IOException 
     */
    public void write(byte b) throws IOException
    {
        write(fd, b);
    }
    private native void write(int fd, byte b) throws IOException;
    /**
     * Read bytes to buffer. Returns number of bytes actually read.
     * @param buf
     * @return
     * @throws IOException 
     */
    public int read(byte[] buf) throws IOException
    {
        return read(fd, buf, 0, buf.length);
    }
    /**
     * Read bytes to buffer. Returns number of bytes actually read.
     * @param buf
     * @param off
     * @param len
     * @return
     * @throws IOException 
     */
    public int read(byte[] buf, int off, int len) throws IOException
    {
        return read(fd, buf, off, len);
    }
    private native int read(int fd, byte[] buf, int off, int len) throws IOException;
    /**
     * Writes bytes
     * @param bytes
     * @throws IOException 
     */
    public void write(byte... bytes) throws IOException
    {
        write(fd, bytes, 0, bytes.length);
    }
    /**
     * Writes length bytes starting from start.
     * @param buf
     * @param off
     * @param len
     * @throws IOException 
     */
    public void write(byte[] buf, int off, int len) throws IOException
    {
        write(fd, buf, off, len);
    }
    private native void write(int fd, byte[] buf, int off, int len) throws IOException;
}
