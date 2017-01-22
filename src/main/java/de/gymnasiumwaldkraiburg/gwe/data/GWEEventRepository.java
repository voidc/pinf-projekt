package de.gymnasiumwaldkraiburg.gwe.data;

import de.gymnasiumwaldkraiburg.gwe.model.GWEEvent;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GWEEventRepository extends PagingAndSortingRepository<GWEEvent, Long> {
    List<GWEEvent> findByOrganizerId(long organizerId);
}
