package com.aim.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.User;
import com.aim.entity.UserFile;

@Repository
@Transactional
public interface UserFileRepository extends CrudRepository<UserFile, Integer> {

	List<UserFile> findByUser(User user);

}
