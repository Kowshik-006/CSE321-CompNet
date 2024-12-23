import numpy as np
import matplotlib.pyplot as plt


file_name_prefix = "TcpVariantsComparison-flow"
metrics = ["cwnd","inflight","next-rx","next-tx","rto","rtt","ssth"]

for metric in metrics:
    x_values = []
    y_values = []
    for i in range(7):
        file_name = file_name_prefix + str(i) + "-" + metric + ".data"
        with open(file_name, "r") as file:
            for line in file:
                x,y = line.split()
                x_values.append(float(x))
                y_values.append(float(y))

    np_x = np.array(x_values)
    np_y = np.array(y_values)
    np_pairs = np.column_stack((np_x, np_y))
    np_pairs = np_pairs[np_pairs[:,0].argsort()]

    i = 0

    x = []
    y = []
    while i <= 100.1:
        mask = (np_pairs[:,0] >= i) & (np_pairs[:,0] < i+0.1)
        filtered_pairs = np_pairs[mask]
        x.append(i)
        if filtered_pairs.size == 0:
            y.append(0)
        else:
            y.append(np.mean(filtered_pairs[:,1]))
        
        i += 0.1

    plt.plot(x,y,linestyle='-',label=metric.upper())
    plt.xlabel('Time (s)')
    plt.ylabel(f'Average {metric.upper()} Value')
    plt.title(f'Average {metric.upper()} Across All Flows')
    plt.grid()
    plt.legend()
    plt.show()



