package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class AdminCodec extends AppCodec<Admin> {
    @Autowired
    public AdminCodec(ObjectMapper mapper) {
        super(mapper, Admin.class);
    }
}
