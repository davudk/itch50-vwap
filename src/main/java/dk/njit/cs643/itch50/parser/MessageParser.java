package dk.njit.cs643.itch50.parser;

import dk.njit.cs643.itch50.model.AddOrderMessage;
import dk.njit.cs643.itch50.model.Message;
import dk.njit.cs643.itch50.model.MessageType;
import dk.njit.cs643.itch50.model.TradeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class MessageParser {
    private static final MessageType[] MESSAGE_TYPES = new MessageType[256];
    private static byte[] SHARED_BUFFER = new byte[65536];

    static {
        for (MessageType t : MessageType.values()) {
            MESSAGE_TYPES[t.getType()] = t;
        }
    }

    public boolean isValidMessageType(int type) {
        try {
            Objects.requireNonNull(MESSAGE_TYPES[type]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Message> parse(InputStream in) throws IOException {
        int initial;
        while (true) {
            initial = in.read();
            if (initial == -1) {
                return Optional.empty();
            } else if (initial < 32 || initial == '\'' || initial == '$' || initial == '#' ||
                    initial == '(' || initial == ',' || initial == '2') {
            } else break;
        }

        final MessageType type;
        try {
            type = MESSAGE_TYPES[initial];
            Objects.requireNonNull(type);
        } catch (Exception e) {
            throw new RuntimeException("Unrecognized message type: 0x" + Integer.toHexString(initial), e);
        }

        int remainingLength = type.getLength() - 1;
        while (remainingLength > 0) {
            SHARED_BUFFER[0] = (byte) initial;
            int length = in.read(SHARED_BUFFER, 1, remainingLength);
            if (length == -1) {
                throw new RuntimeException("Message was not completed.");
            }
            remainingLength -= length;
        }

        if (type.equals(MessageType.ADD_ORDER_NO_MPID_ATTRIBUTION)) {
            return Optional.of(parseAddOrderMessage(SHARED_BUFFER));
        }

        return Optional.of(() -> type);
    }

    private AddOrderMessage parseAddOrderMessage(byte[] buffer) {
        final AddOrderMessage m = new AddOrderMessage();
        m.setStockLocate(parseInt16(buffer, 1));
        m.setTrackingNumber(parseInt16(buffer, 3));
        m.setTimestamp(parseInt48(buffer, 5));
        m.setOrderReferenceNumber(parseInt64(buffer, 11));
        m.setBuySellIndicator((char) buffer[19]);
        m.setShares(parseInt32(buffer, 20));
        m.setStockSymbol(parseString(buffer, 24, 8).trim());
        m.setPrice(parsePrice(buffer, 32, 4));
        return m;
    }

    private TradeMessage parseTradeMessage(byte[] buffer) {
        final TradeMessage m = new TradeMessage();
        m.setStockLocate(parseInt16(buffer, 1));
        m.setTrackingNumber(parseInt16(buffer, 3));
        m.setTimestamp(parseInt48(buffer, 5));
        m.setOrderReferenceNumber(parseInt64(buffer, 11));
        m.setBuySellIndicator((char) buffer[19]);
        m.setShares(parseInt32(buffer, 20));
        m.setStockSymbol(parseString(buffer, 24, 8).trim());
        m.setPrice(parsePrice(buffer, 32, 4));
        m.setMatchNumber(parseInt64(buffer, 36));
        return m;
    }

    private static int parseInt16(byte[] buffer, int off) {
        return (buffer[off] << 8) +
                buffer[off + 1];
    }

    private static int parseInt32(byte[] buffer, int off) {
        return (buffer[off] << 24) +
                (buffer[off + 1] << 16) +
                (buffer[off + 2] << 8) +
                buffer[off + 3];
    }

    private static long parseInt48(byte[] buffer, int off) {
        return ((long) buffer[off] << 40) +
                ((long) buffer[off + 1] << 32) +
                ((long) buffer[off + 2] << 24) +
                ((long) buffer[off + 3] << 16) +
                ((long) buffer[off + 4] << 8) +
                buffer[off + 5];
    }

    private static long parseInt64(byte[] buffer, int off) {
        return ((long) buffer[off] << 56) +
                ((long) buffer[off + 1] << 48) +
                ((long) buffer[off + 2] << 40) +
                ((long) buffer[off + 3] << 32) +
                ((long) buffer[off + 4] << 24) +
                ((long) buffer[off + 5] << 16) +
                ((long) buffer[off + 6] << 8) +
                buffer[off + 7];
    }

    private static String parseString(byte[] buffer, int off, int length) {
        return new String(buffer, off, length, StandardCharsets.US_ASCII);
    }

    private static BigDecimal parsePrice(byte[] buffer, int off, int length) {
        BigInteger intValue = BigInteger.ZERO;
        for (int i = 0; i < length; i++) {
            final BigInteger iv = BigInteger.valueOf(buffer[off + i]);
            intValue = intValue.shiftLeft(8).add(iv);
        }

        return new BigDecimal(intValue).movePointLeft(length);
    }
}
