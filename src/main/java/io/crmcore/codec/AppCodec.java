package io.crmcore.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crmcore.model.Address;
import io.crmcore.model.Model;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Created by someone on 26-Jul-2015.
 */
public class AppCodec<T> implements MessageCodec<T, T> {
    private final ObjectMapper mapper;
    private final Class<T> tClass;

    public AppCodec(ObjectMapper mapper, Class<T> tClass) {
        this.mapper = mapper;
        this.tClass = tClass;
    }

    @Override
    public void encodeToWire(Buffer buffer, T address) {
        final byte[] byteArray = ExceptionUtil.sallowCall(() -> mapper.writeValueAsBytes(address));
        buffer.appendInt(byteArray.length);
        buffer.appendBytes(byteArray);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        final byte[] bytes = buffer.getBytes(pos, pos + length);
        return ExceptionUtil.sallowCall(() -> mapper.readValue(bytes, tClass));
    }

    @Override
    public T transform(T address) {
        return address;
    }

    @Override
    public String name() {
        return tClass.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
