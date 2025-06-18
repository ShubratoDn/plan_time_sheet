package com.aim.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.Template;
import com.aim.enums.MailTemplateType;

@Repository
public interface TemplateRepository extends CrudRepository<Template,Integer>{

	List<Template> findByMailTemplateType(MailTemplateType admintimesheetsubmit);

	List<Template> findByMailTemplateTypeAndRoleAdminPermission(MailTemplateType mailTemplateType, boolean b);

	List<Template> findByMailTemplateTypeAndRoleUserPermission(MailTemplateType mailTemplateType, boolean b);

	List<Template> findByMailTemplateTypeAndRoleSupervisorPermission(MailTemplateType mailTemplateType, boolean b);

	Template findByTemplateName(String templateName);

}
