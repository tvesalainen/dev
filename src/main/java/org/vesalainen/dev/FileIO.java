/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vesalainen.dev;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author tkv
 */
public class FileIO implements AutoCloseable
{
    protected byte[] path;
    protected int fd;

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
    public void close() throws Exception
    {
        close(fd);
    }

    private native void close(int fd) throws IOException;

}
