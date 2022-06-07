package com.konchalovmaxim.creditconveyorms.bean;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;

//@Component
//@Profile("logs")
//public class RequestResponseLoggingFilter implements Filter{
//
//    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
//    //private static final int MAX_BODY_SIZE = 1024;
//
//        @Override
//        public void doFilter(ServletRequest request, ServletResponse response,
//                             FilterChain chain) throws IOException, ServletException {
//

//            ContentCachingRequestWrapper req = new ContentCachingRequestWrapper((HttpServletRequest) request);
//            ContentCachingResponseWrapper res = new ContentCachingResponseWrapper((HttpServletResponse) response);
//
//            LOG.info("Logging Request  {} : {}", req.getMethod(), req.getRequestURI());
//
//            chain.doFilter(req, res);
//
//            LOG.info("Request Body: " + new String(req.getContentAsByteArray()));
//
//            LOG.info("Logging Response :{}", res.getContentType());
//            LOG.info("Response Body: " + new String(res.getContentAsByteArray()));
//        }
//
//}
//            //TODO доделать
@Component
@NoArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest(request);

        logger.info(new String(cachedBodyHttpServletRequest.getInputStream().readAllBytes()));

        filterChain.doFilter(cachedBodyHttpServletRequest, response);

    }
}

class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}

class CachedBodyServletInputStream extends ServletInputStream {

    private InputStream cachedBodyInputStream;


    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }

    @Override
    public boolean isFinished() {
        try {
            return cachedBodyInputStream.available() == 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}