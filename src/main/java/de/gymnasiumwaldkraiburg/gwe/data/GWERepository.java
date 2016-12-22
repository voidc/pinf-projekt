package de.gymnasiumwaldkraiburg.gwe.data;

import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.stream.Stream;

public interface GWERepository extends PagingAndSortingRepository<GWEUser, Long> {
    GWEUser findByEmail(String email);

    GWEUser findByResetToken(String resetToken);

    List<GWEUser> findByGraduationYear(int graduationYear);

    @Query("select distinct u.graduationYear from GWEUser u order by u.graduationYear")
    List<Integer> findDistinctGraduationYears();

    @Query("select u from GWEUser u")
    Stream<GWEUser> streamAllSorted(Sort sort);

    GWEUser findByActivationToken(String token);

    List<GWEUser> findByGraduationYearAndGraduationTypeAndIdNot(int graduationYear,
                                                                GWEUser.GraduationType graduationType, long id, Sort sort);
}
