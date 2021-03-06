package hotel.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;

public class Booking {
	
	private enum State {PENDING, CHECKED_IN, CHECKED_OUT};
	
	private Guest guest;
	private Room room;
	private Date bookedArrival; 
	private int stayLength;
	int numberOfOccupants;
	long confirmationNumber;
	CreditCard creditCard;
	
	private List<ServiceCharge> charges;
	
	private State state;


	
	public Booking(Guest guest, Room room, 
			Date arrivalDate, int stayLength, 
			int numberOfOccupants, 
			CreditCard creditCard) {
		
		this.guest = guest;
		this.room = room;
		this.bookedArrival = arrivalDate;
		this.stayLength = stayLength;
		this.numberOfOccupants = numberOfOccupants;
		this.confirmationNumber = generateConfirmationNumber(room.getId(), arrivalDate);
		this.creditCard = creditCard;
		this.charges = new ArrayList<>();
		this.state = State.PENDING;
	}

	
	private long generateConfirmationNumber(int roomId, Date arrivalDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(arrivalDate);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		String numberString = String.format("%d%d%d%d", day, month, year, roomId);
		
		return Long.parseLong(numberString);
	}


	public boolean doTimesConflict(Date requestedArrival, int stayLength) {
		IOUtils.trace("Booking: timesConflict");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(bookedArrival);
		calendar.add(Calendar.DATE, stayLength);
		Date bookedDeparture = calendar.getTime();
		
		calendar.setTime(requestedArrival);
		calendar.add(Calendar.DATE, stayLength);
		Date requestedDeparture = calendar.getTime();
		
		boolean doesConflict = requestedArrival.before(bookedDeparture) && 
				requestedDeparture.after(bookedArrival);

		return doesConflict;
	}


	public long getConfirmationNumber() {
		return confirmationNumber;
	}


	public int getRoomId() {
		return room.getId();
	}
	
	
	public Room getRoom() {
		return room;
	}


	public Date getArrivalDate() {
		return bookedArrival;
	}


	public int getStayLength() {
		return stayLength;
	}


	public Guest getGuest() {
		return guest;
	}


	public CreditCard getCreditCard() {
		return creditCard;
	}


	public boolean isPending() {
		return state == State.PENDING;
	}


	public boolean isCheckedIn() {
		return state == State.CHECKED_IN;
	}


	public boolean isCheckedOut() {
		return state == State.CHECKED_OUT;
	}


	public List<ServiceCharge> getCharges() {
		return Collections.unmodifiableList(charges);
	}


	public void checkIn() {
		
		if(state != State.PENDING){		//If the state is not pending
            String msg = String.format("Booking: checkIn : bad state : %s", new Object[] {state});		//Setting the Exception message
            throw new RuntimeException(msg);	//Throws the RunTimeException error message
        } //if
		else{		//If pending
			room.checkin();		//Calls the checkin method in room class
			state = State.CHECKED_IN;		//Sets the state to checkedIn
            return;	
        } //else
	} //checkIn()


	public void addServiceCharge(ServiceType serviceType, double cost) {
		
		if(state != State.CHECKED_IN){		//If the state is not checked in 
            String msg = String.format("Booking: checkOut : bad state : %s", new Object[] {state});		//Setting the Exception message
            throw new RuntimeException(msg);	//Throws the RunTimeException error message
        } //if
		
		else{		//If checked in
			ServiceCharge charge = new ServiceCharge(serviceType, cost);	//Creates new ServiceCharge object
            charges.add(charge);	//Adds the ServiceCharge to charges List
            return;	
        } //else
			
	} //addServiceCharge


	public void checkOut() {
		
		if(state != State.CHECKED_IN){		//If the state is not checked in 
            String msg = String.format("Booking: checkOut : bad state : %s", new Object[] {state});		//Setting the Exception message
            throw new RuntimeException(msg);	//Throws the RunTimeException error message
        } //if
		
		else{		//If checked in
			room.checkout(this);		//Calls the checkout method in room class
			state = State.CHECKED_OUT;		//Sets the state to checked out
            return;	
        } //else
			
	} //checkOut()

}
