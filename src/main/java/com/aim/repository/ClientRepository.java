package com.aim.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.aim.entity.Client;
import com.aim.enums.ClientType;

public interface ClientRepository extends CrudRepository <Client, Integer> {

	List<Client> findByType(ClientType employee);

}

