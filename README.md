# Reliable Data Transfer
### go-back-n implementation

Rigo Sandoval, CSCI4211S23, 3/27/2023
Java, A.java B.java,, simulator.java

## Compilation
1. Download the following files in the same directory:
    - A.java
    - B.java
    - circular_buffer.java
    - event.java
    - event_list.java
    - msg.java
    - packet.java
    - simulator.java
2. Open up the command line terminal
3. Navigate to the directory using the command line
4. Run "javac simulator.java" in the command line

## Execution/Running
1. Run "java simulator" in the command line
The simulator will immediately start providing the sender A with message data to send

## Description
This Java code implements the Go-Back-N Reliable Data Transfer protocol. A.java contains code for the sender and B.java contains code for the receiver. The file simulator.java contains code that will simulate layer 5 sending/receiving data to the sender/receiver as well as a layer 3 channel in which the probability of a packet being dropped or corrupted can be adjusted.

### Sender Code (A.java)
The sender waits on the simulated layer 5 for a message to send. Once it receives one, it will create a packet using the packet structure, place the packet in a circular buffer, increment the next packet sequence number, and send the packet to the receiver via a sim.to_layer3() call. It will also start a timer to resend unACK'd packets if an ACK is not received within the estimated round-trip-time.

If a packet is received by the sender, it will check if the packet received has been corrupted, or is a NAK packet. If not, it will remove the ACK'd packets from the window and wait for the next message to send. If the packet received has been corrupted, it will send a NAK to the receiver. If the packet received is a NAK packet, it will check its window for the least numbered, un-ACK'd packet and resend the packet and those that come after it.

#### Functions/Methods implemented
- **Initialization of A:**
    The sender was initialized with an outgoing packet sequence number of 0, and expected acknowledgement packet number equal to the sequence number, and an estimated round-trip-time of 30 milliseconds. The circular buffer used for the window is defined.
- **A_output():**
    Upon receiving a message from the simulated layer 5, this function will create a packet structure with a seqnum and acknum of the current sender's sequence number. It will place the packet in the window and send the packet to the receiver via a sim.to_layer3() call, start the timer, and increment the sequence number for the next packet sent.
- **A_input():**
    Upon receiving a message from the receiver, this function will stop the timer, check if the packet has been corrupted, check if the ACK received is greater than or equal to what is was expecting. If both of these conditions are met, the sender will remove the packets with sequence numbers less than or equal to the ACK packet number from the window and wait for the next message to send. If the packet received was an out-of-order packet, it will check its window for the least numbered, un-ACK'd packet and resend the packet and those that come after it. If the packet received was corrupted, it will send a NAK to the receiver and restart the timer.
- **A_handle_timer():**
    This function resends any unACK'd packets in the window and restarts the timer.

### Receiver Code (B.java)
The receiver code is extremely simple by comparison. The receiver will check if the packet received is in order and not corrupted. If it is, it will send an ACK to the sender and send to packet payload to the simulated layer 5. If not, it will send a NAK to the sender.

#### Functions/Methods implemented
- **Initialization of B:**
    The receiver is initialized to expect a packet with sequence number 0.
- **B_output():**
    This function will check if the packet received is not corrupted and expected. If so, it will send the payload of the packet to the simulated layer 5 and send an ACK to the sender. If not, it will send a NAK to the sender.

### Evaluation
#### Test Cases:
##### No lost packets, no corrupted packets
Edit simulator.java variables **lossprob** = 0.0 and **corruptprob** = 0.0.
No lost or corrupted packets, so all messages should be received correctly.
Console Output:

    recieving aaaaaaaaaaaaaaaaaaaa
    recieving bbbbbbbbbbbbbbbbbbbb
    recieving cccccccccccccccccccc
    recieving dddddddddddddddddddd
    recieving eeeeeeeeeeeeeeeeeeee
    recieving ffffffffffffffffffff
    recieving gggggggggggggggggggg
    recieving hhhhhhhhhhhhhhhhhhhh
    recieving iiiiiiiiiiiiiiiiiiii
    recieving jjjjjjjjjjjjjjjjjjjj
    recieving kkkkkkkkkkkkkkkkkkkk
    recieving llllllllllllllllllll
    recieving mmmmmmmmmmmmmmmmmmmm
    recieving nnnnnnnnnnnnnnnnnnnn
    recieving oooooooooooooooooooo
    recieving pppppppppppppppppppp
    recieving qqqqqqqqqqqqqqqqqqqq
    recieving rrrrrrrrrrrrrrrrrrrr
    recieving ssssssssssssssssssss
    The simulator has sent enough packets. Simulation end

##### Packets lost, no corrupted packets
Edit simulator.java variables **lossprob** = 0.9 and **corruptprob** = 0.0.
stop-and-wait protocol completely recovers from packet loss by resending the lost packet after the timer expires and/or sending NAK packets.
Console Output:

    recieving aaaaaaaaaaaaaaaaaaaa
    recieving bbbbbbbbbbbbbbbbbbbb
    recieving cccccccccccccccccccc
    recieving dddddddddddddddddddd
    recieving eeeeeeeeeeeeeeeeeeee
    recieving ffffffffffffffffffff
    recieving gggggggggggggggggggg
    recieving hhhhhhhhhhhhhhhhhhhh
    recieving iiiiiiiiiiiiiiiiiiii
    recieving jjjjjjjjjjjjjjjjjjjj
    recieving kkkkkkkkkkkkkkkkkkkk
    recieving llllllllllllllllllll
    recieving mmmmmmmmmmmmmmmmmmmm
    recieving nnnnnnnnnnnnnnnnnnnn
    recieving oooooooooooooooooooo
    recieving pppppppppppppppppppp
    recieving qqqqqqqqqqqqqqqqqqqq
    recieving rrrrrrrrrrrrrrrrrrrr
    recieving ssssssssssssssssssss
    The simulator has sent enough packets. Simulation end

##### No lost packets, packets corrupted
Edit simulator.java variables **lossprob** = 0.0 and **corruptprob** = 0.8.
stop-and-wait protocol will competely recover from corrupted packets up until corruptprob of 0.7. After that, the window starts to fill up and drop packets, so the message received is incomplete. Here packets q, r, and s are missing.
Console Output:

    recieving aaaaaaaaaaaaaaaaaaaa
    recieving bbbbbbbbbbbbbbbbbbbb
    recieving cccccccccccccccccccc
    recieving dddddddddddddddddddd
    recieving eeeeeeeeeeeeeeeeeeee
    recieving ffffffffffffffffffff
    recieving gggggggggggggggggggg
    recieving hhhhhhhhhhhhhhhhhhhh
    recieving iiiiiiiiiiiiiiiiiiii
    recieving jjjjjjjjjjjjjjjjjjjj
    recieving kkkkkkkkkkkkkkkkkkkk
    recieving llllllllllllllllllll
    recieving mmmmmmmmmmmmmmmmmmmm
    recieving nnnnnnnnnnnnnnnnnnnn
    recieving oooooooooooooooooooo
    recieving pppppppppppppppppppp
    The simulator has sent enough packets. Simulation end

##### Packets lost, packets corrupted
Edit simulator.java variables **lossprob** = 0.9 and **corruptprob** = 0.8.
Again message recovery is dependent on the corruptprob variable because the window start to fill up and drop packets. Here packet s is missing.
Console Output:

    recieving aaaaaaaaaaaaaaaaaaaa
    recieving bbbbbbbbbbbbbbbbbbbb
    recieving cccccccccccccccccccc
    recieving dddddddddddddddddddd
    recieving eeeeeeeeeeeeeeeeeeee
    recieving ffffffffffffffffffff
    recieving gggggggggggggggggggg
    recieving hhhhhhhhhhhhhhhhhhhh
    recieving iiiiiiiiiiiiiiiiiiii
    recieving jjjjjjjjjjjjjjjjjjjj
    recieving kkkkkkkkkkkkkkkkkkkk
    recieving llllllllllllllllllll
    recieving mmmmmmmmmmmmmmmmmmmm
    recieving nnnnnnnnnnnnnnnnnnnn
    recieving oooooooooooooooooooo
    recieving pppppppppppppppppppp
    recieving qqqqqqqqqqqqqqqqqqqq
    recieving rrrrrrrrrrrrrrrrrrrr
    The simulator has sent enough packets. Simulation end
