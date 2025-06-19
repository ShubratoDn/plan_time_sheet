package com.aim.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.enums.W2OrC2cType;

@Repository
@Transactional
public interface UserDetailsRepository extends CrudRepository<UserDetail, Integer> {

	List<UserDetail> findByUser(User user);

	UserDetail findByUserDetailId(int userDetailsId);

	Iterable<UserDetail> findByUserId(Integer user);

	Iterable<UserDetail> findByW2C2cType(W2OrC2cType w2);

	UserDetail findByFileFolder(String urlSlug);

}
