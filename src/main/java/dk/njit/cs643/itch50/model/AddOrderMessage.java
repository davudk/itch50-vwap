package dk.njit.cs643.itch50.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class AddOrderMessage implements Message {
    private int stockLocate;
    private int trackingNumber;
    private long timestamp;
    private long orderReferenceNumber;
    private char buySellIndicator;
    private int shares;
    private String stockSymbol;
    private BigDecimal price;

    public int getStockLocate() { return stockLocate; }
    public void setStockLocate(int stockLocate) { this.stockLocate = stockLocate; }
    public int getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(int trackingNumber) { this.trackingNumber = trackingNumber; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getOrderReferenceNumber() { return orderReferenceNumber; }
    public void setOrderReferenceNumber(long orderReferenceNumber) { this.orderReferenceNumber = orderReferenceNumber; }
    public char getBuySellIndicator() { return buySellIndicator; }
    public void setBuySellIndicator(char buySellIndicator) { this.buySellIndicator = buySellIndicator; }
    public int getShares() { return shares; }
    public void setShares(int shares) { this.shares = shares; }
    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Instant getTimestampAsInstant(LocalDate date) { return Instant.from(date).plusNanos(timestamp); }
    public boolean isBuyOrder() { return buySellIndicator == 'B'; }
    public boolean isSellOrder() { return buySellIndicator == 'S'; }

    @Override
    public MessageType getMessageType() { return MessageType.ADD_ORDER_NO_MPID_ATTRIBUTION; }
}
