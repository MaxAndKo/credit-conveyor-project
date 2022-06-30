package com.konchalovmaxim.creditconveyorms.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream;

    public CachedBodyHttpServletResponse(HttpServletResponse response) throws IOException {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public byte[] getByteArray() {
        return byteArrayOutputStream.toByteArray();
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return byteArrayOutputStream != null;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
                CachedBodyHttpServletResponse.super.getOutputStream().write(b);
            }
        };
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(byteArrayOutputStream);
    }

}
