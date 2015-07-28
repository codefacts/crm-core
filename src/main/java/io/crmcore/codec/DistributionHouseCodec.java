package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.DistributionHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class DistributionHouseCodec extends AppCodec<DistributionHouse> {
    @Autowired
    public DistributionHouseCodec(ObjectMapper mapper) {
        super(mapper, DistributionHouse.class);
    }
}
