package dk.njit.cs643.itch50.csv;

import dk.njit.cs643.itch50.model.AddOrderMessage;
import dk.njit.cs643.itch50.model.Message;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class MessageWriter implements AutoCloseable {
    private static final CSVFormat DEFAULT_CSV_FORMAT = CSVFormat.DEFAULT.withHeader(
            "Timestamp",
            "Shares",
            "Stock",
            "Price"
    );
    private final CSVPrinter csvPrinter;

    public MessageWriter(String file) throws IOException {
        this(new File(file), DEFAULT_CSV_FORMAT);
    }
    public MessageWriter(File file) throws IOException {
        this(file, DEFAULT_CSV_FORMAT);
    }

    public MessageWriter(File file, CSVFormat csvFormat) throws IOException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(csvFormat);
        csvPrinter = new CSVPrinter(new FileWriter(file), csvFormat);
    }

    public <T extends Message> void write(Iterable<T> messages) throws IOException {
        for (Message m : messages) {
            if (m instanceof AddOrderMessage) {
                write((AddOrderMessage) m);
            }
        }
    }

    public void write(AddOrderMessage m) throws IOException {
        csvPrinter.printRecord(m.getTimestamp(), m.getShares(), m.getStockSymbol(), m.getPrice());
    }

    public void flush() throws IOException {
        csvPrinter.flush();
    }

    @Override
    public void close() throws Exception {
        csvPrinter.close(true);
    }
}
