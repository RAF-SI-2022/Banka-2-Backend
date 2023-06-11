//package rs.edu.raf.si.bank2.client.listener;
//
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//import rs.edu.raf.si.bank2.dto.client.CreateCompanyDto;
//import rs.edu.raf.si.bank2.dto.client.EditCompanyDto;
//import rs.edu.raf.si.bank2.mongodb.models.client.Company;
//import rs.edu.raf.si.bank2.services.client.CompanyService;
//
//import javax.jms.JMSException;
//import javax.jms.Message;
//
//
//@Component
//public class CompanyQueueListener {
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
////        Company companyToCreate = new Company(companyDtoToCreate.getName(),companyDtoToCreate.getRegistrationNumber(),companyDtoToCreate.getTaxNumber(),companyDtoToCreate.getActivityCode(),companyDtoToCreate.getAddress(),companyDtoToCreate.getContactPersons(),companyDtoToCreate.getBankAccounts());
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
//}
