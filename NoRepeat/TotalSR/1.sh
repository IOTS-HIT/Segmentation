# !/bin/bash

# Define the array of filenames
filenames=("BIBLE.txt" )

# Define the array of minutil values
minutils=(12000)

confss=(0.6 0.65 0.7 0.75 0.8 0.85 0.9)

# Loop over all combinations of filename, minutil, and output
for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/US/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 1
      done
  done
done

filenames=("kosarak10k.txt")

# Define the array of minutil values
minutils=(19000)

# Loop over all combinations of filename, minutil, and output
for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/US/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 1
      done
  done
done

filenames=("SIGN.txt")
minutils=(11000)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/US/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 1
      done
  done
done

filenames=("Leviathan.txt")
minutils=(1600)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/US/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 1
      done
  done
done

filenames=("10K.txt")
minutils=(1300)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/US/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 1
      done
  done
done

filenames=("20K.txt")
minutils=(3200)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/US/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 1
      done
  done
done




# Define the array of filenames
filenames=("BIBLE.txt" )

# Define the array of minutil values
minutils=(12000)


# Loop over all combinations of filename, minutil, and output
for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/TotalSR/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 2
      done
  done
done

filenames=("kosarak10k.txt")

# Define the array of minutil values
minutils=(19000)

# Loop over all combinations of filename, minutil, and output
for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/TotalSR/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 2
      done
  done
done

filenames=("SIGN.txt")
minutils=(11000)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/TotalSR/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 2
      done
  done
done

filenames=("Leviathan.txt")
minutils=(1600)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/TotalSR/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 2
      done
  done
done

filenames=("10K.txt")
minutils=(1300)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/TotalSR/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 2
      done
  done
done

filenames=("20K.txt")
minutils=(3200)

for filename in "${filenames[@]}"; do
  for conf in "${confss[@]}"; do
    for minutil in "${minutils[@]}"; do
        # Construct the input and output paths
        input="../dataset/$filename"
        output_path="../output/TotalSR/${filename%.txt}_${conf}.txt"
        # Run the Java program
        java Algo $input $output_path $minutil $conf 2
      done
  done
done



# !/bin/bash

# Define the array of filenames
# filenames=("10K.txt" )

# # Define the array of minutil values
# minutils=(1300	1350	1400	1450	1500	1550 1600)

# # Loop over all combinations of filename, minutil, and output
# for filename in "${filenames[@]}"; do
#   for minutil in "${minutils[@]}"; do
#       # Construct the input and output paths
#       input="../dataset/$filename"
#       output_path="../output/TotalSR/${filename%.txt}_${minutil}.txt"
#       # Run the Java program
#       java Algo $input $output_path $minutil
#   done
# done

# filenames=("20K.txt")
# # Define the array of minutil values
# minutils=(3200	3250	3300	3350	3400	3450	3500)

# # Loop over all combinations of filename, minutil, and output
# for filename in "${filenames[@]}"; do
#   for minutil in "${minutils[@]}"; do
#       # Construct the input and output paths
#       input="../dataset/$filename"
#       output_path="../output/TotalSR/${filename%.txt}_${minutil}.txt"
#       # Run the Java program
#       java Algo $input $output_path $minutil
#   done
# done

# # filenames=("20K.txt")

# #!/bin/bash

# # Define the array of filenames
# filenames=("10K.txt" )

# # Define the array of minutil values
# minutils=(1300	1350	1400	1450	1500	1550 1600)

# # Loop over all combinations of filename, minutil, and output
# for filename in "${filenames[@]}"; do
#   for minutil in "${minutils[@]}"; do
#       # Construct the input and output paths
#       input="../dataset/$filename"
#       output_path="../output/TotalSR/${filename%.txt}_${minutil}.txt"
#       # Run the Java program
#       java Algo $input $output_path $minutil
#   done
# done

# filenames=("20K.txt")
# # Define the array of minutil values
# minutils=(3200	3250	3300	3350	3400	3450	3500)

# # Loop over all combinations of filename, minutil, and output
# for filename in "${filenames[@]}"; do
#   for minutil in "${minutils[@]}"; do
#       # Construct the input and output paths
#       input="../dataset/$filename"
#       output_path="../output/TotalSR/${filename%.txt}_${minutil}.txt"
#       # Run the Java program
#       java Algo $input $output_path $minutil
#   done
# done
