package dk.njit.cs643.itch50;

import dk.njit.cs643.itch50.csv.MessageWriter;
import dk.njit.cs643.itch50.model.AddOrderMessage;
import dk.njit.cs643.itch50.model.Message;
import dk.njit.cs643.itch50.parser.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final String INPUT_PATH = "./input";
    private static final String OUTPUT_PATH = "./output";
    private static final MessageParser PARSER = new MessageParser();

    public static void main(String[] args) {
        logger.info("Starting...");

        final File input = new File(INPUT_PATH);
        final File[] inputFiles = input.listFiles((d, name) -> name.endsWith(".NASDAQ_ITCH50"));

        if (inputFiles == null || inputFiles.length == 0) {
            logger.error("Input folder is not a directory or is empty.");
            return;
        }

        try {
            for (File inputFile : inputFiles) {
                if (!inputFile.getName().equalsIgnoreCase("01302020.NASDAQ_ITCH50")) {
                    continue;
                }
                File outputFile = Paths.get(OUTPUT_PATH, "add_orders." + inputFile.getName() + ".csv").toFile();
                try (final MessageWriter output = new MessageWriter(outputFile)) {
                    readFile(inputFile, output);
                }
                System.gc();
            }
        } catch (Exception e) {
            logger.error("Process failed.", e);
        }
    }

    public static List<Message> readFile(File inputFile, MessageWriter output) {
        logger.info("Processing: {}", inputFile);
        final Instant startTime = Instant.now();
        final List<Message> messages = new LinkedList<>();
        try (final FileInputStream fin = new FileInputStream(inputFile);
             final BufferedInputStream in = new BufferedInputStream(fin, 1 << 24)) {

            final FileChannel channel = fin.getChannel();
            int messageIndex = 0;
            while (true) {
                try {
                    final Optional<Message> opMessage = PARSER.parse(in);
                    if (opMessage.isPresent()) {
                        messageIndex++;

                        final Message m = opMessage.get();
                        if (m instanceof AddOrderMessage) {
                            output.write((AddOrderMessage) m);
                        }
                    } else if (in.available() <= 0) {
                        break;
                    }

                    if (messageIndex % 2_500_000 == 0) {
                        logPosition(channel.position());
                        output.flush();
                        System.gc();
                    }
                } catch (Exception e) {
                    logPosition(channel.position());
                    throw new RuntimeException("Failed when parsing message.", e);
                }
            }

            output.flush();

            return messages;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file: " + inputFile, e);
        } finally {
            final Instant endTime = Instant.now();
            final Duration duration = Duration.between(startTime, endTime);
            logger.info("Processed file in {} [{}]", duration, inputFile);
        }
    }

    private static void logPosition(long position) {
        final double gibibytes = position / (1024.0 * 1024.0 * 1024.0);
        logger.debug("Processed: {}GiB", String.format("%,.3f", gibibytes));
    }
}
