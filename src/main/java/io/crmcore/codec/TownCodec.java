package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Town;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class TownCodec extends AppCodec<Town> {
    @Autowired
    public TownCodec(ObjectMapper mapper) {
        super(mapper, Town.class);
    }
}
