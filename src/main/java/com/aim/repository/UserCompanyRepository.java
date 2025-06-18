package com.aim.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.entity.UserCompany;

@Repository
@Transactional
public interface UserCompanyRepository extends CrudRepository<UserCompany,Integer>{

	UserCompany findByCompanyAndUser(Company company, User user);

	List<UserCompany> findByUser(User loginUser);
	
	List<UserCompany> findByRole(String role);

	List<UserCompany> findByUserAndCompanyVarify(User loginUser, boolean b);

	List<UserCompany> findByCompany(Company company);

	@Query("SELECT u.company.dbName FROM UserCompany u where u.user.email= :email AND u.company.varify = true")
	List<String> getDbNameByUser(@Param("email") String user);

	List<UserCompany> findByCompanyIdAndRole(Integer companyId, String string);
	
	List<UserCompany> findByUserAndCompanyVarifyAndCompanyActive(User loginUser, boolean b, boolean c);

}
