package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class ClientCodec extends AppCodec<Client> {
    @Autowired
    public ClientCodec(ObjectMapper mapper) {
        super(mapper, Client.class);
    }
}
