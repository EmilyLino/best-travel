package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.TourRequest;
import com.debuggeando_ideas.best_travel.api.models.response.TourResponse;
import com.debuggeando_ideas.best_travel.domain.entities.*;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.TourRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITourService;
import com.debuggeando_ideas.best_travel.infraestructure.helper.TourHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class TourService implements ITourService {

    private final TourRepository tourRepository;
    private final FlyRepository flyRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final TourHelper tourHelper;

    @Override
    public void removeTicket(UUID ticketId, Long tourId) {
        var tourToUpdate = this.tourRepository.findById(tourId).orElseThrow();
        tourToUpdate.removeTicket(ticketId);
        this.tourRepository.save(tourToUpdate);
    }

    @Override
    public UUID addTicket(Long flyId, Long tourId) {
        var tourToUpdate = this.tourRepository.findById(tourId).orElseThrow();
        var fly = this.flyRepository.findById(flyId).orElseThrow();
        var ticket = tourHelper.createTicket(fly,tourToUpdate.getCustomer());
        tourToUpdate.addTicket(ticket);
        this.tourRepository.save(tourToUpdate);

        return ticket.getId();
    }

    @Override
    public void removeReservation(UUID reservationId, Long TourId) {

    }

    @Override
    public UUID addReservation(Long reservationId, Long tourId) {
        return null;
    }

    @Override
    public TourResponse create(TourRequest request) {
        var customer = this.customerRepository.findById(request.getCustomerId()).orElseThrow();
        var flights = new HashSet<FlyEntity>();
        var hotels = new HashMap<HotelEntity, Integer>();

        request.getFlights().forEach(fly-> flights.add(this.flyRepository.findById(fly.getId()).orElseThrow()));
        request.getHotels().forEach(hotel-> hotels.put(this.hotelRepository.findById(hotel.getId()).orElseThrow(), hotel.getTotalDays()));

        var tourToSave = TourEntity.builder()
                .tickets(tourHelper.createTickets(flights,customer))
                .reservations((tourHelper.createReservations(hotels,customer)))
                .customer(customer)
                .build();
        var tourSaved = this.tourRepository.save(tourToSave);

        return TourResponse.builder()
                .reservationIds(tourSaved.getReservations().stream().map(ReservationEntity::getId).collect(Collectors.toSet()))
                .ticketsIds(tourSaved.getTickets().stream().map(TicketEntity::getId).collect(Collectors.toSet()))
                .id(tourSaved.getId())
                .build();
    }

    @Override
    public TourResponse read(Long id) {
        var tourFromDb = this.tourRepository.findById(id).orElseThrow();
        return TourResponse.builder()
                .reservationIds(tourFromDb.getReservations().stream().map(ReservationEntity::getId).collect(Collectors.toSet()))
                .ticketsIds(tourFromDb.getTickets().stream().map(TicketEntity::getId).collect(Collectors.toSet()))
                .id(tourFromDb.getId())
                .build();
    }

    @Override
    public void delete(Long id) {
        var tourToDelete = this.tourRepository.findById(id).orElseThrow();
        this.tourRepository.delete(tourToDelete);
    }

    private TourResponse entityToResponse(TourEntity entity){
        TourResponse response = new TourResponse();
        BeanUtils.copyProperties(entity,response);
        return  response;
    }
}
