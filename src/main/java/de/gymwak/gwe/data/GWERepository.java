package de.gymwak.gwe.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.gymwak.gwe.model.GWEUser;

public interface GWERepository extends PagingAndSortingRepository<GWEUser, Long> {
	GWEUser findByEmail(String email);

	Iterable<GWEUser> findByGraduationYear(int graduationYear);

	Iterable<GWEUser> findByEmailContainingIgnoreCase(String email);

	Iterable<GWEUser> findByFirstNameContainingIgnoreCase(String firstName);

	Iterable<GWEUser> findByLastNameContainingIgnoreCase(String lastName);

	Iterable<GWEUser> findByGraduationYearContaining(int graduationYear);

	Iterable<GWEUser> findByOccupationContainingIgnoreCase(String occupation);
}