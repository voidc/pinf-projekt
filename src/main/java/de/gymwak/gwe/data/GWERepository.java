package de.gymwak.gwe.data;

import org.springframework.data.repository.CrudRepository;

import de.gymwak.gwe.model.GWEUser;

public interface GWERepository extends CrudRepository<GWEUser, Long> {
	GWEUser findByEmail(String email);
}