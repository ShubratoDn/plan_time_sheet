package com.aim.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.aim.entity.Client;
import com.aim.entity.Manager;
import com.aim.enums.ClientType;

public interface ManagerRepository extends CrudRepository<Manager,Integer>{

	Manager findByEmail(String email);

	List<Manager> findByClient(Client client);

	List<Manager> findByClientType(ClientType type);

}
