package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.BrSupervisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class BrSupervisorCodec extends AppCodec<BrSupervisor> {
    @Autowired
    public BrSupervisorCodec(ObjectMapper mapper) {
        super(mapper, BrSupervisor.class);
    }
}
