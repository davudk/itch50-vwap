package dk.njit.cs643.itch50.model;

public enum MessageType {
    SYSTEM_EVENT('S', 12, "System Event Message"), // Section 4.1
    STOCK_DIRECTORY('R', 39, "Stock Directory Message"), // Section 4.2.1
    STOCK_TRADING_ACTION('H', 25, "Stock Trading Action Message"), // Section 4.2.2
    REG_SHO_SHORT_SALE_PRICE_TEST_RESTRICTED_INDICATOR('Y', 20, "Reg SHO Short Sale Price Test Restricted Indicator Message"), // Section 4.2.3
    MARKET_PARTICIPANT_POSITION('L', 26, "Market Participant Position Message"), // Section 4.2.4
    MARKET_WIDE_CIRCUIT_BREAKER_DECLINE_LEVEL('V', 35, "Market-Wide Circuit Breaker (MWCB) Decline Level Message"), // Section 4.2.5.1
    MARKET_WIDE_CIRCUIT_BREAKER_STATUS('W', 12, "Market-Wide Circuit Breaker (MWCB) Status Message"), // Section 4.2.5.2
    IPO_QUOTING_PERIOD_UPDATE('K', 28, "IPO Quoting Period Update Message"), // Section 4.2.6
    LULD_AUCTION_COLLAR('J', 35, "Limit Up - Limit Down (LULD) Auction Collar Message"), // Section 4.2.7
    OPERATIONAL_HALT('h', 21, "Operational Halt Message"), // Section 4.2.8
    ADD_ORDER_NO_MPID_ATTRIBUTION('A', 36, "Add Order - No MPID Attribution Message"), // Section 4.3.1
    ADD_ORDER_WITH_MPID_ATTRIBUTION('F', 40, "Add Order with MPID Attribution Message"), // Section 4.3.2
    ORDER_EXECUTED('E', 31, "Order Executed Message"), // Section 4.4.1
    ORDER_EXECUTED_WITH_PRICE('C', 36, "Order Executed With Price Message"), // Section 4.4.2
    ORDER_CANCEL('X', 23, "Order Cancel Message"), // Section 4.4.3
    ORDER_DELETE('D', 19, "Order Delete Message"), // Section 4.4.4
    ORDER_REPLACE('U', 35, "Order Replace Message"), // Section 4.4.5
    TRADE('P', 44, "Trade Message"), // Section 4.5.1
    CROSS_TRADE('Q', 40, "Cross Trade Message"), // Section 4.5.2
    BROKEN_TRADE('B', 19, "Broken Trade Message"), // Section 4.5.3
    NET_ORDER_IMBALANCE_INDICATOR('I', 50, "Net Order Imbalance Indicator (NOII) Message"), // Section 4.6
    RETAIL_PRICE_IMPROVEMENT_INDICATOR('N', 1337, "Retail Interest Message"); // Section 4.7
    
    private final char type;
    private final int length;
    private final String name;

    MessageType(char type, int length, String name) {
        this.type = type;
        this.length = length;
        this.name = name;
    }

    public char getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
