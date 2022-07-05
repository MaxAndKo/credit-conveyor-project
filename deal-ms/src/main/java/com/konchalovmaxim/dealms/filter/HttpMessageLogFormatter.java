package com.konchalovmaxim.dealms.filter;

import com.konchalovmaxim.dealms.enums.HttpMessageKind;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class HttpMessageLogFormatter {

    public void doLogHttpMessage(HttpMessage httpMessage) {
        if (httpMessage.getKind() == HttpMessageKind.REQUEST) {
            doLogRequest(httpMessage);
        } else {
            doLogResponse(httpMessage);
        }

    }

    private void doLogRequest(HttpMessage httpMessage) {
        log.info(String.format("\n%1$s Type: %2$s\n%1$s Method: %3$s\n%1$s URI: %4$s\n%1$s Headers: %5$s\n%1$s Body: %6$s",
                httpMessage.getKind().prefix, HttpMessage.getType(), httpMessage.getMethod(), httpMessage.getUri(),
                httpMessage.getHeaders(), httpMessage.getBody()));
    }

    private void doLogResponse(HttpMessage httpMessage) {
        log.info(String.format("\n%1$s Type: %2$s\n%1$s Status: %3$s\n%1$s Headers: %4$s\n%1$s Body: %5$s",
                httpMessage.getKind().prefix, HttpMessage.getType(), httpMessage.getStatus(),
                httpMessage.getHeaders(), httpMessage.getBody()));
    }

}
