
## VWAP calculation on NASDAQ ITCH5.0 datasets
This application was made as a class project for
[CS 643](https://catalog.njit.edu/search/?P=CS%20643) "Cloud Computing" at NJIT.
This is a graduate level class I took in Spring of 2020. As a group we chose to compute
the [Volume Weighted Average Price (VWAP)](https://www.investopedia.com/terms/v/vwap.asp) of a stock
using Hadoop MapReduce. We're performing this calculation on the famous NASDAQ ITCH5.0 dataset.
See below for more info.

#### Input
The datasets we used (listed below) were obtained from the
[official NASDAQ FTP repository](ftp://emi.nasdaq.com/ITCH/).
 - 07302019.NASDAQ_ITCH50.gz
 - 08302019.NASDAQ_ITCH50.gz
 - 10302019.NASDAQ_ITCH50.gz
 - 12302019.NASDAQ_ITCH50.gz
 - 01302020.NASDAQ_ITCH50.gz

#### Specification
The project follows the 
[ITCH5.0 specification](https://www.nasdaqtrader.com/content/technicalsupport/specifications/dataproducts/NQTVITCHspecification.pdf).
The data file is a binary file consisting only of records (no trailing metadata). Records are of various types and each
type is of a constant length. This makes random reads difficult.

#### Implementation
Calculating the VWAP is a two-step process. Since the NASDAQ ITCH5.0 data files contain records of varying lengths it's
not easy to chunk the data so that it can be distributed to various nodes of the Hadoop cluster. If the records were
delimited by a symbol it would have been easier to split the work, but it is not the case.

So, our first step is a one-time process (per data file) that does several things:
 - Remove records that aren't necessary to compute VWAP.
 - Of the remaining records, remove the fields that aren't necessary to compute VWAP.
 - Save the remaining data as a CSV file.

This process is very quick, since it's a linear operation on the data.
Once that is complete, the VWAP of any stock can be computed many times using the produced CSV as the input. 

#### Running locally
 1. Build the project using [Gradle](https://docs.gradle.org/current/userguide/userguide.html)
 2. Run `dk.njit.cs643.itch50.Application` to convert all data files under `./input/` directory.
    The produced CSV files will be written to `./output/`.
 3. Put the CSV file into HDFS: `hadoop fs -copyFromLocal ~/Desktop/csv/output/add_orders.12302019.NASDAQ_ITCH50.csv /input`
 4. Run MapReduce VWAP: e.g. `hadoop jar itch50vwap.jar dk.njit.cs643.itch50.vwap.VWAP ~/Desktop/vwap/output /input/add_orders.12302019.NASDAQ_ITCH50.csv`
    The first application argument is the output directory and all remaining arguments are the input files.