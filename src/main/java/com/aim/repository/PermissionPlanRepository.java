package com.aim.repository;

import org.springframework.data.repository.CrudRepository;

import com.aim.entity.Company;
import com.aim.entity.PermissionPlan;

public interface PermissionPlanRepository extends CrudRepository<PermissionPlan,Integer>{

	PermissionPlan findByCompany(Company company);

	PermissionPlan findByCompanyUrlSlug(String urlSlug);

}
