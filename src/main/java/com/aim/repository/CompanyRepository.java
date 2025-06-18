package com.aim.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.Company;

@Repository
@Transactional
public interface CompanyRepository extends CrudRepository <Company, Integer>{

	Company findByUrlSlug(String urlSlug);

	Company findByName(String name);

	Company findByUuid(String cId);

	Company findFirstByVarifyAndActive(boolean b, boolean c);

	Company findFirstByVarifyOrActiveOrVarifyOrActive(boolean b, boolean c, boolean d, boolean e);

	Company findByFileFolder(String fileFolder);

}
