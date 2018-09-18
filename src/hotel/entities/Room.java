package hotel.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;

public class Room {
	
	private enum State {READY, OCCUPIED}
	
	int id;
	RoomType roomType;
	List<Booking> bookings;
	State state;

	
	public Room(int id, RoomType roomType) {
		this.id = id;
		this.roomType = roomType;
		bookings = new ArrayList<>();
		state = State.READY;
	}
	

	public String toString() {
		return String.format("Room : %d, %s", id, roomType);
	}


	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return roomType.getDescription();
	}
	
	
	public RoomType getType() {
		return roomType;
	}
	
	public boolean isAvailable(Date arrivalDate, int stayLength) {
		IOUtils.trace("Room: isAvailable");
		for (Booking b : bookings) {
			if (b.doTimesConflict(arrivalDate, stayLength)) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean isReady() {
		return state == State.READY;
	}


	public Booking book (Guest guest, Date arrivalDate, int stayLength, int numberOfOccupants, CreditCard creditCard) { //method to create a new booking.
		Booking booking = new Booking (guest, arrivalDate, stayLength, numberOfOccupants, creditCard); //creates a new booking named booking.
		bookings.add(booking); //Inserts booking into bookings.
		return booking; //returns booking.
	} //method ends.


	public void checkin() { //method to checkin.
		if (state != State.READY) { //executes if statement when state is not set to ready.
            String except = String.format ("State is not ready so it throws RuntimeException message named except", new Object[] {state}); //Gives the exception message and it is named as except.
            throw new RuntimeException(except); //throws the RuntimeException message except.
        } //if statement ends.
		
		else { //executes else statement when state is set to ready.
            state = State.OCCUPIED; //sets the state to occupied. 
            return;
        } //else statement ends.
	} //method ends.


	public void checkout (Booking booking) { //method to checkout which takes the booking created as an argument.
		if (state != State.OCCUPIED) { //executes if statement when state is not set to occupied.
            String except = String.format ("State is not occupied so it throws RuntimeException message named except", new Object[] {state}); //Gives the exception message and it is named as except.
            throw new RuntimeException(except); //throws the RuntimeException message except.
        } //if statement ends.
		
		else{ //executes else statement when state is set to occupied.
            bookings.remove(booking); //removes booking from bookings.
            state = State.READY; //sets the state again to ready.
            return;
        } //else statement ends.
	} //method ends.


}
