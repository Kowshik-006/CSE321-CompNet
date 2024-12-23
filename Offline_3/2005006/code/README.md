I have modified the TCP BIC congestion protocol.
In TCP BIC, in the congestion control phase, a Binary Search Coefficient is used to find the optimal number of acknowledgments required to increase the window size.
This is represented by the variable m_b in the code.
In the original code, the value of this variable is statically assigned to 4, and the data type is uint32. I have made the datatype of the variable double, which 
allows us to use a lot more values for m_b.
Similarly, after congestion the window size is decreased and a factor called beta is used in this process. The value of the variable beta is also statically assigned
to 0.8. 

I made the values set to these variables dynamic.
The function below does the job.

void
TcpBicTweaked::ChangeBeta_BinCoeff()
{
    Time currentTime = Simulator::Now();
    uint32_t timeToGetCongested = currentTime.GetMilliSeconds() - m_epochStart.GetMilliSeconds();

    if(m_epochStart == Time::Min()){
        m_beta = 0.6;
        m_b = 3;
    }
    else if(timeToGetCongested > 4000){
        m_beta = 0.9;
        m_b = 6;
    }
    else if(timeToGetCongested > 3500){
        m_beta = 0.88;
        m_b = 6;
    }
    else if(timeToGetCongested > 3000){
        m_beta = 0.86;
        m_b = 5;
    }
    else if(timeToGetCongested > 2500){
        m_beta = 0.84;
        m_b = 5;
    }
    else if(timeToGetCongested > 2000){
        m_beta = 0.82;
        m_b = 4;
    }
    else if(timeToGetCongested > 1500){
        m_beta = 0.8;
        m_b = 4;
    }
    else{
        m_beta = 0.8;
        m_b = 3.5;
    }
}

Here, I am checking the time at which congestion has occurred. Basically the m_epochstart is initialized after the end of the slow start phase. 
And that phase only occurs once at the beginning. After that, after each congestion the window size is set to the value of the ssthresh. 
The first if block basically, handles the first congestion. In order to avoid back-to-back congestion, the value of beta is set to a lower value.
Hence, the window size shrinks to a lower size, which prevents another back-to-back congestion. 
If we see that, congestion is occurring after a long time then we conclude that the condition of the link is quite good, so we do not shrink the window
much and keep m_b high, which makes the growth faster. Inversely, when we see that congestion is happening quite frequently, we shrink the window more 
by making beta lower and slow the growth of the window by making m_b lower.  
