/* A.java
 * This code serves as the sender in a rdt3.0 Stop-And-Wait Reliable 
 * Data Transfer implementation. It receives data from the simulator 
 * and sends it through a simulated channel in which the probability 
 * of packets being lost and/or corrupted can be adjusted. It uses a 
 * timer to automatically resend a packet that may have been lost. 
 * 
 * If a packet received is an ACK (Acknowledgement) packet, it will 
 * change state and get ready to send the next message. If a packet 
 * received is a NAK (Negative Acknowledgement) packet, it will 
 * resend the last packet sent.
 * 
 * By: Rigo Sandoval
 */

public class A {
    String state;
    int seq;
    int estimated_rtt;
    packet lastpacket;
    public A(){
        /* stop and wait, the initialization of A
        // for stop and wait, the state can be "WAIT_LAYER5" or "WAIT_ACK"
        // "WAIT_LAYER5" is the state that A waits messages from application layer.
        // "WAIT_ACK" is the state that A waits acknowledgement
        // You can set the estimated_rtt to be 30, which is used as a parameter when you call start_timer
         */
        this.state = "WAIT_LAYER5";
        this.seq = 0;
        this.estimated_rtt = 30;
    }
    public void A_input(simulator sim, packet p){
        /* stop and wait A_input
        // the sim is the simulator. It is provided to call its method such as to_layer_three, start_timer and stop timer
        // p is the packet from the B
        // first verify the checksum to make sure that packet is uncorrupted
        // then verify the acknowledgement number to see whether it is the expected one
        // if not, you may need to resend the packet.
        // if the acknowledgement is the expected one, you need to update the state of A from "WAIT_ACK" to "WAIT_LAYER5" again
         */
        sim.envlist.remove_timer();                                             // packet received, stop the timer

        // check if packet is corrupted
        if(p.checksum == p.get_checksum()){

            // if packet (ACK) recieved is not corrupted, verify acknum
            if(p.acknum == lastpacket.acknum && this.state == "WAIT_ACK"){      // if packet acknum is expected (ACK)
                state = "WAIT_LAYER5";                                          //      update state of A from "WAIT_ACK" to "WAIT_LAYER5"

            } else {                                                            // if packet acknum is unexpected (NAK)
                sim.to_layer_three('A', lastpacket);                            //      resend the packet
                sim.envlist.start_timer('A', estimated_rtt);                    //      restart the timer
            }
        } else {
            // if packet (ACK) recieved is corrupted
            // send a duplicate ACK (NAK)
            // restart timer
            lastpacket.send_ack(sim, 'A', lastpacket.acknum);
            sim.envlist.start_timer('A', estimated_rtt);
        }
    }
    public void A_output(simulator sim, msg m){
        /* stop and wait A_output
        // the "sim" is the simulator. It is provided to call its method such as to_layer_three, start_timer and stop timer
        // msg m is the message. It should be used to construct the packet.
        // You can construct the packet using the "public packet(int seqnum,msg m)" in "packet.java".
        // save the packet so that it can be resent if needed.
        // Set the timer using "sim.envlist.start_timer", and the time should be set to estimated_rtt. Make sure that there is only one timer started in the event list.
        // In the end, you should change the state to "WAIT_ACK"
         */
        packet temp = new packet(seq, seq, m);              // create packet to send
        lastpacket = temp;                                  // save packet to resend if needed

        sim.to_layer_three('A', temp);                      // send packet
        sim.envlist.start_timer('A', estimated_rtt);        // start timer
        state = "WAIT_ACK";                                 // wait for ACK

        if(lastpacket.seqnum == 0){                         // alternate seqnum bit for next packet to send
            seq = 1;
        } else {
            seq = 0;
        }
    }
    public void A_handle_timer(simulator sim){
        /* stop and wait A_handle_timer
        // the sim is the simulator. It is provided to call its method such as to_layer_three, start_timer and stop timer
        // if it is triggered, it means the previous sent packet isn't delivered
        // so you need to resend the last packet
        // After sending the last packet, don't forget to set the timer again
         */
        sim.to_layer_three('A', lastpacket);                // packet lost, resend last packet
        sim.envlist.start_timer('A', estimated_rtt);        // restart timer
    }
}
