package com.debuggeando_ideas.best_travel.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourResponse {
    private Long id;
    private Set<UUID> ticketsIds;
    private Set<UUID> reservationIds;
}
