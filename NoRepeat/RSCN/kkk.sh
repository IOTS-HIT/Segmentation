#!/bin/bash

# Define the array of filenames
filenames=("10K.txt" )

# Define the array of minutil values
minutils=(1300	1350	1400	1450	1500	1550 1600)

# Loop over all combinations of filename, minutil, and output
for filename in "${filenames[@]}"; do
  for minutil in "${minutils[@]}"; do
      # Construct the input and output paths
      input="../dataset/$filename"
      output_path="../output/AlgoNoRU/${filename%.txt}_${minutil}.txt"
      # Run the Java program
      java Algo $input $output_path $minutil
  done
done

filenames=("20K.txt")
# Define the array of minutil values
minutils=(3200	3250	3300	3350	3400	3450	3500)

# Loop over all combinations of filename, minutil, and output
for filename in "${filenames[@]}"; do
  for minutil in "${minutils[@]}"; do
      # Construct the input and output paths
      input="../dataset/$filename"
      output_path="../output/AlgoNoRU/${filename%.txt}_${minutil}.txt"
      # Run the Java program
      java Algo $input $output_path $minutil
  done
done

filenames=("20K.txt")