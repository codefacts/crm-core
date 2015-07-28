package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.HeadOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class HeadOfficeCodec extends AppCodec<HeadOffice> {
    @Autowired
    public HeadOfficeCodec(ObjectMapper mapper) {
        super(mapper, HeadOffice.class);
    }
}
