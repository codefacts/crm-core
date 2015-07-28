package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.UserIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserIndexCodec extends AppCodec<UserIndex> {
    @Autowired
    public UserIndexCodec(ObjectMapper mapper) {
        super(mapper, UserIndex.class);
    }
}
