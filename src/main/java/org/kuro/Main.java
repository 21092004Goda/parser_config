package org.kuro;

import org.kuro.adapters.incoming.CliAdapterImpl;
import org.kuro.adapters.outgoing.JsonWriterImpl;
import org.kuro.adapters.outgoing.FileReaderImpl;
import org.kuro.core.application.ConfigService;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.hendler.ConsoleErrorHandler;
import org.kuro.exceptions.hendler.ErrorHandler;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.port.incoming.IncomingAdapter;
import org.kuro.port.outgoing.SourceReader;
import org.kuro.port.outgoing.SourceWriter;

public class Main {
    public static void main(String[] args) {
        ErrorHandler errorHandler = new ConsoleErrorHandler();

        try {
            IncomingAdapter incoming = getIncomingAdapter(args, errorHandler);
            incoming.process(args[0], Integer.parseInt(args[1]));
        }
        catch (ApplicationException e) {
            errorHandler.handle(e);
        }
        catch (NumberFormatException e) {
            errorHandler.handle(
                    new ApplicationException(
                            ErrorCode.INVALID_PARAMETER,
                            "Invalid configuration ID: " + args[1], 
                            e
                    )
            );
        }
        catch (Exception e) {
            errorHandler.handleUnexpected(e);
        }
    }

    private static IncomingAdapter getIncomingAdapter(String[] args, ErrorHandler errorHandler) {
        if (args.length != 2) {
            throw new ApplicationException(
                    ErrorCode.INVALID_PARAMETER,
                    "Usage: java -jar app.jar <path> <id>"
            );
        }

        SourceWriter sourceWriter = new JsonWriterImpl();
        SourceReader sourceReader = new FileReaderImpl();
        ConfigService configService = new ConfigService(sourceWriter, sourceReader);
        return new CliAdapterImpl(configService, errorHandler);
    }
}
