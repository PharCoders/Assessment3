package hotel.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;

public class Hotel {
	
	private Map<Integer, Guest> guests;
	public Map<RoomType, Map<Integer,Room>> roomsByType;
	public Map<Long, Booking> bookingsByConfirmationNumber;
	public Map<Integer, Booking> activeBookingsByRoomId;
	
	
	public Hotel() {
		guests = new HashMap<>();
		roomsByType = new HashMap<>();
		for (RoomType rt : RoomType.values()) {
			Map<Integer, Room> rooms = new HashMap<>();
			roomsByType.put(rt, rooms);
		}
		bookingsByConfirmationNumber = new HashMap<>();
		activeBookingsByRoomId = new HashMap<>();
	}

	
	public void addRoom(RoomType roomType, int id) {
		IOUtils.trace("Hotel: addRoom");
		for (Map<Integer, Room> rooms : roomsByType.values()) {
			if (rooms.containsKey(id)) {
				throw new RuntimeException("Hotel: addRoom : room number already exists");
			}
		}
		Map<Integer, Room> rooms = roomsByType.get(roomType);
		Room room = new Room(id, roomType);
		rooms.put(id, room);
	}

	
	public boolean isRegistered(int phoneNumber) {
		return guests.containsKey(phoneNumber);
	}

	
	public Guest registerGuest(String name, String address, int phoneNumber) {
		if (guests.containsKey(phoneNumber)) {
			throw new RuntimeException("Phone number already registered");
		}
		Guest guest = new Guest(name, address, phoneNumber);
		guests.put(phoneNumber, guest);		
		return guest;
	}

	
	public Guest findGuestByPhoneNumber(int phoneNumber) {
		Guest guest = guests.get(phoneNumber);
		return guest;
	}

	
	public Booking findActiveBookingByRoomId(int roomId) {
		Booking booking = activeBookingsByRoomId.get(roomId);;
		return booking;
	}


	public Room findAvailableRoom(RoomType selectedRoomType, Date arrivalDate, int stayLength) {
		IOUtils.trace("Hotel: checkRoomAvailability");
		Map<Integer, Room> rooms = roomsByType.get(selectedRoomType);
		for (Room room : rooms.values()) {
			IOUtils.trace(String.format("Hotel: checking room: %d",room.getId()));
			if (room.isAvailable(arrivalDate, stayLength)) {
				return room;
			}			
		}
		return null;
	}

	
	public Booking findBookingByConfirmationNumber(long confirmationNumber) {
		return bookingsByConfirmationNumber.get(confirmationNumber);
	}


	//this method returns a unique confirmation number for a booking.
public long book(Room room, Guest guest, Date arrivalDate, int stayLength, int occupantNumber, CreditCard creditCard) {
        Booking booking = room.book(guest, arrivalDate, stayLength, occupantNumber, creditCard); //calls room.book.
        long confirmationNumber = booking.getConfirmationNumber();
        bookingsByConfirmationNumber.put(Long.valueOf(confirmationNumber), booking); //it inserts booking.
        return confirmationNumber; //returns confirmation number.
    }

	
	// this method throws runtimeException if no booking for the perticular confirmation number exits.
public void checkin(long confirmationNumber) {
        Booking booking = (Booking)bookingsByConfirmationNumber.get(Long.valueOf(confirmationNumber)); //extracts bookings from bookingByConfirmationNumber.
        if(booking == null) {
            String errordialogue = String.format("error message", new Object[] {
				Long.valueOf(confirmationNumber)});
            throw new RuntimeException(errordialogue); //if booking is null it throws runtimeexception.
        } 
		else {
            int roomId = booking.getRoomId(); //else calls booking.getRoomId.
            booking.checkIn(); //calls booking.checkIn
            activeBookingsByRoomId.put(Integer.valueOf(roomId), booking); //Inserts booking in activeBookingsByRoomId
            return;
        }
    }

	//throws a RuntimeException if no active booking associated with the room identified by roomID can be found
public void addServiceCharge(int roomId, ServiceType serviceType, double cost) {
        Booking booking = (Booking)activeBookingsByRoomId.get(Integer.valueOf(roomId)); // it extracts booking from activeBookingsByRoomId.
        if(booking == null) {
            String errordialogue = String.format("error message", new Object[] {
				Integer.valueOf(roomId)});
            throw new RuntimeException(errordialogue); //if booking is null it throws RumtimeException.
        } 
		else{
            booking.addServiceCharge(serviceType, cost); //else calls addServiceCharge.
            return;
        }
    }
	
// throws a RuntimeException if no active booking associated with the room identified by roomID can be found
//The Booking referenced by confirmationNumber should have a state of CHECKED_OUT
public void checkout(int roomId) {
        Booking booking = (Booking)activeBookingsByRoomId.get(Integer.valueOf(roomId)); //it extracts booking from activeBookingsByRoomId.
        if(booking == null) {
            String errordialogue = String.format("error message", new Object[] {
				Integer.valueOf(roomId)});
            throw new RuntimeException(errordialogue); //if booking is null it throws RumtimeException
        } 
		else{
            booking.checkOut(); //calls booking.checkout
            activeBookingsByRoomId.remove(Integer.valueOf(roomId)); it removes booking from activeBookingByRoomId
            return;
        }
    }