package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class AreaCodec extends AppCodec<Area> {
    @Autowired
    public AreaCodec(ObjectMapper mapper) {
        super(mapper, Area.class);
    }
}
