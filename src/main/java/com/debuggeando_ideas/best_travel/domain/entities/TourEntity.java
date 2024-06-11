package com.debuggeando_ideas.best_travel.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity(name= "tour")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            mappedBy = "tour"
    )
    private Set<ReservationEntity> reservations;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            mappedBy = "tour"
    )
    private Set<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "id_customer")
    private CustomerEntity customer;

    @PrePersist
    @PreRemove
    public void updateFk(){
        this.reservations.forEach(reservation -> reservation.setTour(this));
        this.tickets.forEach(ticket -> ticket.setTour(this));
    }

    public void removeTicket(UUID id) {
        this.tickets.forEach(ticket ->{
            if(ticket.getId().equals(id)){
                ticket.setTour(null);
            }
        });
    }

    public void addTicket(TicketEntity ticketToAdd){
        if(Objects.isNull(this.tickets))  this.tickets = new HashSet<>();

        this.tickets.add(ticketToAdd);
        this.tickets.forEach(ticket -> ticket.setTour(this));
    }

}
