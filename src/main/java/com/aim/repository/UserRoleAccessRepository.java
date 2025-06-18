package com.aim.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.UserRoleAccess;
import com.aim.enums.Functionality;

@Repository
public interface UserRoleAccessRepository extends CrudRepository<UserRoleAccess, Integer> {

	UserRoleAccess findByRoleAndFunctionality(String role, Functionality functionality);

}
