package com.aim.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.Activity;
import com.aim.entity.User;

@Repository
@Transactional
public interface ActivityRepository extends CrudRepository <Activity, Integer> {

	List<Activity> findAllByActivityByUserRoleOrderByIdDesc(String role);

	List<Activity> findAllByActivityByUserRoleAndActivityTypeOrderByIdDesc(String role, String type);

	List<Activity> findAllByActivityByUserOrderByIdDesc(User user);

	List<Activity> findAllByActivityByUserAndActivityTypeOrderByIdDesc(User user, String type);

}
