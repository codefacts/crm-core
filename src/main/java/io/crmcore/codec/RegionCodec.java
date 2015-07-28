package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class RegionCodec extends AppCodec {
    @Autowired
    public RegionCodec(ObjectMapper mapper) {
        super(mapper, Region.class);
    }
}
