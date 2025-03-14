package org.kuro.port.incoming;

public interface IncomingAdapter {
    void process(String path, int id);
}
