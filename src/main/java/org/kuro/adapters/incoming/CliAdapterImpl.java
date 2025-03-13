package org.kuro.adapters.incoming;

import org.kuro.core.application.ConfigService;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.hendler.ErrorHandler;
import org.kuro.port.incoming.IncomingAdapter;


public class CliAdapterImpl implements IncomingAdapter {

    private final ConfigService configService;
    private final ErrorHandler errorHandler;

    public CliAdapterImpl(ConfigService configService, ErrorHandler errorHandler) {
        this.configService = configService;
        this.errorHandler = errorHandler;
    }

    @Override
    public void process(String path, int id) {

        try {
            String outputPath = configService.processConfig(path, id);
            System.out.println("JSON save in: " + outputPath);
        } catch (ApplicationException e) {
            errorHandler.handle(e);
        } catch (Exception e) {
            errorHandler.handleUnexpected(e);
        }

    }
}