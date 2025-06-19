package com.aim.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.Company;
import com.aim.entity.User;

@Repository("userRepository")
@Transactional
public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByEmail(String email);

	User findByUuid(String key);
	
	List<User> findByFirstNameStartsWith(String startsWith);

	List<User> findByRole(String role);

	Integer countByRole(String role);
	
	List<User> findByRoleOrRole(String string, String string2);

	List<User> findByRoleAndActive(String string, int i);

	List<User> findByCompany(Company company);

	Page<User> findAll(Pageable pageable);

	User findByFileFolder(String name);

	Page<User> findByFirstNameStartsWithIgnoreCase(String startsWith, Pageable pageable);

	User findByEmailAndActive(String email, int active);
	
}
