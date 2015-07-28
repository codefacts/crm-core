package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class ConsumerCodec extends AppCodec<Consumer> {
    @Autowired
    public ConsumerCodec(ObjectMapper mapper) {
        super(mapper, Consumer.class);
    }
}
