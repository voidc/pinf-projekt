package de.gymwak.gwe.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.gymwak.gwe.model.GWEEvent;

public interface GWEEventRepository extends PagingAndSortingRepository<GWEEvent, Long> {
	List<GWEEvent> findByOrganizerId(long organizerId);
}