import sys
import pandas as pd
import matplotlib.pyplot as plt


# Check if the correct number of arguments are passed
if len(sys.argv) != 2:
    print('Error: Type of the protocol not passed as an argument')
    sys.exit(1)

type = sys.argv[1]

if (type != 'aodv' and type != 'raodv'):
    print('Error: Invalid protocol type')
    sys.exit(1)

def plot_graph(x,y,xlabel,ylabel,title):
    # plt.figure()
    plt.plot(x,y,marker='o',linestyle='-',color='b',label=ylabel)
    
    # for xi, yi in zip(x, y):
    #     plt.annotate(f'({xi}, {yi})', (xi, yi), textcoords="offset points", xytext=(5, 5), fontsize=9)
    
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.title(title)
    plt.grid()
    plt.legend()
    plt.show()

def extract_and_plot(data,x_name):
    x = data.iloc[:,0]
    throughput = data.iloc[:,1]
    endToEndDelay = data.iloc[:,2]
    packetDeliveryRatio = data.iloc[:,3]
    packetDropRatio = data.iloc[:,4]
    plot_graph(x,throughput,x_name,'Throughput (kbps)',f'Throughput vs. {x_name}')
    plot_graph(x,endToEndDelay,x_name,'End-To-End Delay (s)',f'End-To-End Delay vs. {x_name}')
    plot_graph(x,packetDeliveryRatio,x_name,'Packet Delivery Ratio',f'Packet Delivery Ratio vs. {x_name}')
    plot_graph(x,packetDropRatio,x_name,'Packet Drop Ratio',f'Packet Drop Ratio vs. {x_name}')


# Read the data from the CSV file
node_num_data = pd.read_csv(f'node_num_{type}.csv')
node_speed_data = pd.read_csv(f'node_speed_{type}.csv')
packet_rate_data = pd.read_csv(f'packet_rate_{type}.csv')

# Extract and plot the data
extract_and_plot(node_num_data,'Number of Nodes')
extract_and_plot(node_speed_data,'Node Speed (m/s)')
extract_and_plot(packet_rate_data,'Packet Rate (packets/s)')
