package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.App;
import io.crmcore.model.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class BrandCodec extends AppCodec<Brand> {
    @Autowired
    public BrandCodec(ObjectMapper mapper) {
        super(mapper, Brand.class);
    }
}
