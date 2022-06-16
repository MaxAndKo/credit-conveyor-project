package com.konchalovmaxim.creditconveyorms.filter;

import com.konchalovmaxim.creditconveyorms.enums.HttpMessageKind;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class HttpMessageLogFormatter {

    public void doLogHttpMessage(HttpMessage httpMessage){
        String prefix = httpMessage.getKind().prefix;

        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(String.format("%s Type: %s\n", prefix, httpMessage.getType()));

        if (httpMessage.getKind() == HttpMessageKind.REQUEST)
        {
            stringBuilder.append(String.format("%s Method: %s\n", prefix, httpMessage.getMethod()));
            stringBuilder.append(String.format("%s URI: %s\n", prefix, httpMessage.getUri()));
        }
        else {
            stringBuilder.append(String.format("%s Status: %s\n", prefix, httpMessage.getStatus()));
        }

        stringBuilder.append(String.format("%s Headers: %s\n", prefix, httpMessage.getHeaders()));

        if (!httpMessage.getBody().isBlank())
            stringBuilder.append(String.format("%s Body: %s", prefix, httpMessage.getBody()));

        log.info(stringBuilder.toString());

    }

}
