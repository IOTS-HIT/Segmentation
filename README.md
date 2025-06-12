# Segmentation
High-Utility Sequential Rule Mining Based on Segmentation

To ensure the reliability of the evaluation, all experiments were conducted on a PC equipped with Ubuntu 24.04.2 with the 6.11.0-17 Linux kernel, equipped with a 12th Gen Intel® CoreTM i9-12900K processor and 64 GB of RAM. 

The NoRepeat directory contains the implementation code of the algorithms RSC, RSCN, TotalSR and TotalUS, all of which are designed for mining sequences without repeated items. 
In these implementations, only the item with the highest utility is retained if duplicates appear within a sequence. 
The method TotalUS is implemented in Comparators.java under the TotalSR folder, and the execution mode can be specified in the main class Algo.java.

The Repeat directory contains the implementation code of the RSC and RSCR algorithms, which are capable of mining sequences that contain repeated items.
