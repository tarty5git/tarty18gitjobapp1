package com.cth.sdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cth.sdm.model.entity.ApplicantProfile;

public interface ApplicantProfileRepository extends JpaRepository<ApplicantProfile, Long> {
}