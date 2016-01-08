/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vesalainen.dev;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.vesalainen.loader.LibraryLoader;

/**
 *
 * @author tkv
 */
public class FileIO implements AutoCloseable
{
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

    private native void close(int fd) throws IOException;
    /**
     * Reads one byte
     * @return One byte
     * @throws IOException 
     */
    public int read() throws IOException
    {
        return read(fd);
    }
    private native int read(int fd) throws IOException;
    /**
     * Writes one byte
     * @param cc Byte
     * @throws IOException 
     */
    public void write(int cc) throws IOException
    {
        write(fd, cc);
    }
    private native void write(int fd, int cc) throws IOException;
    /**
     * Read bytes to buffer. Returns number of bytes actually read.
     * @param buf
     * @return
     * @throws IOException 
     */
    public int read(byte[] buf) throws IOException
    {
        return read(fd, buf);
    }
    private native int read(int fd, byte[] buff) throws IOException;
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
     * @param start
     * @param length
     * @throws IOException 
     */
    public void write(byte[] buf, int start, int length) throws IOException
    {
        write(fd, buf, start, length);
    }
    private native void write(int fd, byte[] buf, int start, int length) throws IOException;
}
