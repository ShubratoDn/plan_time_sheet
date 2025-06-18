package com.aim.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.Signature;

@Repository
public interface SignatureRepository extends CrudRepository <Signature, Integer>{

}
