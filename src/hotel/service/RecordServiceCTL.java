package hotel.service;

import hotel.entities.Booking;
import hotel.entities.Hotel;
import hotel.entities.ServiceType;
import hotel.utils.IOUtils;

public class RecordServiceCTL {
	
	private static enum State {ROOM, SERVICE, CHARGE, CANCELLED, COMPLETED};
	
	private Hotel hotel;
	private RecordServiceUI recordServiceUI;
	private State state;
	
	private Booking booking;
	private int roomNumber;


	public RecordServiceCTL(Hotel hotel) {
		this.recordServiceUI = new RecordServiceUI(this);
		state = State.ROOM;
		this.hotel = hotel;
	}

	
	public void run() {		
		IOUtils.trace("PayForServiceCTL: run");
		recordServiceUI.run();
	}


	public void roomNumberEntered(int roomNumber) {
		if (state != State.ROOM) {
			String mesg = String.format("PayForServiceCTL: roomNumberEntered : bad state : %s", state);
			throw new RuntimeException(mesg);
		}
		booking = hotel.findActiveBookingByRoomId(roomNumber);
		if (booking == null) {
			String mesg = String.format("No active booking for room id: %d", roomNumber);
			recordServiceUI.displayMessage(mesg);
		}
		else {
			this.roomNumber = roomNumber;
			state = State.SERVICE;
			recordServiceUI.setState(RecordServiceUI.State.SERVICE);
		}
	}
	
	
	public void serviceDetailsEntered(ServiceType serviceType, double cost) { //method to check service details entered which takes serviceType and cost as an argument.
        if (state != State.SERVICE) { //executes if statement when state is not set to service.
            String except = String.format("State is not service so it throws RuntimeException message named except", new Object[] {state}); //Gives the exception message named except.
            throw new RuntimeException(except); //throws RuntimeException message named except.
        } // if statement ends.
		else{ //executes else statement when state is set to service.
			hotel.addServiceCharge(roomNumber, serviceType, cost); //calls addServiceCharge.
            recordServiceUI.displayServiceChargeMessage(roomNumber, cost, serviceType.getDescription()); //calls ui displayServiceChargeMessage.
            state = State.COMPLETED; //sets the state to completed. 
            recordServiceUI.setState(RecordServiceUI.State.COMPLETED); //sets the state of ui to completed.
            return;
        } //else statement ends.
    } //method ends.


	public void cancel() {
		recordServiceUI.displayMessage("Pay for service cancelled");
		state = State.CANCELLED;
		recordServiceUI.setState(RecordServiceUI.State.CANCELLED);
	}


	public void completed() {
		recordServiceUI.displayMessage("Pay for service completed");
	}


	

}
