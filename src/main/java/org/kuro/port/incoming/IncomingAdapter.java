package org.kuro.port.incoming;

import java.io.IOException;

public interface IncomingAdapter {
    void process(String path, int id) throws IOException;
}
