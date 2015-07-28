package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class RoleCodec extends AppCodec<Role> {
    @Autowired
    public RoleCodec(ObjectMapper mapper) {
        super(mapper, Role.class);
    }
}
