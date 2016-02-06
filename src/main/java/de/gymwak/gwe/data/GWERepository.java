package de.gymwak.gwe.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.gymwak.gwe.model.GWEUser;

public interface GWERepository extends PagingAndSortingRepository<GWEUser, Long> {
	GWEUser findByEmail(String email);
	GWEUser findByResetToken(String resetToken);
}