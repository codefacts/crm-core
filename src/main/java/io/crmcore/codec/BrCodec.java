package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Br;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class BrCodec extends AppCodec<Br> {
    @Autowired
    public BrCodec(ObjectMapper mapper) {
        super(mapper, Br.class);
    }
}
