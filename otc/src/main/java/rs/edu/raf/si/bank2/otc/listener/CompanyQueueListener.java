// package rs.edu.raf.si.bank2.otc.listener;
//
// import org.springframework.jms.annotation.JmsListener;
// import org.springframework.stereotype.Component;
// import rs.edu.raf.si.bank2.otc.dto.CreateCompanyDto;
// import rs.edu.raf.si.bank2.otc.dto.EditCompanyDto;
// import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
// import rs.edu.raf.si.bank2.otc.services.CompanyService;
//
// import javax.jms.JMSException;
// import javax.jms.Message;
//
//
// @Component
// public class CompanyQueueListener {
//
//    private MessageHelper messageHelper;
//    private CompanyService companyService;
//
//    public CompanyQueueListener(MessageHelper messageHelper, CompanyService companyService) {
//        this.messageHelper = messageHelper;
//        this.companyService = companyService;
//    }
//
////    @JmsListener(destination = "${destination.createCompany}", concurrency = "5-10")
////    public void createCompany(Message message) throws JMSException {
////        System.out.println("PORUKA : " + message);
////        CreateCompanyDto companyDtoToCreate = messageHelper.getMessage(message, CreateCompanyDto.class);
////        System.out.println(companyDtoToCreate);
////        Company companyToCreate = new
// Company(companyDtoToCreate.getName(),companyDtoToCreate.getRegistrationNumber(),companyDtoToCreate.getTaxNumber(),companyDtoToCreate.getActivityCode(),companyDtoToCreate.getAddress(),companyDtoToCreate.getContactPersons(),companyDtoToCreate.getBankAccounts());
////        companyService.createCompany(companyToCreate);
////    }
//
////    @JmsListener(destination = "${destination.editCompany}", concurrency = "5-10")
////    public void editCompany(Message message) throws JMSException {
////        EditCompanyDto companyToEdit = messageHelper.getMessage(message, EditCompanyDto.class);
////        System.out.println(companyToEdit);
////        companyService.updateCompany(companyToEdit);
////    }
//
// }
