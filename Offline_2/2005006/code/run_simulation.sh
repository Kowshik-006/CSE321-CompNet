#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Error: Invalid number of arguments"
    exit 1
fi

if [ "$1" != "aodv" ] && [ "$1" != "raodv" ]; then
    echo "Error: Argument must be either 'aodv' or 'raodv'."
    exit 1
fi

echo "Running simulation for $1 protocol..."

csv_file1="node_num_$1.csv"
csv_file2="node_speed_$1.csv"
csv_file3="packet_rate_$1.csv"

echo "Number of Nodes,Throughput,End-To-End Delay,Packet Delivery Ratio,Packet Drop Ratio" > $csv_file1
echo "Node Speed,Throughput,End-To-End Delay,Packet Delivery Ratio,Packet Drop Ratio" > $csv_file2
echo "Packets Per Second,Throughput,End-To-End Delay,Packet Delivery Ratio,Packet Drop Ratio" > $csv_file3

nodes=(20 40 70 100)
speeds=(5 10 15 20)
pps=(100 200 300 400)

for n in "${nodes[@]}"; do 
    ./ns3 run "scratch/manet-routing-compare-$1 --nodes=$n --speed=20 --pps=100 --outputfile=$csv_file1"
done

for s in "${speeds[@]}"; do
    ./ns3 run "scratch/manet-routing-compare-$1 --nodes=20 --speed=$s --pps=100 --outputfile=$csv_file2"
done

for p in "${pps[@]}"; do
    ./ns3 run "scratch/manet-routing-compare-$1 --nodes=20 --speed=20 --pps=$p --outputfile=$csv_file3"
done

echo "Simulation complete"
