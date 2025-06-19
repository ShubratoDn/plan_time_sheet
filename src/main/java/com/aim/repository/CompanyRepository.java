package com.aim.repository;

import com.aim.entity.Company;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
