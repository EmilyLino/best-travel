package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.request.TourRequest;
import com.debuggeando_ideas.best_travel.api.models.response.TourResponse;

import java.util.UUID;

public interface ITourService extends SimpleCrudService<TourRequest, TourResponse,Long>{
    void removeTicket(UUID ticketId, Long TourId);
    UUID addTicket(Long flyId, Long tourId);

    void removeReservation(UUID reservationId, Long TourId);
    UUID addReservation(Long reservationId, Long tourId);

}
