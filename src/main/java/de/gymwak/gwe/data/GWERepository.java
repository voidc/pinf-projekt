package de.gymwak.gwe.data;

import de.gymwak.gwe.model.GWEUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GWERepository extends PagingAndSortingRepository<GWEUser, Long> {
    GWEUser findByEmail(String email);

    GWEUser findByResetToken(String resetToken);

    List<GWEUser> findByGraduationYear(int graduationYear);

    GWEUser findByActivationToken(String token);

    List<GWEUser> findByGraduationYearAndGraduationTypeAndIdNot(int graduationYear,
                                                                GWEUser.GraduationType graduationType, long id, Sort sort);
}
