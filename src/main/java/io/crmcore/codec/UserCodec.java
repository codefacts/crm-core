package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserCodec extends AppCodec<UserInterface> {
    @Autowired
    public UserCodec(ObjectMapper mapper) {
        super(mapper, UserInterface.class);
    }
}
