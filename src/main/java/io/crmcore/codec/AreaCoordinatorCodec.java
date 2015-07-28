package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.AreaCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class AreaCoordinatorCodec extends AppCodec<io.crmcore.model.AreaCoordinator> {
    @Autowired
    public AreaCoordinatorCodec(ObjectMapper mapper) {
        super(mapper, AreaCoordinator.class);
    }
}
