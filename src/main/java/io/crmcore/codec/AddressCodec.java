package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class AddressCodec extends AppCodec<Address> {
    @Autowired
    public AddressCodec(ObjectMapper mapper) {
        super(mapper, Address.class);
    }
}
