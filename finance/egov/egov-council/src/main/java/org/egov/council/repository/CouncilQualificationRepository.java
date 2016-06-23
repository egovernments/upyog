package org.egov.council.repository;

import org.egov.council.entity.CouncilQualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilQualificationRepository extends JpaRepository<CouncilQualification, java.lang.Long> {

    CouncilQualification findByName(String name);

}