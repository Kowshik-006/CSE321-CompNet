#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Invalid number of arguments!"
    exit 1
fi 

if [ "$1" -eq 1 ]; then

    echo -e "Running Simulation for TcpBic...\n"
    echo -e "Transport Protocol: TcpBic \nBandwidth: 2 Mbps \nNumber of Flows: 7\n"
    ./ns3 run "scratch/tcp-variants-comparison.cc --tracing=true --transport_prot=TcpBic --bandwidth=2Mbps --num_flows=7"

    echo -e "Finished Simulation for TcpBic\nPlotting Graphs for TcpBic..."
    python3 graph_plotter_tcp.py
    echo -e "Finished Plotting Graphs for TcpBic\n"

fi


if [ "$1" -eq 2 ]; then

    echo -e "Running Simulation for TcpLedbat...\n"
    echo -e "Transport Protocol: TcpLedbat \nBandwidth: 2 Mbps \nNumber of Flows: 7\n"
    ./ns3 run "scratch/tcp-variants-comparison.cc --tracing=true --transport_prot=TcpLedbat --bandwidth=2Mbps --num_flows=7"

    echo -e "Finished Simulation for TcpLedbat\nPlotting Graphs for TcpLedbat..."
    python3 graph_plotter_tcp.py
    echo -e "Finished Plotting Graphs for TcpLedbat\n"

fi

if [ "$1" -eq 3 ]; then

    echo -e "Running Simulation for TcpBicTweaked...\n"
    echo -e "Transport Protocol: TcpBicTweaked \nBandwidth: 2 Mbps \nNumber of Flows: 7\n"
    ./ns3 run "scratch/tcp-variants-comparison.cc --tracing=true --transport_prot=TcpBicTweaked --bandwidth=2Mbps --num_flows=7"

    echo -e "Finished Simulation for TcpBicTweaked\nPlotting Graphs for TcpBicTweaked..."
    python3 graph_plotter_tcp.py
    echo -e "Finished Plotting Graphs for TcpBicTweaked\n"

fi

