package com.mikuwxc.autoreply.utils;

import java.io.IOException;

public interface Serializable {
    public void serialize(Output out) throws IOException;

    public void deserialize(Input in) throws IOException;
}
