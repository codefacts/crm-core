package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.UserBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserBasicCodec extends AppCodec<UserBasic> {
    @Autowired
    public UserBasicCodec(ObjectMapper mapper) {
        super(mapper, UserBasic.class);
    }
}
