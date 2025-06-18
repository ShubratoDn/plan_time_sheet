package com.aim.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.InternalUser;

@Repository
@Transactional
public interface InternalUserRepository extends CrudRepository<InternalUser,Integer>{

	List<InternalUser> findAllByInternalUserType(String urlParam);

	InternalUser findByEmail(String email);

	List<InternalUser> findAllByDefaultUser(boolean b);

	List<InternalUser> findAllByInternalUserTypeOrderByDefaultUserDesc(String urlParam);

}
