/* B.java
 * This code serves as the receiver in a rdt3.0 Stop-And-Wait Reliable 
 * Data Transfer implementation. It is resposible for sending an ACK 
 * (Acknowledgement) packet if a packet was correctly received, or a 
 * NAK (Negative Acknowledgement) packet if the packet received was 
 * an ACK packet, a corrupted packet, or was received out of order. 
 * It will send the payload of the packet to a simulated level 5 OSI 
 * layer if the packet was correctly received.
 * 
 * By: Rigo Sandoval
 */

public class B {
    int seq;
    int oldAck;
    public B(){
        /* stop and wait, the initialization of B
        // The state only need to maintain the information of expected sequence number of packet
         */
        this.seq = 0;
    }
    public void B_input(simulator sim,packet p){

        /* stop and wait, B_input
        // you need to verify the checksum to make sure that packet isn't corrupted
        // If the packet is the right one, you need to pass to the fifth layer using "sim.to_layer_five(entity, payload)"
        // Send acknowledgement using "send_ack(sim, entity, seq)" of packet based on the correctness of received packet
        // If the packet is the correct one, in the last step, you need to update its state ( update the expected sequence number)
         */

        // check if packet is corrupted
        if(p.checksum == p.get_checksum()){                         // if packet is NOT corrupted

            // check if packet is a duplicate or ACK packet
            if(seq == p.acknum && p.payload[0] != 0){               //      if packet is NOT a duplicate or ACK packet
                sim.to_layer_five('B', p.payload);                  //          send the payload to layer 5
                p.send_ack(sim, 'B', p.acknum);                     //          send ACK for correctly recovered packet
                if(p.seqnum == 0){                                  //          update the expected sequence number
                    seq = 1;
                } else {
                    seq = 0;
                }
            } else {                                                //      otherwise
                p.send_ack(sim, 'B', oldAck);                       //          send duplicate ACK of last packet sent (send NAK)
            }
        } else {                                                    // if packet IS corrupted
            if(seq == 0){                                           //      update the last packet's ACK number
                oldAck = 1;
            } else {
                oldAck = 0;
            }
            p.send_ack(sim, 'B', oldAck);                           //      send duplicate ACK of last packet sent (send NAK)
        }
    }
    public void B_output(simulator sim){

    }
    public void B_handle_timer(simulator sim){

    }
}
